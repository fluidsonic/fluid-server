package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import com.github.fluidsonic.fluid.mongo.*
import io.ktor.application.Application
import io.ktor.application.ApplicationStarting
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import org.bson.codecs.configuration.CodecConfigurationException
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.slf4j.event.Level
import kotlin.reflect.KClass


class Baku internal constructor() {

	class Builder<Context : BakuContext, Transaction : BakuTransaction> internal constructor() {

		private val environment = commandLineEnvironment(arrayOf())
		private var modules: List<BakuModule<in Context, in Transaction>>? = null
		private val providerBasedBSONCodecRegistry = ProviderBasedBSONCodecRegistry<Context>()
		private var service: BakuService<Context, Transaction>? = null

		val bsonCodecRegistry = CodecRegistries.fromRegistries(
			MongoClients.defaultCodecRegistry,
			providerBasedBSONCodecRegistry
		)!!

		val config = environment.config


		internal fun build(): Baku {
			// create engine before monitoring the start event because Netty's subscriptions must be processed first
			val engine = embeddedServer(Netty, environment)
			val subscription = environment.monitor.subscribe(ApplicationStarting) { application ->
				application.apply {
					configureBasics()
					configureModules()
				}
			}

			engine.start()
			subscription.dispose()

			return Baku()
		}


		internal fun configure(assemble: suspend Builder<Context, Transaction>.() -> BakuService<Context, Transaction>) {
			runBlocking {
				service = assemble()
			}
		}


		fun modules(vararg modules: BakuModule<in Context, in Transaction>) {
			check(this.modules == null) { "modules() can only be specified once" }

			this.modules = modules.toList()
		}


		private fun Application.configureBasics() {
			install(CallLogging) {
				level = Level.INFO
			}

			install(DefaultHeaders) {
				header(HttpHeaders.Server, "Baku")
			}

			install(CORS) {
				anyHost()
				exposeHeader(HttpHeaders.WWWAuthenticate)
				header(HttpHeaders.Accept) // https://github.com/ktorio/ktor/issues/939
				header(HttpHeaders.AcceptLanguage) // https://github.com/ktorio/ktor/issues/939
				header(HttpHeaders.Authorization)
				method(HttpMethod.Delete)
				method(HttpMethod.Patch)
			}

			install(XForwardedHeaderSupport)
			install(EncryptionEnforcementFeature)
		}


		private fun Application.configureModules() {
			val modules = (modules ?: error("modules() must be specified")) + StandardModule
			val service = service!!

			val configurations = modules.map { it.configure() }

			val context = runBlocking { service.createContext() }
			val idFactoriesByType = configurations.flatMap { it.idFactories }.associateBy { it.type }

			val bsonCodecProviders: MutableList<BSONCodecProvider<Context>> = mutableListOf()
			bsonCodecProviders += configurations.flatMap { it.bsonCodecProviders }
			bsonCodecProviders += configurations.flatMap { it.idFactories }.map { EntityIdBSONCodec(factory = it) }
			bsonCodecProviders += TypedIdBSONCodec(idFactoryProvider = object : EntityIdFactoryProvider {
				override fun idFactoryForType(type: String) = idFactoriesByType[type]
			})

			val jsonCodecProviders: MutableList<JSONCodecProvider<Transaction>> = mutableListOf()
			jsonCodecProviders += configurations.flatMap { it.jsonCodecProviders }
			jsonCodecProviders += configurations.flatMap { it.idFactories }.map { EntityIdJSONCodec(factory = it) }
			jsonCodecProviders += JSONCodecProvider.extended
			val jsonCodecProvider = JSONCodecProvider(jsonCodecProviders)

			providerBasedBSONCodecRegistry.context = context
			providerBasedBSONCodecRegistry.provider = BSONCodecProvider.of(bsonCodecProviders)
			providerBasedBSONCodecRegistry.rootRegistry = bsonCodecRegistry

			val entityResolverSources: MutableMap<KClass<out EntityId>, BakuModule<*, *>> = mutableMapOf()
			val entityResolvers: MutableMap<KClass<out EntityId>, suspend Transaction.(ids: Set<EntityId>) -> ReceiveChannel<Entity>> =
				mutableMapOf()

			for (configuration in configurations) {
				for ((idClass, entityResolver) in configuration.entities.resolvers) {
					val previousModule = entityResolverSources.putIfAbsent(idClass, configuration.module)
					if (previousModule != null) {
						error("Cannot add entity resolver for $idClass of ${configuration.module} because $previousModule already provides one")
					}

					entityResolvers[idClass] = entityResolver
				}
			}

			configurations.forEach { it.customConfigurations.forEach { it() } }

			install(BakuTransactionFeature(
				service = service,
				context = context
			))

			install(BakuCommandFailureFeature)

			install(BakuCommandRequestFeature(
				jsonCodecProvider = jsonCodecProvider
			))

			install(BakuCommandResponseFeature(
				additionalEncodings = configurations.flatMap { it.additionalResponseEncodings },
				codecProvider = jsonCodecProvider,
				entityResolver = EntityResolver(resolvers = entityResolvers)
			))

			var topRouteBuilder: Route.() -> Unit = {
				for (configuration in configurations) {
					for (routingConfiguration in configuration.routeConfigurations) {
						routingConfiguration()
					}
				}
			}
			configurations.forEach {
				it.routeWrappers.forEach {
					val next = topRouteBuilder
					topRouteBuilder = { it(next) }
				}
			}

			routing {
				topRouteBuilder()
			}

			val commandNames = mutableSetOf<BakuCommandName>()
			val commandHandlers: MutableMap<BakuCommandFactory<*, *, *>, BakuCommandHandler<*, *, *>> = hashMapOf()
			for (configuration in configurations) {
				for (handler in configuration.commands.handlers) {
					val name = handler.factory.name
					if (commandNames.contains(name)) {
						error("Multiple commands have the same name: $name")
					}
					else {
						commandNames.add(name)
					}

					if (commandHandlers.putIfAbsent(handler.factory, handler) != null) {
						error("Cannot register multiple handlers for command factory ${handler.factory}")
					}
				}
			}

			for (configuration in configurations) {
				for (route in configuration.commandRoutes) {
					val factory = route.factory

					@Suppress("UNCHECKED_CAST")
					val handler = commandHandlers[factory]
						as BakuCommandHandler<BakuTransaction, BakuCommand, Any>?
						?: error("No handler registered for command factory $factory")

					route.route.handle {
						val request = call.request.pipeline.execute(call, ApplicationReceiveRequest(
							type = BakuCommand::class,
							value = factory
						)).value as BakuCommandRequest

						call.respond(BakuCommandResponse(
							factory = factory,
							result = handler.run { transaction.handler() }(request.command)
						))
					}
				}
			}

			runBlocking {
				service.onStart(context = context) // TODO we could make BakuContext and BakuService one thing
			}
		}
	}


	private class ProviderBasedBSONCodecRegistry<Context : BakuContext> : CodecRegistry {

		lateinit var context: Context
		lateinit var provider: BSONCodecProvider<Context>
		lateinit var rootRegistry: CodecRegistry


		override fun <Value : Any> get(clazz: Class<Value>): BSONCodec<Value, Context> {
			val codec = provider.codecForClass(clazz.kotlin) ?: throw CodecConfigurationException("No BSON codec provided for $clazz")
			if (codec is AbstractBSONCodec<Value, Context>) {
				codec.configure(context = context, rootRegistry = rootRegistry)
			}

			return codec
		}
	}
}


fun <Context : BakuContext, Transaction : BakuTransaction> baku(
	assemble: suspend Baku.Builder<Context, Transaction>.() -> BakuService<Context, Transaction>
) {
	Baku.Builder<Context, Transaction>().apply { configure(assemble) }.build()
}

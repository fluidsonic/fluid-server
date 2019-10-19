package io.fluidsonic.server

import io.fluidsonic.json.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.io.*
import kotlinx.coroutines.io.jvm.javaio.*
import java.nio.charset.*


internal class BakuCommandRequestFeature<Transaction : BakuTransaction>(
	private val jsonCodecProvider: JsonCodecProvider<Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: command request feature")


	private fun PipelineContext<ApplicationReceiveRequest, ApplicationCall>.resolveBody(factory: BakuCommandFactory<Transaction, *, *>): ByteReadChannel {
		val contentType = call.request.contentType().withoutParameters()

		if (contentType.match(ContentType.Application.Json))
			return call.request.receiveChannel()

		if (methodsAllowedForQueryParameterBody.contains(call.request.httpMethod))
			call.request.queryParameters["body"]?.let { return ByteReadChannel(it, Charset.defaultCharset()) }

		if (contentType.match(ContentType.Any) && (factory is BakuCommandFactory.Empty<*, *, *> || !call.parameters.isEmpty()))
			return ByteReadChannel(text = "{}", charset = Charsets.UTF_8)

		throw BakuCommandFailure(
			code = "invalidRequest",
			developerMessage = "Expected content of type '${ContentType.Application.Json}'",
			userMessage = BakuCommandFailure.genericUserMessage
		)
	}


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.receivePipeline.intercept(ApplicationReceivePipeline.Transform) { subject ->
			val factory = subject.value as? BakuCommandFactory<Transaction, *, *>
				?: return@intercept

			val transaction = transaction as Transaction
			val body = resolveBody(factory = factory)

			proceedWith(ApplicationReceiveRequest(
				type = subject.type,
				value = parseCommandRequest(
					transaction = transaction,
					body = body,
					charset = call.request.contentCharset() ?: Charsets.UTF_8,
					parameters = call.parameters,
					factory = factory
				)
			))
		}
	}


	private fun parseCommandRequest(
		transaction: Transaction,
		body: ByteReadChannel,
		charset: Charset,
		parameters: Parameters,
		factory: BakuCommandFactory<Transaction, *, *>
	): BakuCommandRequest {
		val reader = JsonReader.build(body.toInputStream().reader(charset = charset)) // FIXME blocking

		try {
			var command: BakuCommand? = null

			reader.readFromMapByElementValue { key ->
				when (key) {
					"command" -> command = readCommand(
						factory = factory,
						parameters = parameters,
						transaction = transaction
					)
					else -> skipValue()
				}
			}

			if (command == null) {
				command = if (factory is BakuCommandFactory.Empty<Transaction, *, *>) {
					factory.createCommand()
				}
				else {
					JsonReader.build("""{"command":{}}""").run {
						readFromMap {
							readMapKey()
							readCommand(
								factory = factory,
								parameters = parameters,
								transaction = transaction
							)
						}
					}
				}
			}

			return BakuCommandRequest(
				command = command!!
			)
		}
		catch (e: JsonException) {
			if (e is JsonException.Schema || e is JsonException.Syntax) {
				throw BakuCommandFailure(
					code = "invalidRequest",
					developerMessage = e.message,
					userMessage = BakuCommandFailure.genericUserMessage,
					cause = e
				)
			}

			throw e
		}
	}


	private fun JsonReader.readCommand(
		factory: BakuCommandFactory<Transaction, *, *>,
		parameters: Parameters,
		transaction: Transaction
	): BakuCommand {
		var commandReader = this
		if (!parameters.isEmpty()) {
			commandReader = PropertyInjectingJsonReader(
				properties = parameters.toMap().mapValues { it.value.single() },
				source = commandReader
			)
		}

		val decoder = JsonDecoder.builder(transaction)
			.codecs(jsonCodecProvider)
			.source(commandReader)
			.build()

		return try {
			factory.run { decoder.decodeCommand() }
		}
		catch (e: JsonException) {
			e.addSuppressed(JsonException.Parsing("… when decoding command '${factory.name}' using ${factory::class.qualifiedName}"))
			throw e
		}
	}


	companion object {

		private val methodsAllowedForQueryParameterBody = setOf(HttpMethod.Get, HttpMethod.Head)
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.readFromMap
import com.github.fluidsonic.fluid.json.readFromMapByElementValue
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.request.contentType
import io.ktor.request.httpMethod
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import java.nio.charset.Charset


internal class BakuCommandRequestFeature<Transaction : BakuTransaction>(
	private val jsonCodecProvider: JSONCodecProvider<Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: command request feature")


	private fun PipelineContext<ApplicationReceiveRequest, ApplicationCall>.resolveBody(factory: BakuCommandFactory<Transaction, *, *>): ByteReadChannel {
		val contentType = call.request.contentType().withoutParameters()

		if (contentType.match(ContentType.Application.Json))
			return call.request.receiveChannel()

		if (methodsAllowedForQueryParameterBody.contains(call.request.httpMethod))
			call.request.queryParameters["body"]?.let { return ByteReadChannel(it, Charset.defaultCharset()) }

		if (contentType.match(ContentType.Any) && (factory is BakuCommandFactory.Empty<*, *, *> || !call.parameters.isEmpty()))
			ByteReadChannel(text = "{}", charset = Charsets.UTF_8)

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
				?: throw BakuCommandFailure(code = "fixme", developerMessage = "FIXME", userMessage = BakuCommandFailure.genericUserMessage) // FIXME

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
		val reader = JSONReader.build(body.toInputStream().reader(charset = charset))

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
					JSONReader.build("""{"command":{}}""").run {
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
		catch (e: JSONException) {
			if (e is JSONException.Schema || e is JSONException.Syntax) {
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


	private fun JSONReader.readCommand(
		factory: BakuCommandFactory<Transaction, *, *>,
		parameters: Parameters,
		transaction: Transaction
	): BakuCommand {
		var commandReader = this
		if (!parameters.isEmpty()) {
			commandReader = PropertyInjectingJSONReader(
				properties = parameters.toMap().mapValues { it.value.single() },
				source = commandReader
			)
		}

		val decoder = JSONDecoder.builder(transaction)
			.codecs(jsonCodecProvider)
			.source(commandReader)
			.build()

		return try {
			factory.run { decoder.decodeCommand() }
		}
		catch (e: JSONException) {
			e.addSuppressed(JSONException.Parsing("… when decoding command '${factory.name}' using ${factory::class.qualifiedName}"))
			throw e
		}
	}


	companion object {

		private val methodsAllowedForQueryParameterBody = setOf(HttpMethod.Get, HttpMethod.Head)
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.readFromMapByElementValue
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.request.contentType
import io.ktor.util.AttributeKey
import io.ktor.util.toMap
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import org.slf4j.LoggerFactory
import java.nio.charset.Charset


internal class BakuCommandRequestFeature<Transaction : BakuTransaction>(
	private val jsonCodecProvider: JSONCodecProvider<Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: command request feature")


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.receivePipeline.intercept(ApplicationReceivePipeline.Transform) { subject ->
			val factory = subject.value as? BakuCommandFactory<Transaction, *, *>
				?: throw BakuCommandFailure(code = "fixme", developerMessage = "FIXME", userMessage = BakuCommandFailure.genericUserMessage) // FIXME

			val contentType = call.request.contentType().withoutParameters()

			val log = LoggerFactory.getLogger("BAKU")
			log.info("CALL: $call")
			log.info("REQ: ${call.request}")
			log.info("HEADERS: ${call.request.headers}")
			log.info("RECEIVEC: ${call.request.receiveChannel()}")

			val transaction = transaction as Transaction
			val body = when {
				contentType.match(ContentType.Application.Json) ->
					call.request.receiveChannel()

				contentType.match(ContentType.Any) ->
					if (factory is BakuCommandFactory.Empty<*, *, *> || !call.parameters.isEmpty())
						ByteReadChannel(text = "{}", charset = Charsets.UTF_8)
					else
						throw BakuCommandFailure(
							code = "invalidRequest",
							developerMessage = "Expected content of type '${ContentType.Application.Json}'",
							userMessage = BakuCommandFailure.genericUserMessage
						)

				else ->
					throw BakuCommandFailure(
						code = "unsupportedContentType",
						developerMessage = "Expected content type '${ContentType.Application.Json}' but got '$contentType'",
						userMessage = BakuCommandFailure.genericUserMessage
					)
			}

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

			command = command ?: if (factory is BakuCommandFactory.Empty<Transaction, *, *>) {
				factory.createCommand()
			}
			else {
				JSONReader.build("{}").readCommand(
					factory = factory,
					parameters = parameters,
					transaction = transaction
				)
			}

			return BakuCommandRequest(
				command = command!!
			)
		}
		catch (e: JSONException) {
			throw BakuCommandFailure( // FIXME
				code = "invalidRequest",
				developerMessage = e.message ?: "Unable to process JSON",
				userMessage = BakuCommandFailure.genericUserMessage,
				cause = e
			)
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

		return factory.run { decoder.decodeCommand() }
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.writeIntoMap
import com.github.fluidsonic.fluid.json.writeMapElement
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.response.ApplicationSendPipeline
import io.ktor.util.AttributeKey
import java.io.StringWriter


internal class BakuCommandResponseFeature<Transaction : BakuTransaction>(
	private val additionalEncodings: List<JSONEncoder<Transaction>.() -> Unit>,
	private val codecProvider: JSONCodecProvider<Transaction>,
	private val entityResolver: EntityResolver<Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: command response feature")


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.sendPipeline.intercept(ApplicationSendPipeline.Render) { subject ->
			subject !is OutgoingContent
				|| return@intercept

			val response = subject as? BakuCommandResponse
				?: throw BakuCommandFailure(code = "fixme", developerMessage = "FIXME", userMessage = BakuCommandFailure.genericUserMessage) // FIXME

			val transaction = transaction as Transaction

			proceedWith(serializeCommandResponse(
				response = response,
				transaction = transaction
			))
		}
	}


	private suspend fun serializeCommandResponse(
		response: BakuCommandResponse,
		transaction: Transaction
	): OutgoingContent {
		@Suppress("UNCHECKED_CAST")
		val factory = response.factory as BakuCommandFactory<Transaction, *, Any>
		val writer = StringWriter()

		BakuEntityResolvingJSONEncoder(
			codecProvider = codecProvider,
			context = transaction,
			entityResolver = entityResolver,
			writer = writer
		).apply {
			writeIntoMap {
				additionalEncodings.forEach { it() }
				writeMapElement("result") {
					factory.run { encodeResult(response.result) }
				}
				writeMapElement("entities") { writeEntities() }
				writeMapElement("status", string = "success")
			}
		}

		return TextContent(
			text = writer.toString(),
			contentType = ContentType.Application.Json.withCharset(Charsets.UTF_8),
			status = null
		)
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.client.utils.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.response.ApplicationSendPipeline
import io.ktor.response.header
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

			call.response.header(HttpHeaders.CacheControl, CacheControl.NO_CACHE)

			proceedWith(serializeCommandResponse(
				response = response,
				transaction = transaction as Transaction
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
			writeMapStart()

			additionalEncodings.forEach { it() }

			writeMapElement("result") {
				try {
					factory.run { encodeResult(response.result) }
				}
				catch (e: JSONException) {
					e.addSuppressed(JSONException.Serialization("… when encoding result of command '${factory.name}' using ${factory::class.qualifiedName}"))
					throw e
				}
			}

			writeMapKey("entities")
			writeEntities()

			writeMapElement("status", string = "success")

			writeMapEnd()
		}

		return TextContent(
			text = writer.toString(),
			contentType = ContentType.Application.Json.withCharset(Charsets.UTF_8),
			status = null
		)
	}
}

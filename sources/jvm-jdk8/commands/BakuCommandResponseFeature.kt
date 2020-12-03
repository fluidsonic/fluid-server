package io.fluidsonic.server

import io.fluidsonic.json.*
import io.ktor.application.*
import io.ktor.client.utils.CacheControl
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.util.*
import kotlinx.coroutines.*
import java.io.*


internal class BakuCommandResponseFeature<Transaction : BakuTransaction>(
	private val additionalEncodings: List<JsonEncoder<Transaction>.() -> Unit>,
	private val codecProvider: JsonCodecProvider<Transaction>,
	private val entityResolver: EntityResolver<Transaction>
) : ApplicationFeature<ApplicationCallPipeline, Unit, Unit> {

	override val key = AttributeKey<Unit>("Baku: command response feature")


	@Suppress("UNCHECKED_CAST")
	override fun install(pipeline: ApplicationCallPipeline, configure: Unit.() -> Unit) {
		Unit.configure()

		pipeline.sendPipeline.intercept(ApplicationSendPipeline.Render) { subject ->
			val response = subject as? BakuCommandResponse
				?: return@intercept

			call.response.header(HttpHeaders.CacheControl, CacheControl.NO_CACHE)

			val output = withContext(Dispatchers.Default) {
				serializeCommandResponse(
					response = response,
					transaction = transaction as Transaction
				)
			}

			proceedWith(output)
		}
	}


	private suspend fun serializeCommandResponse(
		response: BakuCommandResponse,
		transaction: Transaction
	): OutgoingContent {
		@Suppress("UNCHECKED_CAST")
		val factory = response.factory as BakuCommandFactory<Transaction, *, Any>
		val writer = StringWriter()

		BakuEntityResolvingJsonEncoder(
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
				catch (e: JsonException) {
					e.addSuppressed(JsonException.Serialization("… when encoding result of command '${factory.name}' using ${factory::class.qualifiedName}"))
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

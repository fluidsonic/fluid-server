package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.JSONWriter
import com.github.fluidsonic.fluid.json.withErrorChecking
import com.github.fluidsonic.fluid.json.writeIntoMap
import com.github.fluidsonic.fluid.json.writeMapElement
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import kotlinx.coroutines.channels.associateByTo
import org.slf4j.LoggerFactory
import java.io.StringWriter
import java.io.Writer


internal class APIResponseProcessor<Transaction : BakuTransaction>(
	private val additionalEncodings: List<JSONEncoder<Transaction>.() -> Unit>,
	private val codecProvider: JSONCodecProvider<Transaction>,
	private val entityResolver: EntityResolver<Transaction>
) {

	private val cachedEntities: MutableMap<EntityId, Entity> = hashMapOf()
	private val entityReferences: MutableSet<EntityId> = hashSetOf()
	private var isProcessed = false


	suspend fun process(payload: Any, transaction: Transaction): OutgoingContent {
		check(!isProcessed) { "Cannot process response multiple times" }
		isProcessed = true

		val writer = StringWriter()

		if (payload is Payload && payload != Payload.empty) {
			JSONEncoder.builder(transaction)
				.codecs(codecProvider)
				.destination(writer)
				.build()
				.writeValue(payload.rawValue)
		}
		else {
			Encoder(
				codecProvider = codecProvider,
				context = transaction,
				writer = writer
			).apply {
				writeIntoMap {
					additionalEncodings.forEach { it() }

					if (payload !== Payload.empty) {
						writeMapElement("payload", value = payload)
						writeMapElement("entities") { writeEntities() }
					}
					writeMapElement("status", string = "success")
				}
			}
		}

		return TextContent(
			text = writer.toString(),
			contentType = ContentType.Application.Json.withCharset(Charsets.UTF_8),
			status = null
		)
	}


	private suspend fun Encoder.writeEntities() {
		writeMapStart()

		val resolvedIds = hashSetOf<EntityId>()
		val unresolvedIds = mutableListOf<EntityId>()

		var runCount = 0

		var idsToResolve: Set<EntityId> = entityReferences
		while (idsToResolve.isNotEmpty()) {
			runCount += 1
			resolvedIds += idsToResolve

			val resolvedEntitiesById = entityResolver
				.resolve(ids = idsToResolve, transaction = context)
				.associateByTo(hashMapOf()) { it.id }

			for (id in idsToResolve) {
				if (!resolvedEntitiesById.containsKey(id)) {
					val cachedEntity = cachedEntities[id]
					if (cachedEntity != null) {
						resolvedEntitiesById[id] = cachedEntity
					}
					else {
						unresolvedIds.add(id)
					}
				}
			}

			for (entity in resolvedEntitiesById.values) {
				writeValue(entity.id, collect = false)
				writeValue(entity)
			}

			idsToResolve = entityReferences - resolvedIds
		}

		if (!unresolvedIds.isEmpty()) {
			log.warn("Response references entities which cannot be found: " + unresolvedIds.joinToString(", "))
		}

		if (runCount >= 3) {
			log.debug("Response serialization took $runCount runs! Consider flattening the entity hierarchy.")
		}

		writeMapEnd()
	}


	companion object {

		private val log = LoggerFactory.getLogger(APIResponseProcessor::class.java)!!
	}


	private inner class Encoder(
		private val codecProvider: JSONCodecProvider<Transaction>,
		override val context: Transaction,
		writer: Writer
	) : JSONEncoder<Transaction>, JSONWriter by JSONWriter.build(writer) {

		fun writeValue(value: Any, collect: Boolean) {
			withErrorChecking {
				if (collect && value is EntityId) {
					entityReferences += value
				}

				codecProvider.encoderCodecForClass(value::class)
					?.encode(value = value, encoder = this)
					?: throw JSONException("no encoder codec registered for ${value::class}: $value")
			}
		}


		override fun writeValue(value: Any) =
			writeValue(value, collect = true)
	}
}

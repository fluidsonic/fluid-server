package io.fluidsonic.server

import io.fluidsonic.json.*
import kotlinx.coroutines.flow.*
import org.slf4j.*
import java.io.*


internal class BakuEntityResolvingJsonEncoder<Transaction : BakuTransaction>(
	private val codecProvider: JsonCodecProvider<Transaction>,
	override val context: Transaction,
	private val entityResolver: EntityResolver<Transaction>,
	writer: Writer
) : JsonEncoder<Transaction>, JsonWriter by JsonWriter.build(writer) {

	private val cachedEntities: MutableMap<EntityId, Entity> = hashMapOf()
	private val entityReferences: MutableSet<EntityId> = hashSetOf()


	suspend fun writeEntities() {
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
				.toList() // https://github.com/Kotlin/kotlinx.coroutines/issues/1541
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

		if (unresolvedIds.isNotEmpty()) {
			log.warn("Response references entities which cannot be found: " + unresolvedIds.joinToString(", "))
		}

		if (runCount >= 4) {
			log.debug("Response serialization took $runCount runs! Consider flattening the entity hierarchy.")
		}

		writeMapEnd()
	}


	@Suppress("UNCHECKED_CAST")
	private fun writeValue(value: Any, collect: Boolean) {
		withErrorChecking {
			if (collect && value is EntityId) {
				entityReferences += value
			}

			(codecProvider.encoderCodecForClass(value::class) as JsonEncoderCodec<Any, Transaction>?)
				?.run {
					try {
						isolateValueWrite {
							encode(value = value)
						}
					}
					catch (e: JsonException) {
						// TODO remove .java once KT-28418 is fixed
						e.addSuppressed(JsonException.Serialization("… when encoding value of ${value::class} using ${this::class.java.name}: $value"))
						throw e
					}
				}
				?: throw JsonException.Serialization(
					message = "No encoder codec registered for ${value::class}: $value",
					path = path
				)
		}
	}


	override fun writeValue(value: Any) =
		writeValue(value, collect = true)


	companion object {

		private val log = LoggerFactory.getLogger(BakuEntityResolvingJsonEncoder::class.java)!!
	}
}

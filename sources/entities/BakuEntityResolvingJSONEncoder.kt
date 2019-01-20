package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.JSONWriter
import com.github.fluidsonic.fluid.json.withErrorChecking
import kotlinx.coroutines.channels.associateByTo
import org.slf4j.LoggerFactory
import java.io.Writer


internal class BakuEntityResolvingJSONEncoder<Transaction : BakuTransaction>(
	private val codecProvider: JSONCodecProvider<Transaction>,
	override val context: Transaction,
	private val entityResolver: EntityResolver<Transaction>,
	writer: Writer
) : JSONEncoder<Transaction>, JSONWriter by JSONWriter.build(writer) {

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

		if (runCount >= 4) {
			log.debug("Response serialization took $runCount runs! Consider flattening the entity hierarchy.")
		}

		writeMapEnd()
	}


	private fun writeValue(value: Any, collect: Boolean) {
		withErrorChecking {
			if (collect && value is EntityId) {
				entityReferences += value
			}

			codecProvider.encoderCodecForClass(value::class)
				?.run { encode(value = value) }
				?: throw JSONException("no encoder codec registered for ${value::class}: $value")
		}
	}


	override fun writeValue(value: Any) =
		writeValue(value, collect = true)


	companion object {

		private val log = LoggerFactory.getLogger(BakuEntityResolvingJSONEncoder::class.java)!!
	}
}

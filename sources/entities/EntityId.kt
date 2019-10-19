package io.fluidsonic.server

import org.bson.*
import org.bson.types.*
import kotlin.reflect.*


interface EntityId {

	val factory: Factory<*>


	interface Factory<Id : EntityId> {

		val idClass: KClass<Id>

		val type: String

		fun parse(string: String): Id?

		fun parseWithoutType(string: String): Id?

		fun BsonReader.readIdValue(): Id

		fun BsonWriter.writeIdValue(id: Id)

		fun Id.serialize(): String

		fun Id.serializeWithoutType(): String
	}


	interface ObjectIdBased : EntityId {

		val raw: ObjectId


		abstract class Factory<Id : ObjectIdBased>(
			final override val type: String,
			final override val idClass: KClass<Id>,
			private val constructor: (raw: ObjectId) -> Id
		) : EntityId.Factory<Id> {

			private val prefix = "$type/"


			final override fun parse(string: String) =
				string
					.takeIf { it.startsWith(prefix) || !it.contains('/') }
					?.removePrefix(prefix)
					?.let { parseWithoutType(it) }


			final override fun parseWithoutType(string: String) =
				string
					.let {
						try {
							ObjectId(it)
						}
						catch (_: Exception) {
							null
						}
					}
					?.let(constructor)


			final override fun BsonReader.readIdValue() =
				constructor(readObjectId())


			final override fun BsonWriter.writeIdValue(id: Id) =
				writeObjectId(id.raw)


			final override fun Id.serialize() =
				prefix + serializeWithoutType()


			override fun Id.serializeWithoutType() =
				raw.toHexString()!!
		}
	}


	interface StringBased : EntityId {

		val raw: String


		abstract class Factory<Id : StringBased>(
			final override val type: String,
			final override val idClass: KClass<Id>,
			private val constructor: (raw: String) -> Id
		) : EntityId.Factory<Id> {

			private val prefix = "$type/"


			final override fun parse(string: String) =
				string
					.takeIf { it.startsWith(prefix) || !it.contains('/') }
					?.removePrefix(prefix)
					?.let { parseWithoutType(it) }


			final override fun parseWithoutType(string: String) =
				constructor(string)


			final override fun BsonReader.readIdValue() =
				constructor(readString())


			final override fun BsonWriter.writeIdValue(id: Id) =
				writeString(id.raw)


			final override fun Id.serialize() =
				prefix + serializeWithoutType()


			override fun Id.serializeWithoutType() =
				raw
		}
	}
}


val EntityId.typed
	get() = TypedId(this)


@Suppress("UNCHECKED_CAST")
fun EntityId.toStringWithoutType() =
	(factory as EntityId.Factory<EntityId>).run { serializeWithoutType() }


fun <Id : EntityId> EntityId.Factory<Id>.serialize(id: Id) =
	id.run { serialize() }

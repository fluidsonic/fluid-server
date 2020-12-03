package io.fluidsonic.server

import io.fluidsonic.json.*


internal class EntityIdJsonCodec<Id : EntityId>(
	private val factory: EntityId.Factory<Id>
) : JsonCodec<Id, JsonCodingContext> {

	override val decodableType = jsonCodingType(factory.idClass)


	override fun JsonDecoder<JsonCodingContext>.decode(valueType: JsonCodingType<Id>) =
		readString().let { string ->
			factory.parse(string) ?: invalidValueError("'$string' is not a valid '${factory.type}' ID")
		}


	override fun JsonEncoder<JsonCodingContext>.encode(value: Id) {
		writeString(factory.serialize(value))
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.invalidValueError
import com.github.fluidsonic.fluid.json.jsonCodingType


internal class EntityIdJSONCodec<Id : EntityId>(
	private val factory: EntityId.Factory<Id>
) : JSONCodec<Id, JSONCodingContext> {

	override val decodableType = jsonCodingType(factory.idClass)


	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Id>) =
		readString().let { string ->
			factory.parse(string) ?: invalidValueError("'$string' is not a valid '${factory.type}' ID")
		}


	override fun JSONEncoder<JSONCodingContext>.encode(value: Id) {
		writeString(factory.serialize(value))
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object LastNameJSONCodec : AbstractJSONCodec<LastName, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in LastName>) =
		LastName(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: LastName) {
		writeString(value.raw)
	}
}

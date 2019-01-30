package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object FullNameJSONCodec : AbstractJSONCodec<FullName, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in FullName>) =
		FullName(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: FullName) {
		writeString(value.raw)
	}
}

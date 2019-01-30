package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object CompanyNameJSONCodec : AbstractJSONCodec<CompanyName, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in CompanyName>) =
		CompanyName(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: CompanyName) {
		writeString(value.raw)
	}
}

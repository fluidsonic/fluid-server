package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object CityNameJSONCodec : AbstractJSONCodec<CityName, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in CityName>) =
		CityName(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: CityName) {
		writeString(value.raw)
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


object CityNameJSONCodec : AbstractJSONCodec<CityName, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in CityName>, decoder: JSONDecoder<JSONCodingContext>) =
		CityName(decoder.readString())


	override fun encode(value: CityName, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

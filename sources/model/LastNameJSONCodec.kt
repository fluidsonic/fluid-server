package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


object LastNameJSONCodec : AbstractJSONCodec<LastName, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in LastName>, decoder: JSONDecoder<JSONCodingContext>) =
		LastName(decoder.readString())


	override fun encode(value: LastName, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

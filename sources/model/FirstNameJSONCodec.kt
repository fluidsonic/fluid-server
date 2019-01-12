package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


object FirstNameJSONCodec : AbstractJSONCodec<FirstName, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in FirstName>, decoder: JSONDecoder<JSONCodingContext>) =
		FirstName(decoder.readString())


	override fun encode(value: FirstName, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

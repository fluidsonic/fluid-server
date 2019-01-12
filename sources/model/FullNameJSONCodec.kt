package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


internal object FullNameJSONCodec : AbstractJSONCodec<FullName, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in FullName>, decoder: JSONDecoder<JSONCodingContext>) =
		FullName(decoder.readString())


	override fun encode(value: FullName, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

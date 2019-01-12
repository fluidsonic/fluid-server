package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


object CompanyNameJSONCodec : AbstractJSONCodec<CompanyName, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in CompanyName>, decoder: JSONDecoder<JSONCodingContext>) =
		CompanyName(decoder.readString())


	override fun encode(value: CompanyName, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

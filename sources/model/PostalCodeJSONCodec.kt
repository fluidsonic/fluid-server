package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


object PostalCodeJSONCodec : AbstractJSONCodec<PostalCode, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in PostalCode>, decoder: JSONDecoder<JSONCodingContext>) =
		PostalCode(decoder.readString())


	override fun encode(value: PostalCode, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

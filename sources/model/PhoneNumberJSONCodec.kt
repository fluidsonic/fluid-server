package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


internal object PhoneNumberJSONCodec : AbstractJSONCodec<PhoneNumber, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in PhoneNumber>, decoder: JSONDecoder<JSONCodingContext>) =
		PhoneNumber(decoder.readString())


	override fun encode(value: PhoneNumber, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

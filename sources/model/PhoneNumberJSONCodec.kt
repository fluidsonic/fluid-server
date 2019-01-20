package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


internal object PhoneNumberJSONCodec : AbstractJSONCodec<PhoneNumber, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in PhoneNumber>) =
		PhoneNumber(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: PhoneNumber) {
		writeString(value.raw)
	}
}

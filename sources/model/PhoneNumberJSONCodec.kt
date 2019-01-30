package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object PhoneNumberJSONCodec : AbstractJSONCodec<PhoneNumber, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in PhoneNumber>) =
		PhoneNumber(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: PhoneNumber) {
		writeString(value.raw)
	}
}

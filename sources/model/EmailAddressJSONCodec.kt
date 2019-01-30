package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object EmailAddressJSONCodec : AbstractJSONCodec<EmailAddress, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in EmailAddress>) =
		EmailAddress(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: EmailAddress) {
		writeString(value.raw)
	}
}


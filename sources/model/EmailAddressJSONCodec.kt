package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


internal object EmailAddressJSONCodec : AbstractJSONCodec<EmailAddress, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in EmailAddress>) =
		EmailAddress(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: EmailAddress) {
		writeString(value.raw)
	}
}


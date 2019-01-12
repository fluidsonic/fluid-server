package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder


internal object EmailAddressJSONCodec : AbstractJSONCodec<EmailAddress, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in EmailAddress>, decoder: JSONDecoder<JSONCodingContext>) =
		EmailAddress(decoder.readString())


	override fun encode(value: EmailAddress, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}


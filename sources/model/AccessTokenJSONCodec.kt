package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONEncoderCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONEncoder


object AccessTokenJSONCodec : AbstractJSONEncoderCodec<AccessToken, JSONCodingContext>() {

	override fun encode(value: AccessToken, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONEncoderCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONEncoder


object RefreshTokenJSONCodec : AbstractJSONEncoderCodec<RefreshToken, JSONCodingContext>() {

	override fun encode(value: RefreshToken, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.raw)
	}
}

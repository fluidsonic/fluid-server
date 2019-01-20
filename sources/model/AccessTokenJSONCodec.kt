package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONEncoderCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONEncoder


internal object AccessTokenJSONCodec : AbstractJSONEncoderCodec<AccessToken, JSONCodingContext>() {

	override fun JSONEncoder<JSONCodingContext>.encode(value: AccessToken) {
		writeString(value.raw)
	}
}

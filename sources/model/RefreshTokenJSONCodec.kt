package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object RefreshTokenJSONCodec : AbstractJSONCodec<RefreshToken, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in RefreshToken>) =
		RefreshToken(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: RefreshToken) {
		writeString(value.raw)
	}
}

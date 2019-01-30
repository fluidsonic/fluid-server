package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object AccessTokenJSONCodec : AbstractJSONCodec<AccessToken, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in AccessToken>) =
		AccessToken(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: AccessToken) {
		writeString(value.raw)
	}
}

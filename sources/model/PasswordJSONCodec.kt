package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object PasswordJSONCodec : AbstractJSONDecoderCodec<Password, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Password>) =
		Password(readString())
}

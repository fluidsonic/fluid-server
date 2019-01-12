package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONDecoderCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder


internal object PasswordJSONCodec : AbstractJSONDecoderCodec<Password, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in Password>, decoder: JSONDecoder<JSONCodingContext>) =
		Password(decoder.readString())
}

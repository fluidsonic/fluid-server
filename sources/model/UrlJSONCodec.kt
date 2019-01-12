package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import io.ktor.http.Url


object UrlJSONCodec : AbstractJSONCodec<Url, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in Url>, decoder: JSONDecoder<JSONCodingContext>) =
		Url(decoder.readString())


	override fun encode(value: Url, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.toString())
	}
}

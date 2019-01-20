package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import io.ktor.http.Url


internal object UrlJSONCodec : AbstractJSONCodec<Url, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Url>) =
		Url(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: Url) {
		writeString(value.toString())
	}
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import io.ktor.http.Url


internal object UrlJSONCodec : AbstractJSONCodec<Url, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Url>) =
		Url(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: Url) {
		writeString(value.toString())
	}
}

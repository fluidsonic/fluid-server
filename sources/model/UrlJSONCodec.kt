package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import io.ktor.http.*


internal object UrlJSONCodec : AbstractJSONCodec<Url, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<Url>) =
		Url(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: Url) {
		writeString(value.toString())
	}
}

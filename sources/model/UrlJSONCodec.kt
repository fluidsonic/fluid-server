package io.fluidsonic.server

import io.fluidsonic.json.*
import io.ktor.http.*


internal object UrlJsonCodec : AbstractJsonCodec<Url, JsonCodingContext>() {

	override fun JsonDecoder<JsonCodingContext>.decode(valueType: JsonCodingType<Url>) =
		Url(readString())


	override fun JsonEncoder<JsonCodingContext>.encode(value: Url) {
		writeString(value.toString())
	}
}

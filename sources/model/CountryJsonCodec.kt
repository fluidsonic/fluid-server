package io.fluidsonic.server

import io.fluidsonic.json.*
import io.fluidsonic.stdlib.*


internal object CountryJsonCodec : AbstractJsonCodec<Country, JsonCodingContext>() {

	override fun JsonDecoder<JsonCodingContext>.decode(valueType: JsonCodingType<Country>) =
		readString().let { code ->
			Country.byCode(code) ?: invalidValueError("'$code' is not a valid IANA country code")
		}


	override fun JsonEncoder<JsonCodingContext>.encode(value: Country) {
		writeString(value.code)
	}
}

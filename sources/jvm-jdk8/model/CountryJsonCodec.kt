package io.fluidsonic.server

import io.fluidsonic.country.*
import io.fluidsonic.json.*


internal object CountryJsonCodec : AbstractJsonCodec<Country, JsonCodingContext>() {

	override fun JsonDecoder<JsonCodingContext>.decode(valueType: JsonCodingType<Country>) =
		readString().let { code ->
			Country.forCodeOrNull(code) ?: invalidValueError("'$code' is not a valid IANA country code")
		}


	override fun JsonEncoder<JsonCodingContext>.encode(value: Country) {
		writeString(value.code.toString())
	}
}

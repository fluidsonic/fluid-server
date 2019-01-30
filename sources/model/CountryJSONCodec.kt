package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object CountryJSONCodec : AbstractJSONCodec<Country, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Country>) =
		readString().let { code ->
			Country.byCode(code) ?: invalidValueError("'$code' is not a valid IANA country code")
		}


	override fun JSONEncoder<JSONCodingContext>.encode(value: Country) {
		writeString(value.code)
	}
}

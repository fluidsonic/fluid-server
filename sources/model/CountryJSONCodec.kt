package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.invalidValueError


internal object CountryJSONCodec : AbstractJSONCodec<Country, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Country>) =
		readString().let { code ->
			Country.byCode(code) ?: invalidValueError("'$code' is not a valid IANA country code")
		}


	override fun JSONEncoder<JSONCodingContext>.encode(value: Country) {
		writeString(value.code)
	}
}

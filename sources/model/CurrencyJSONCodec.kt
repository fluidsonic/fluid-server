package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.invalidValueError
import java.util.Currency


internal object CurrencyJSONCodec : AbstractJSONCodec<Currency, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in Currency>) =
		readString().let { code ->
			runCatching { Currency.getInstance(code) }.getOrNull() ?: invalidValueError("'$code' is not a valid ISO 4217 currency code")
		}


	override fun JSONEncoder<JSONCodingContext>.encode(value: Currency) {
		writeString(value.currencyCode)
	}
}

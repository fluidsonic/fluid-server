package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.JSONException
import java.util.Currency


internal object CurrencyJSONCodec : AbstractJSONCodec<Currency, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in Currency>, decoder: JSONDecoder<JSONCodingContext>) =
		decoder.readString().let { code ->
			runCatching { Currency.getInstance(code) }.getOrNull() ?: throw JSONException("Invalid currency code '$code'")
		}


	override fun encode(value: Currency, encoder: JSONEncoder<JSONCodingContext>) {
		encoder.writeString(value.currencyCode)
	}
}

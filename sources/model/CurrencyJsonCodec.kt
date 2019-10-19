package io.fluidsonic.server

import io.fluidsonic.json.*
import io.fluidsonic.stdlib.*


internal object CurrencyJsonCodec : AbstractJsonCodec<Currency, JsonCodingContext>() {

	override fun JsonDecoder<JsonCodingContext>.decode(valueType: JsonCodingType<Currency>) =
		readString().let { code ->
			runCatching { Currency.byCode(code) }.getOrNull() ?: invalidValueError("'$code' is not a valid ISO 4217 currency code")
		}


	override fun JsonEncoder<JsonCodingContext>.encode(value: Currency) {
		writeString(value.code)
	}
}

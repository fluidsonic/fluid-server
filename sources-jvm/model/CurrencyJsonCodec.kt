package io.fluidsonic.server

import io.fluidsonic.currency.*
import io.fluidsonic.json.*


internal object CurrencyJsonCodec : AbstractJsonCodec<Currency, JsonCodingContext>() {

	override fun JsonDecoder<JsonCodingContext>.decode(valueType: JsonCodingType<Currency>) =
		readString().let { code ->
			Currency.forCodeOrNull(code) ?: invalidValueError("'$code' is not a valid ISO 4217 currency code")
		}


	override fun JsonEncoder<JsonCodingContext>.encode(value: Currency) {
		writeString(value.code.toString())
	}
}

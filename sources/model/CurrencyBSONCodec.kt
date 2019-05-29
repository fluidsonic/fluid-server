package com.github.fluidsonic.baku

import org.bson.*
import java.util.*


internal object CurrencyBSONCodec : AbstractBSONCodec<Currency, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			runCatching { Currency.getInstance(code) }.getOrNull() ?: throw BSONException("Invalid currency code '$code'")
		}


	override fun BsonWriter.encode(value: Currency, context: BSONCodingContext) {
		writeString(value.currencyCode)
	}
}

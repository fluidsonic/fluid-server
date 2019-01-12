package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter
import java.util.Currency


object CurrencyBSONCodec : AbstractBSONCodec<Currency, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			tryOrNull { Currency.getInstance(code) } ?: throw BSONException("Invalid currency code '$code'")
		}


	override fun BsonWriter.encode(value: Currency, context: BSONCodingContext) {
		writeString(value.currencyCode)
	}
}

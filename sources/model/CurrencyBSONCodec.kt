package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.stdlib.*
import org.bson.*


internal object CurrencyBSONCodec : AbstractBSONCodec<Currency, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			runCatching { Currency.byCode(code) }.getOrNull() ?: throw BSONException("Invalid currency code '$code'")
		}


	override fun BsonWriter.encode(value: Currency, context: BSONCodingContext) {
		writeString(value.code)
	}
}

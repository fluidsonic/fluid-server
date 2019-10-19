package io.fluidsonic.server

import io.fluidsonic.stdlib.*
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

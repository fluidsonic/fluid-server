package io.fluidsonic.server

import io.fluidsonic.currency.*
import org.bson.*


internal object CurrencyBSONCodec : AbstractBSONCodec<Currency, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			Currency.forCodeOrNull(code) ?: throw BSONException("Invalid currency code '$code'")
		}


	override fun BsonWriter.encode(value: Currency, context: BSONCodingContext) {
		writeString(value.code.toString())
	}
}

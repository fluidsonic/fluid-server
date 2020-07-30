package io.fluidsonic.server

import io.fluidsonic.stdlib.*
import org.bson.*


internal object CountryBSONCodec : AbstractBSONCodec<Country, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			Country.byCode(CountryCode(code)) ?: throw BSONException("Invalid country code '$code'")
		}


	override fun BsonWriter.encode(value: Country, context: BSONCodingContext) {
		writeString(value.code.value)
	}
}

package io.fluidsonic.server

import io.fluidsonic.country.*
import org.bson.*


internal object CountryBSONCodec : AbstractBSONCodec<Country, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			Country.forCodeOrNull(code) ?: throw BSONException("Invalid country code '$code'")
		}


	override fun BsonWriter.encode(value: Country, context: BSONCodingContext) {
		writeString(value.code.toString())
	}
}

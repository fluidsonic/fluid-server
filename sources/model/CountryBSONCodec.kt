package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.stdlib.*
import org.bson.*


internal object CountryBSONCodec : AbstractBSONCodec<Country, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { code ->
			Country.byCode(code) ?: throw BSONException("Invalid country code '$code'")
		}


	override fun BsonWriter.encode(value: Country, context: BSONCodingContext) {
		writeString(value.code)
	}
}

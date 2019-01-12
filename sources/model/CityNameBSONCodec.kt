package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


object CityNameBSONCodec : AbstractBSONCodec<CityName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		CityName(readString())


	override fun BsonWriter.encode(value: CityName, context: BSONCodingContext) {
		writeString(value.raw)
	}
}

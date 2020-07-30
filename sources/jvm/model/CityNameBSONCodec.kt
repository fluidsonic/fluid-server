package io.fluidsonic.server

import org.bson.*


internal object CityNameBSONCodec : AbstractBSONCodec<CityName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		CityName(readString())


	override fun BsonWriter.encode(value: CityName, context: BSONCodingContext) {
		writeString(value.value)
	}
}

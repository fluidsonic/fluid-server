package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


internal object CompanyNameBSONCodec : AbstractBSONCodec<CompanyName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		CompanyName(readString())


	override fun BsonWriter.encode(value: CompanyName, context: BSONCodingContext) {
		writeString(value.raw)
	}
}

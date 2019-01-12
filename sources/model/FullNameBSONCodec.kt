package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


object FullNameBSONCodec : AbstractBSONCodec<FullName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		FullName(readString())


	override fun BsonWriter.encode(value: FullName, context: BSONCodingContext) {
		writeString(value.raw)
	}
}

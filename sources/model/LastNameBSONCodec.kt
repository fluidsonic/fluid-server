package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


internal object LastNameBSONCodec : AbstractBSONCodec<LastName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		LastName(readString())


	override fun BsonWriter.encode(value: LastName, context: BSONCodingContext) {
		writeString(value.value)
	}
}

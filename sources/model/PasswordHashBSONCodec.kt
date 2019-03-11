package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


internal object PasswordHashBSONCodec : AbstractBSONCodec<PasswordHash, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		PasswordHash(readString())


	override fun BsonWriter.encode(value: PasswordHash, context: BSONCodingContext) {
		writeString(value.vakue)
	}
}

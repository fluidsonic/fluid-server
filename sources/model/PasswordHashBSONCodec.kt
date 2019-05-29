package com.github.fluidsonic.baku

import org.bson.*


internal object PasswordHashBSONCodec : AbstractBSONCodec<PasswordHash, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		PasswordHash(readString())


	override fun BsonWriter.encode(value: PasswordHash, context: BSONCodingContext) {
		writeString(value.vakue)
	}
}

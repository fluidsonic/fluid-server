package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


internal object PostalCodeBSONCodec : AbstractBSONCodec<PostalCode, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		PostalCode(readString())


	override fun BsonWriter.encode(value: PostalCode, context: BSONCodingContext) {
		writeString(value.raw)
	}
}

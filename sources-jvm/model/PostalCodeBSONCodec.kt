package io.fluidsonic.server

import org.bson.*


internal object PostalCodeBSONCodec : AbstractBSONCodec<PostalCode, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		PostalCode(readString())


	override fun BsonWriter.encode(value: PostalCode, context: BSONCodingContext) {
		writeString(value.value)
	}
}

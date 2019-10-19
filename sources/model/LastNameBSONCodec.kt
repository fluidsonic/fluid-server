package io.fluidsonic.server

import org.bson.*


internal object LastNameBSONCodec : AbstractBSONCodec<LastName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		LastName(readString())


	override fun BsonWriter.encode(value: LastName, context: BSONCodingContext) {
		writeString(value.value)
	}
}

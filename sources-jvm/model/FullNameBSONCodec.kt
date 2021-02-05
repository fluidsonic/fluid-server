package io.fluidsonic.server

import org.bson.*


internal object FullNameBSONCodec : AbstractBSONCodec<FullName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		FullName(readString())


	override fun BsonWriter.encode(value: FullName, context: BSONCodingContext) {
		writeString(value.value)
	}
}

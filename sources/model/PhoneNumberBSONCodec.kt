package com.github.fluidsonic.baku

import org.bson.*


internal object PhoneNumberBSONCodec : AbstractBSONCodec<PhoneNumber, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		PhoneNumber(readString())


	override fun BsonWriter.encode(value: PhoneNumber, context: BSONCodingContext) {
		writeString(value.value)
	}
}

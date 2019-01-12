package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter


internal object EmailAddressBSONCodec : AbstractBSONCodec<EmailAddress, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		EmailAddress(readString())


	override fun BsonWriter.encode(value: EmailAddress, context: BSONCodingContext) {
		writeString(value.raw)
	}
}

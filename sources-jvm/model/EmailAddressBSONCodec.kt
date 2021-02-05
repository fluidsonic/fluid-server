package io.fluidsonic.server

import org.bson.*


internal object EmailAddressBSONCodec : AbstractBSONCodec<EmailAddress, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		EmailAddress(readString())


	override fun BsonWriter.encode(value: EmailAddress, context: BSONCodingContext) {
		writeString(value.value)
	}
}

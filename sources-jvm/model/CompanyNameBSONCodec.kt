package io.fluidsonic.server

import org.bson.*


internal object CompanyNameBSONCodec : AbstractBSONCodec<CompanyName, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		CompanyName(readString())


	override fun BsonWriter.encode(value: CompanyName, context: BSONCodingContext) {
		writeString(value.value)
	}
}

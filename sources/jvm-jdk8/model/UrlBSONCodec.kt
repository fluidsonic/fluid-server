package io.fluidsonic.server

import io.ktor.http.*
import org.bson.*


internal object UrlBSONCodec : AbstractBSONCodec<Url, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		Url(readString())


	override fun BsonWriter.encode(value: Url, context: BSONCodingContext) {
		writeString(value.toString())
	}
}

package com.github.fluidsonic.baku

import org.bson.*
import java.time.*


internal object ZoneIdBSONCodec : AbstractBSONCodec<ZoneId, BSONCodingContext>(includesSubclasses = true) {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { id ->
			ZoneId.of(id) ?: error("invalid timezone: $id")
		}


	override fun BsonWriter.encode(value: ZoneId, context: BSONCodingContext) {
		writeString(value.id)
	}
}

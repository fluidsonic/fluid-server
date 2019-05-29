package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.stdlib.*
import org.bson.*


internal object GeoCoordinateBSONCodec : AbstractBSONCodec<GeoCoordinate, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext): GeoCoordinate {
		var coordinate: GeoCoordinate? = null
		var type: String? = null

		readDocumentWithValues { fieldName ->
			when (fieldName) {
				"coordinates" -> coordinate = readArray {
					val longitude = readDouble()
					val latitude = readDouble()

					GeoCoordinate(latitude = latitude, longitude = longitude)
				}
				"type" -> type = readString()
				else -> skipValue()
			}
		}

		if (type != "Point") throw BSONException("invalid type for GeoCoordinate: $type")
		return coordinate ?: throw BSONException("missing coordinate")
	}


	override fun BsonWriter.encode(value: GeoCoordinate, context: BSONCodingContext) {
		writeStartDocument()
		writeName("coordinates")
		writeStartArray()
		writeDouble(value.longitude)
		writeDouble(value.latitude)
		writeEndArray()
		writeName("type")
		writeString("Point")
		writeEndDocument()
	}
}

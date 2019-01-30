package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import com.github.fluidsonic.fluid.stdlib.*


internal object GeoCoordinateJSONCodec : AbstractJSONCodec<GeoCoordinate, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in GeoCoordinate>): GeoCoordinate {
		var latitude: Double? = null
		var longitude: Double? = null

		readFromMapByElementValue { key ->
			when (key) {
				"latitude" -> latitude = readDouble()
				"longitude" -> longitude = readDouble()
				else -> skipValue()
			}
		}

		return GeoCoordinate(
			latitude = latitude ?: missingPropertyError("latitude"),
			longitude = longitude ?: missingPropertyError("longitude")
		)
	}


	override fun JSONEncoder<JSONCodingContext>.encode(value: GeoCoordinate) {
		writeIntoMap {
			writeMapElement("latitude", double = value.latitude)
			writeMapElement("longitude", double = value.longitude)
		}
	}
}

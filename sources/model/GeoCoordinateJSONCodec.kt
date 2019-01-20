package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.readFromMapByElementValue
import com.github.fluidsonic.fluid.json.writeIntoMap
import com.github.fluidsonic.fluid.json.writeMapElement
import com.github.fluidsonic.jetpack.*


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
			latitude = latitude ?: throw JSONException("missing 'latitude'"),
			longitude = longitude ?: throw JSONException("missing 'longitude'")
		)
	}


	override fun JSONEncoder<JSONCodingContext>.encode(value: GeoCoordinate) {
		writeIntoMap {
			writeMapElement("latitude", double = value.latitude)
			writeMapElement("longitude", double = value.longitude)
		}
	}
}

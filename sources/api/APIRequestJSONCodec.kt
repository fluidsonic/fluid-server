package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.AbstractJSONDecoderCodec
import com.github.fluidsonic.fluid.json.JSONCodingContext
import com.github.fluidsonic.fluid.json.JSONCodingType
import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.readFromMapByElementValue


internal object APIRequestJSONCodec : AbstractJSONDecoderCodec<APIRequest<*>, JSONCodingContext>() {

	override fun decode(valueType: JSONCodingType<in APIRequest<*>>, decoder: JSONDecoder<JSONCodingContext>): APIRequest<*> {
		var payload: Any? = null

		decoder.readFromMapByElementValue { key ->
			when (key) {
				"payload" -> payload = readValueOfType(valueType.arguments.single())
				else -> skipValue()
			}
		}

		return APIRequest(
			payload = payload ?: throw JSONException("missing 'payload'")
		)
	}
}

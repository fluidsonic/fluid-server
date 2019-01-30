package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


internal object PostalCodeJSONCodec : AbstractJSONCodec<PostalCode, JSONCodingContext>() {

	override fun JSONDecoder<JSONCodingContext>.decode(valueType: JSONCodingType<in PostalCode>) =
		PostalCode(readString())


	override fun JSONEncoder<JSONCodingContext>.encode(value: PostalCode) {
		writeString(value.raw)
	}
}

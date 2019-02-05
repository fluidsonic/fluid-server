package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import com.github.fluidsonic.fluid.stdlib.*


@JSON.CodecProvider(
	externalTypes = [
		JSON.ExternalType(GeoCoordinate::class)
	]
)
internal interface BakuJSONCodecProvider : JSONCodecProvider<JSONCodingContext>

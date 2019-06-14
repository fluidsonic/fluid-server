package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*
import com.github.fluidsonic.fluid.stdlib.*


@JSON.CodecProvider(
	externalTypes = [
		JSON.ExternalType(Cents::class, JSON(
			representation = JSON.Representation.singleValue
		)),
		JSON.ExternalType(GeoCoordinate::class)
	]
)
internal interface BakuJSONCodecProvider : JSONCodecProvider<JSONCodingContext>

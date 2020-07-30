package io.fluidsonic.server

import io.fluidsonic.json.*
import io.fluidsonic.stdlib.*


@Json.CodecProvider(
	externalTypes = [
		Json.ExternalType(Cents::class, Json(
			representation = Json.Representation.singleValue
		)),
		Json.ExternalType(GeoCoordinate::class)
	]
)
internal interface BakuJsonCodecProvider : JsonCodecProvider<JsonCodingContext>

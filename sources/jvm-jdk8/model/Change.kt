package io.fluidsonic.server

import io.fluidsonic.json.*


@Json(representation = Json.Representation.singleValue)
data class Change<Value>(val value: Value)

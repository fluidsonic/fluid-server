package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class CityName(val value: String) {

	override fun toString() = value
}

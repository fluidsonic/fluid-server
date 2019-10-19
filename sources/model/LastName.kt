package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class LastName(val value: String) {

	override fun toString() = value
}

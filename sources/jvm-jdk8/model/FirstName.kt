package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class FirstName(val value: String) {

	override fun toString() = value
}

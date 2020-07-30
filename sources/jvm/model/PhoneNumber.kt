package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class PhoneNumber(val value: String) {

	override fun toString() = value
}

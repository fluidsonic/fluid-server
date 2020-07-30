package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class EmailAddress(val value: String) {

	fun toLowerCase() =
		EmailAddress(value.toLowerCase())


	override fun toString() = value
}

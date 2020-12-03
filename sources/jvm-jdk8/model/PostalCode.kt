package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class PostalCode(val value: String) {

	override fun toString() = value
}

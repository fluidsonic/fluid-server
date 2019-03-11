package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class EmailAddress(val value: String) {

	fun toLowerCase() =
		EmailAddress(value.toLowerCase())


	override fun toString() = value
}

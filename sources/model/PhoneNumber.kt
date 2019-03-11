package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class PhoneNumber(val value: String) {

	override fun toString() = value
}

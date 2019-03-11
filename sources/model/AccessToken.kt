package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class AccessToken(val value: String) {

	override fun toString() = value
}

package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class CityName(val raw: String) {

	override fun toString() = raw
}

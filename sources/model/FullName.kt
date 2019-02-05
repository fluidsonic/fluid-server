package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON
inline class FullName(val raw: String) {

	constructor(firstName: FirstName, lastName: LastName) :
		this("$firstName $lastName")


	override fun toString() = raw
}

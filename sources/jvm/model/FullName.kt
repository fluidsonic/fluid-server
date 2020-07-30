package io.fluidsonic.server

import io.fluidsonic.json.*


@Json
inline class FullName(val value: String) {

	constructor(firstName: FirstName, lastName: LastName) :
		this("$firstName $lastName")


	override fun toString() = value
}

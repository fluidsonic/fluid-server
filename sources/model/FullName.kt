package com.github.fluidsonic.baku


inline class FullName(val raw: String) {

	constructor(firstName: FirstName, lastName: LastName) :
		this("$firstName $lastName")


	override fun toString() = raw
}

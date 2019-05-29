package com.github.fluidsonic.baku

import java.util.*


// FIXME add all countries
class Country private constructor(
	val code: String
) {

	override fun equals(other: Any?) =
		other === this || (other is Country && code == other.code)


	override fun hashCode() =
		code.hashCode()


	val isInEuropeanUnion
		get() = europeanUnion.contains(this)


	val name
		get() = name(Locale.US)


	fun name(locale: Locale) =
		Locale("", code).getDisplayCountry(locale)!!


	override fun toString() =
		"Country($code)"


	companion object {

		val allByCode = Locale.getISOCountries()
			.associate {
				val code = it.toUpperCase()
				code to Country(code)
			}


		fun byCode(code: String) =
			allByCode[code.toUpperCase()]


		val belgium = Country.byCode("BE")!!
		val germany = Country.byCode("DE")!!


		private val europeanUnion =
			setOf( // FIXME
				belgium,
				germany
			)
	}
}

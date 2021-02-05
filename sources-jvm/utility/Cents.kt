package io.fluidsonic.server

import kotlin.math.*


data /*inline*/ class Cents(val value: Long) : Comparable<Cents> {

	constructor(value: Int) : this(value.toLong())


	val absolute: Cents
		get() = Cents(value.absoluteValue)


	override operator fun compareTo(other: Cents): Int =
		value.compareTo(other.value)


	operator fun div(other: Int): Cents =
		Cents(value / other)


	operator fun div(other: Long): Cents =
		Cents(value / other)


	operator fun div(other: Cents): Long =
		value / other.value


	val isNegative: Boolean
		get() = value < 0


	val isPositive: Boolean
		get() = value < 0


	val isZero: Boolean
		get() = value == 0L


	operator fun minus(other: Cents): Cents =
		Cents(value - other.value)


	operator fun plus(other: Cents): Cents =
		Cents(value + other.value)


	operator fun rem(other: Int): Cents =
		Cents(value % other)


	operator fun rem(other: Long): Cents =
		Cents(value % other)


	operator fun rem(other: Cents): Long =
		value % other.value


	operator fun times(other: Int): Cents =
		Cents(value * other)


	operator fun times(other: Long): Cents =
		Cents(value * other)


	override fun toString(): String =
		value.toString()


	operator fun unaryMinus(): Cents =
		Cents(-value)


	companion object {

		val zero: Cents = Cents(0L)
	}
}


operator fun Int.times(other: Cents): Cents =
	other.times(this)


operator fun Long.times(other: Cents): Cents =
	other.times(this)

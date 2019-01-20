package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONDecoder
import com.github.fluidsonic.fluid.json.JSONEncoder
import com.github.fluidsonic.fluid.json.writeIntoMap


abstract class BakuCommandFactory<in Transaction : BakuTransaction, Command : BakuCommand, Result : Any>(
	name: String
) {

	val name = BakuCommandName(name)


	abstract fun JSONDecoder<Transaction>.decodeCommand(): Command


	open fun JSONEncoder<Transaction>.encodeResult(result: Result) {
		writeIntoMap {}
	}


	abstract class Empty<in Transaction : BakuTransaction, Command : BakuCommand, Result : Any>(
		name: String
	) : BakuCommandFactory<Transaction, Command, Result>(name = name) {

		abstract fun createCommand(): Command


		final override fun JSONDecoder<Transaction>.decodeCommand() =
			createCommand().also { skipValue() }
	}
}


fun <Transaction : BakuTransaction, Result : Any> BakuCommandFactory<Transaction, *, Result>.encodeResult(result: Result, encoder: JSONEncoder<Transaction>) =
	encoder.encodeResult(result)


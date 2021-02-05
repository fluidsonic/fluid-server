package io.fluidsonic.server

import io.fluidsonic.json.*


abstract class BakuCommandFactory<in Transaction : BakuTransaction, Command : BakuCommand, Result : Any>(
	name: String
) {

	val name = BakuCommandName(name)


	abstract fun JsonDecoder<Transaction>.decodeCommand(): Command


	open fun JsonEncoder<Transaction>.encodeResult(result: Result) {
		writeIntoMap {}
	}


	abstract class Empty<in Transaction : BakuTransaction, Command : BakuCommand, Result : Any>(
		name: String
	) : BakuCommandFactory<Transaction, Command, Result>(name = name) {

		abstract fun createCommand(): Command


		final override fun JsonDecoder<Transaction>.decodeCommand() =
			createCommand().also { skipValue() }
	}
}


fun <Transaction : BakuTransaction, Result : Any> BakuCommandFactory<Transaction, *, Result>.encodeResult(result: Result, encoder: JsonEncoder<Transaction>) =
	encoder.encodeResult(result)


package com.github.fluidsonic.baku


internal class BakuCommandHandler<in Transaction : BakuTransaction, Command : BakuCommand, Result : Any>(
	val factory: BakuCommandFactory<Transaction, Command, Result>,
	val handler: Transaction.() -> (suspend (command: Command) -> Result)
)

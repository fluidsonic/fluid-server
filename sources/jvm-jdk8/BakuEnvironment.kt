package io.fluidsonic.server

import io.ktor.application.*


interface BakuEnvironment<Context : BakuContext, Transaction : BakuTransaction> {

	suspend fun createContext(): Context
	suspend fun createTransaction(context: Context, call: ApplicationCall?): Transaction
	suspend fun onStart(context: Context) {}
}

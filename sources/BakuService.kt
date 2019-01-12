package com.github.fluidsonic.baku

import io.ktor.application.ApplicationCall


interface BakuService<Context : BakuContext, Transaction : BakuTransaction> {

	suspend fun createContext(): Context
	suspend fun createTransaction(context: Context, call: ApplicationCall?): Transaction
	suspend fun onStart(context: Context) {}
}

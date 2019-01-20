package com.github.fluidsonic.baku

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.util.pipeline.PipelineContext


abstract class BakuModule<Context : BakuContext, Transaction : BakuTransaction> {

	internal fun configure() =
		BakuModuleConfiguration(this).apply { configure() }


	abstract fun BakuModuleConfiguration<Context, Transaction>.configure()


	@Suppress("UNCHECKED_CAST")
	val ApplicationCall.transaction
		get() = attributes[BakuTransactionFeature.transactionAttributeKey] as Transaction


	val PipelineContext<*, ApplicationCall>.transaction
		get() = call.transaction
}


val ApplicationCall.transaction
	get() = attributes[BakuTransactionFeature.transactionAttributeKey]


val PipelineContext<*, ApplicationCall>.transaction
	get() = call.transaction

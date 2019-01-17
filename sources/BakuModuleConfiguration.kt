package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONCodecProvider
import com.github.fluidsonic.fluid.json.JSONEncoder
import io.ktor.application.Application
import io.ktor.routing.Routing
import io.ktor.util.pipeline.ContextDsl


class BakuModuleConfiguration<Context : BakuContext, Transaction : BakuTransaction> internal constructor(
	internal val module: BakuModule<Context, Transaction>
) {

	internal val additionalResponseEncodings = mutableListOf<JSONEncoder<Transaction>.() -> Unit>()
	internal val bsonCodecProviders = mutableListOf<BSONCodecProvider<Context>>()
	internal val customConfigurations = mutableListOf<Application.() -> Unit>()
	internal val entityResolution = BakuEntityResolution<Transaction>()
	internal val failureConfigurations = mutableListOf<APIFailureProcessing.Configuration.() -> Unit>()
	internal val idFactories = mutableSetOf<EntityId.Factory<*>>()
	internal val jsonCodecProviders = mutableListOf<JSONCodecProvider<Transaction>>()
	internal val routingConfigurations = mutableListOf<Routing.() -> Unit>()


	fun bsonCodecProviders(vararg providers: BSONCodecProvider<Context>) {
		bsonCodecProviders += providers
	}


	fun custom(configure: Application.() -> Unit) {
		customConfigurations += configure
	}


	fun entityResolution(configure: BakuEntityResolution<Transaction>.() -> Unit) {
		entityResolution.configure()
	}


	fun failures(configure: APIFailureProcessing.Configuration.() -> Unit) {
		failureConfigurations += configure
	}


	fun ids(vararg factories: EntityId.Factory<*>) {
		idFactories += factories
	}


	fun jsonCodecProviders(vararg providers: JSONCodecProvider<Transaction>) {
		jsonCodecProviders += providers
	}


	fun additionalResponseEncoding(encode: JSONEncoder<Transaction>.() -> Unit) {
		additionalResponseEncodings += encode
	}


	@ContextDsl
	fun routing(configure: Routing.() -> Unit) {
		routingConfigurations += configure
	}
}

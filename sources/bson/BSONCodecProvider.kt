package com.github.fluidsonic.baku

import kotlin.reflect.KClass


interface BSONCodecProvider<in Context : BSONCodingContext> {

	fun <Value : Any> codecForClass(valueClass: KClass<in Value>): BSONCodec<Value, Context>?


	companion object {

		fun <Context : BSONCodingContext> of(
			vararg providers: BSONCodecProvider<Context>
		) =
			of(providers.asIterable())


		fun <Context : BSONCodingContext> of(
			providers: Iterable<BSONCodecProvider<Context>>
		): BSONCodecProvider<Context> =
			StandardBSONCodecProvider(providers = providers)
	}
}

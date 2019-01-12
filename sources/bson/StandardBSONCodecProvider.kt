package com.github.fluidsonic.baku

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


internal class StandardBSONCodecProvider<in Context : BSONCodingContext>(
	providers: Iterable<BSONCodecProvider<Context>>
) : BSONCodecProvider<Context> {

	private val codecByClass = ConcurrentHashMap<KClass<*>, BSONCodec<*, Context>>()
	private val providers = providers.toSet().toTypedArray()


	@Suppress("UNCHECKED_CAST")
	override fun <Value : Any> codecForClass(valueClass: KClass<in Value>): BSONCodec<Value, Context>? {
		return codecByClass.getOrPut(valueClass) {
			for (provider in providers) {
				val codec = provider.codecForClass(valueClass)
				if (codec != null) {
					return@getOrPut codec
				}
			}

			return null
		} as BSONCodec<Value, Context>
	}
}

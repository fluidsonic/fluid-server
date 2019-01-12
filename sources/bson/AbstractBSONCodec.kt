package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.DecoderContext
import org.bson.codecs.Encoder
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecRegistry
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass


abstract class AbstractBSONCodec<Value : Any, in Context : BSONCodingContext>(
	valueClass: Class<Value>? = null
) : BSONCodec<Value, Context> {

	private var context: Context? = null
	private var rootRegistry: CodecRegistry? = null

	@Suppress("UNCHECKED_CAST")
	private val valueClass = valueClass
		?: (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments.first() as Class<Value>


	abstract fun BsonReader.decode(context: Context): Value
	abstract fun BsonWriter.encode(value: Value, context: Context)


	@Suppress("UNCHECKED_CAST")
	final override fun <Value : Any> codecForClass(valueClass: KClass<in Value>) =
		super.codecForClass(valueClass)


	internal fun configure(context: Context, rootRegistry: CodecRegistry) {
		this.context = context
		this.rootRegistry = rootRegistry
	}


	final override fun decode(reader: BsonReader, decoderContext: DecoderContext) =
		reader.decode(context = requireContext())


	final override fun encode(writer: BsonWriter, value: Value, encoderContext: EncoderContext) =
		writer.encode(value = value, context = requireContext())


	final override fun getEncoderClass() =
		valueClass


	fun <Value : Any> BsonReader.readValueOfType(`class`: KClass<Value>) =
		requireRootRegistry()[`class`.java].decode(this, decoderContext)!!


	fun BsonWriter.write(name: String, value: Any) {
		writeName(name)

		@Suppress("UNCHECKED_CAST")
		(requireRootRegistry()[value::class.java] as Encoder<Any>).encode(this, value, encoderContext)
	}


	private fun requireContext() =
		context ?: error("AbstractBSONCodec must be used by the CodecRegistry provided by Baku")


	private fun requireRootRegistry() =
		rootRegistry ?: error("AbstractBSONCodec must be used by the CodecRegistry provided by Baku")


	companion object {

		private val decoderContext = DecoderContext.builder().build()!!
		private val encoderContext = EncoderContext.builder().build()!!
	}
}

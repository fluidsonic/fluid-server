package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.DecoderContext
import org.bson.codecs.Encoder
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecRegistry
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass


abstract class AbstractBSONCodec<Value : Any, in Context : BSONCodingContext>(
	private val additionalProviders: List<BSONCodecProvider<Context>> = emptyList(),
	valueClass: Class<Value>? = null,
	private val includesSubclasses: Boolean = false
) : BSONCodec<Value, Context> {

	private var context: Context? = null
	private var rootRegistry: CodecRegistry? = null
	private val valueClass = valueClass ?: defaultValueClass(this::class)


	abstract fun BsonReader.decode(context: Context): Value
	abstract fun BsonWriter.encode(value: Value, context: Context)


	final override fun <Value : Any> codecForClass(valueClass: KClass<in Value>): BSONCodec<Value, Context>? {
		super.codecForClass(valueClass)?.let { return it }

		@Suppress("UNCHECKED_CAST")
		if (includesSubclasses && this.valueClass.isAssignableFrom(valueClass.java))
			return this as BSONCodec<Value, Context>

		for (provider in additionalProviders)
			provider.codecForClass(valueClass)?.let { return it }

		return null
	}


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


	fun <Value : Any> BsonReader.readValueOfType(name: String, `class`: KClass<Value>): Value {
		readName(name)
		return readValueOfType(`class`)
	}


	fun <Value : Any> BsonReader.readValueOfType(`class`: KClass<Value>) =
		requireRootRegistry()[`class`.java].decode(this, decoderContext)!!


	fun <Value : Any> BsonReader.readValueOfTypeOrNull(name: String, `class`: KClass<Value>): Value? {
		readName(name)
		return readValueOfTypeOrNull(`class`)
	}


	fun <Value : Any> BsonReader.readValueOfTypeOrNull(`class`: KClass<Value>): Value? {
		expectValue("readValueOfTypeOrNull")

		if (currentBsonType == BsonType.NULL) {
			skipValue()
			return null
		}

		return readValueOfType(`class`)
	}


	fun <Value : Any> BsonReader.readValuesOfType(`class`: KClass<Value>): List<Value> =
		readValuesOfType(`class`, container = mutableListOf())


	fun <Value, Container> BsonReader.readValuesOfType(`class`: KClass<Value>, container: Container): Container where Value : Any, Container : MutableCollection<Value> {
		readArrayWithValues {
			container.add(readValueOfType(`class`))
		}

		return container
	}


	fun <Value : Any> BsonReader.readValuesOfTypeOrNull(`class`: KClass<Value>): List<Value>? {
		expectValue("readValuesOfTypeOrNull")

		if (currentBsonType == BsonType.NULL) {
			skipValue()
			return null
		}

		return readValuesOfType(`class`)
	}


	fun <Value, Container> BsonReader.readValuesOfTypeOrNull(`class`: KClass<Value>, container: Container): Container? where Value : Any, Container : MutableCollection<Value> {
		expectValue("readValuesOfTypeOrNull")

		if (currentBsonType == BsonType.NULL) {
			skipValue()
			return null
		}

		return readValuesOfType(`class`, container = container)
	}


	fun BsonWriter.write(name: String, value: Any) {
		writeName(name)
		writeValue(value)
	}


	@JvmName("writeOrSkip")
	fun BsonWriter.write(name: String, valueOrSkip: Any?) {
		valueOrSkip ?: return

		write(name = name, value = valueOrSkip)
	}


	fun BsonWriter.write(name: String, values: Iterable<Any>) {
		writeName(name)
		writeValues(values)
	}


	@JvmName("writeOrSkip")
	fun BsonWriter.write(name: String, valuesOrSkip: Iterable<Any>?) {
		valuesOrSkip ?: return

		write(name = name, values = valuesOrSkip)
	}


	fun BsonWriter.writeValue(value: Any) {
		@Suppress("UNCHECKED_CAST")
		(requireRootRegistry()[value::class.java] as Encoder<Any>).encode(this, value, encoderContext)
	}


	fun BsonWriter.writeValues(values: Iterable<Any>) {
		writeArray {
			for (value in values) {
				writeValue(value)
			}
		}
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


@Suppress("UNCHECKED_CAST")
private fun <Value : Any> defaultValueClass(codecClass: KClass<out AbstractBSONCodec<Value, *>>): Class<Value> {
	val typeArgument = (codecClass.java.genericSuperclass as ParameterizedType).actualTypeArguments.first()
	return when (typeArgument) {
		is Class<*> -> typeArgument as Class<Value>
		is ParameterizedType -> typeArgument.rawType as Class<Value>
		else -> error("unsupported type: $typeArgument")
	}
}

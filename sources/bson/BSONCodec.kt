package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import kotlin.reflect.KClass


interface BSONCodec<Value : Any, in Context : BSONCodingContext> : BSONCodecProvider<Context>, Codec<Value> {

	override fun decode(reader: BsonReader, decoderContext: DecoderContext): Value
	override fun encode(writer: BsonWriter, value: Value, encoderContext: EncoderContext)


	@Suppress("UNCHECKED_CAST")
	override fun <Value : Any> codecForClass(valueClass: KClass<in Value>) =
		if (encoderClass == valueClass.java)
			this as BSONCodec<Value, Context>
		else
			null
}

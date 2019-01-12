package com.github.fluidsonic.baku

import com.github.fluidsonic.jetpack.*
import org.bson.BsonWriter
import org.bson.codecs.EncoderContext
import org.bson.types.ObjectId
import java.time.temporal.TemporalAccessor
import java.util.Date


fun BsonWriter.write(name: String, boolean: Boolean) {
	writeName(name)
	writeBoolean(boolean)
}


fun BsonWriter.write(name: String, date: Date) {
	writeName(name)
	writeDate(date)
}


@JvmName("writeOrSkip")
fun BsonWriter.write(name: String, dateOrSkip: Date?) {
	if (dateOrSkip == null) {
		return
	}

	write(name = name, date = dateOrSkip)
}


fun BsonWriter.write(name: String, double: Double) {
	writeName(name)
	writeDouble(double)
}


@JvmName("writeOrSkip")
fun BsonWriter.write(name: String, doubleOrSkip: Double?) {
	if (doubleOrSkip == null) {
		return
	}

	write(name = name, double = doubleOrSkip)
}


fun BsonWriter.write(name: String, temporal: TemporalAccessor) {
	writeName(name)
	writeTemporal(temporal)
}


@JvmName("writeOrSkip")
fun BsonWriter.write(name: String, temporalOrSkip: TemporalAccessor?) {
	if (temporalOrSkip == null) {
		return
	}

	write(name = name, temporal = temporalOrSkip)
}


fun BsonWriter.write(name: String, int32: Int) {
	writeName(name)
	writeInt32(int32)
}


@JvmName("writeOrSkip")
fun BsonWriter.write(name: String, int32OrSkip: Int?) {
	if (int32OrSkip == null) {
		return
	}

	write(name = name, int32 = int32OrSkip)
}


fun <M, U> BsonWriter.write(name: String, measurement: Measurement<M, U>, unit: U) where M : Measurement<M, U>, U : Enum<U>, U : UnitType<U, M> {
	writeName(name)
	writeMeasurement(measurement, unit = unit)
}


@JvmName("writeOrSkip")
fun <M, U> BsonWriter.write(name: String, measurementOrSkip: Measurement<M, U>?, unit: U) where M : Measurement<M, U>, U : Enum<U>, U : UnitType<U, M> {
	if (measurementOrSkip == null) {
		return
	}

	write(name = name, measurement = measurementOrSkip, unit = unit)
}


fun BsonWriter.write(name: String, objectId: ObjectId) {
	writeName(name)
	writeObjectId(objectId)
}


@JvmName("writeOrSkip")
fun BsonWriter.write(name: String, objectIdOrSkip: ObjectId?) {
	if (objectIdOrSkip == null) {
		return
	}

	write(name = name, objectId = objectIdOrSkip)
}


fun BsonWriter.write(name: String, string: String) {
	writeName(name)
	writeString(string)
}


fun BsonWriter.write(name: String, strings: Iterable<String>) {
	writeName(name)
	writeStrings(strings)
}


fun BsonWriter.write(name: String, stringOrSkip: String?, skipIfEmpty: Boolean = false) {
	if (stringOrSkip == null || (skipIfEmpty && stringOrSkip.isEmpty())) {
		return
	}

	write(name = name, string = stringOrSkip)
}


inline fun BsonWriter.writeArray(name: String, write: BsonWriter.() -> Unit) {
	writeName(name)
	writeArray(write)
}


inline fun BsonWriter.writeArray(write: BsonWriter.() -> Unit) {
	writeStartArray()
	write()
	writeEndArray()
}


fun BsonWriter.writeCoordinate(coordinate: GeoCoordinate) {
	writeArray {
		writeDouble(coordinate.longitude)
		writeDouble(coordinate.latitude)
	}
}


fun BsonWriter.writeDate(date: Date) {
	writeDateTime(date.time)
}


inline fun BsonWriter.writeDocument(name: String, write: BsonWriter.() -> Unit) {
	writeName(name)
	writeDocument(write)
}


inline fun BsonWriter.writeDocument(write: BsonWriter.() -> Unit) {
	writeStartDocument()
	write()
	writeEndDocument()
}


inline fun <Key, Value> BsonWriter.writeMap(map: Map<Key, Value>, writeEntry: BsonWriter.(key: Key, value: Value) -> Unit) {
	writeDocument {
		for ((key, value) in map) {
			writeEntry(key, value)
		}
	}
}


fun BsonWriter.writeTemporal(temporal: TemporalAccessor) {
	writeDateTime(temporal.toEpochMilli())
}


fun <M, U> BsonWriter.writeMeasurement(measurement: Measurement<M, U>, unit: U) where M : Measurement<M, U>, U : Enum<U>, U : UnitType<U, M> {
	writeDouble(measurement.valueInUnit(unit))
}


fun BsonWriter.writeStrings(strings: Iterable<String>) {
	writeStartArray()
	for (string in strings) {
		writeString(string)
	}
	writeEndArray()
}


private val dummyContext = EncoderContext.builder().build()

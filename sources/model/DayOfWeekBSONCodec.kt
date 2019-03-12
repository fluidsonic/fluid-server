package com.github.fluidsonic.baku

import org.bson.BsonReader
import org.bson.BsonWriter
import java.time.DayOfWeek


internal object DayOfWeekBSONCodec : AbstractBSONCodec<DayOfWeek, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readString().let { id ->
			when (id) {
				"monday" -> DayOfWeek.MONDAY
				"tuesday" -> DayOfWeek.TUESDAY
				"wednesday" -> DayOfWeek.WEDNESDAY
				"thursday" -> DayOfWeek.THURSDAY
				"friday" -> DayOfWeek.FRIDAY
				"saturday" -> DayOfWeek.SATURDAY
				"sunday" -> DayOfWeek.SUNDAY
				else -> error("invalid day of week: $id")
			}
		}


	override fun BsonWriter.encode(value: DayOfWeek, context: BSONCodingContext) {
		writeString(when (value) {
			DayOfWeek.MONDAY -> "monday"
			DayOfWeek.TUESDAY -> "tuesday"
			DayOfWeek.WEDNESDAY -> "wednesday"
			DayOfWeek.THURSDAY -> "thursday"
			DayOfWeek.FRIDAY -> "friday"
			DayOfWeek.SATURDAY -> "saturday"
			DayOfWeek.SUNDAY -> "sunday"
		})
	}
}

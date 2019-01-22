package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.JSONException
import com.github.fluidsonic.fluid.json.JSONReader
import com.github.fluidsonic.fluid.json.JSONToken


internal class PropertyInjectingJSONReader(
	properties: Map<String, String>,
	private val source: JSONReader
) : JSONReader {

	private var currentProperty: Map.Entry<String, String>? = null
	private var currentPropertyKeyRead = false
	private var depth = 0
	private var topLevelIsMap = false
	private val propertyIterator = properties.entries.iterator()


	override fun close() {
		source.close()
	}


	private fun expectTokenForCustomProperty(expected: JSONToken) {
		currentProperty ?: return

		val token = nextToken
		if (token != expected) {
			val tokenString = if (token != null) "'$token'" else "<end of input>"
			throw JSONException("unexpected token $tokenString, expected $expected")
		}
	}


	override val nextToken: JSONToken?
		get() {
			val nextToken = source.nextToken
			if (nextToken != JSONToken.mapEnd) return nextToken
			if (depth != 1 || !topLevelIsMap) return nextToken

			if (currentProperty == null) {
				if (!propertyIterator.hasNext()) return nextToken
				currentProperty = propertyIterator.next()
				currentPropertyKeyRead = false
			}

			return if (currentPropertyKeyRead)
				JSONToken.stringValue
			else
				JSONToken.mapKey
		}


	override fun readBoolean(): Boolean {
		expectTokenForCustomProperty(JSONToken.booleanValue)

		return source.readBoolean()
	}


	override fun readDouble(): Double {
		expectTokenForCustomProperty(JSONToken.numberValue)

		return source.readDouble()
	}


	override fun readListEnd() {
		expectTokenForCustomProperty(JSONToken.listEnd)

		source.readListEnd()

		depth -= 1
	}


	override fun readListStart() {
		expectTokenForCustomProperty(JSONToken.listStart)

		source.readListStart()

		depth += 1
	}


	override fun readLong(): Long {
		expectTokenForCustomProperty(JSONToken.numberValue)

		return source.readLong()
	}


	override fun readMapEnd() {
		expectTokenForCustomProperty(JSONToken.mapEnd)

		source.readMapEnd()

		depth -= 1
		if (depth == 0) {
			topLevelIsMap = false
		}
	}


	override fun readMapStart() {
		expectTokenForCustomProperty(JSONToken.mapStart)

		source.readMapStart()

		if (depth == 0) {
			topLevelIsMap = true
		}
		depth += 1
	}


	override fun readNull(): Nothing? {
		expectTokenForCustomProperty(JSONToken.nullValue)

		return source.readNull()
	}


	override fun readNumber(): Number {
		expectTokenForCustomProperty(JSONToken.numberValue)

		return source.readNumber()
	}


	override fun readString(): String {
		val currentProperty = currentProperty ?: return source.readString()

		return if (currentPropertyKeyRead) {
			val value = currentProperty.value
			this.currentProperty = null
			this.currentPropertyKeyRead = false

			value
		}
		else {
			this.currentPropertyKeyRead = true

			currentProperty.key
		}
	}


	override fun terminate() {
		source.terminate()
	}
}

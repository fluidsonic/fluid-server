package com.github.fluidsonic.baku


inline fun <R> tryOrNull(action: () -> R) =
	try {
		action()
	}
	catch (e: Exception) {
		null
	}


inline fun <T : Any, R> T.tryOrNull(action: T.() -> R) =
	try {
		action()
	}
	catch (e: Exception) {
		null
	}

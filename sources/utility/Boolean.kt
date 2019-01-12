package com.github.fluidsonic.baku


inline fun <R> Boolean.thenTake(action: () -> R) =
	if (this) action() else null

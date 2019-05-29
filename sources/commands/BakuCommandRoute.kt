package com.github.fluidsonic.baku

import io.ktor.routing.*


internal class BakuCommandRoute<in Transaction : BakuTransaction>(
	val factory: BakuCommandFactory<Transaction, *, *>,
	val route: Route
)

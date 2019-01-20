package com.github.fluidsonic.baku

import io.ktor.routing.Route


internal class BakuCommandRoute<in Transaction : BakuTransaction>(
	val factory: BakuCommandFactory<Transaction, *, *>,
	val route: Route
)

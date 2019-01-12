package com.github.fluidsonic.baku

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.produce


internal fun <E> Iterable<E>.toChannel() =
	GlobalScope.produce { forEach { send(it) } }


internal fun <E> CoroutineScope.emptyReceiveChannel() =
	produce<E> { }

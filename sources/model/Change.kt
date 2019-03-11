package com.github.fluidsonic.baku

import com.github.fluidsonic.fluid.json.*


@JSON(representation = JSON.Representation.singleValue)
data class Change<Value>(val value: Value)

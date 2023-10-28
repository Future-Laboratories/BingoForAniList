package io.future.laboratories.ui

import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation

//region Strings

internal fun String.colon(): String = "$this:"

//endregion

//region Transformations

@Suppress("FunctionName")
internal fun EvenRoundedCornersTransformation(all: Float): Transformation =
    RoundedCornersTransformation(all, all, all, all)

//endregion


package io.future.laboratories.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//region Colors

public val textColor: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

//endregion

//region lambda

public operator fun (() -> Unit).plus(rhs: () -> Unit): () -> Unit = {
    this()
    rhs()
}

//endregion

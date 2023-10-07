package io.future.laboratories.anilistbingo.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistbingo.textColor

@Composable
internal fun DefaultSpacer(): Unit = Spacer(modifier = Modifier.width(4.dp))

@Composable
internal fun PositiveButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
): Unit = Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(contentColor = textColor),
    content = content,
)

@Composable
internal fun NegativeButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
): Unit = Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA0000), contentColor = textColor),
    content = content,
)
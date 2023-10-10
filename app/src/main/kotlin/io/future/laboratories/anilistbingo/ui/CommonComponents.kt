package io.future.laboratories.anilistbingo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.future.laboratories.anilistbingo.textColor

//region Divider & Spacer

@Composable
internal fun RowScope.DefaultDivider() = Divider(
    modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(1.dp)),
    thickness = 2.dp,
    color = MaterialTheme.colorScheme.primary,
)

@Composable
internal fun DefaultHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DefaultDivider()

        Text(
            text = title,
            modifier = Modifier.padding(all = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
        )

        DefaultDivider()
    }
}

@Composable
internal fun DefaultSpacer(): Unit = Spacer(modifier = Modifier.size(4.dp))

//endregion

//region Buttons

internal val negativeButtonColors
    @Composable get() = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFAA0000),
        contentColor = textColor
    )

internal val positiveButtonColors
    @Composable get() = ButtonDefaults.buttonColors(contentColor = textColor)

@Composable
internal fun PositiveImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentDescription: String,
    imageResId: Int,
    content: @Composable RowScope.() -> Unit = {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
        )
    },
): Unit = Button(
    onClick = onClick,
    modifier = Modifier.size(40.dp) then modifier,
    colors = positiveButtonColors,
    contentPadding = PaddingValues(0.dp),
    content = content,
)

@Composable
internal fun NegativeImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentDescription: String,
    imageResId: Int,
    content: @Composable RowScope.() -> Unit = {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
        )
    },
): Unit = Button(
    onClick = onClick,
    modifier = Modifier.size(40.dp) then modifier,
    colors = negativeButtonColors,
    contentPadding = PaddingValues(0.dp),
    content = content,
)

@Composable
internal fun PositiveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
): Unit = Button(
    onClick = onClick,
    modifier = modifier,
    colors = positiveButtonColors,
    content = content,
)

@Composable
internal fun NegativeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
): Unit = Button(
    onClick = onClick,
    modifier = modifier,
    colors = negativeButtonColors,
    content = content,
)

//endregion
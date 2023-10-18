package io.future.laboratories.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import io.future.laboratories.common.textColor
import io.future.laboratories.ui.R
import io.future.laboratories.ui.colon

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
        contentColor = textColor,
    )

internal val positiveButtonColors
    @Composable get() = ButtonDefaults.buttonColors(contentColor = textColor)

@Composable
internal fun PositiveImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentDescription: String,
    imageVector: ImageVector,
    content: @Composable RowScope.() -> Unit = {
        Icon(
            imageVector = imageVector,
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
    imageVector: ImageVector,
    content: @Composable RowScope.() -> Unit = {
        Icon(
            imageVector = imageVector,
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
internal fun BackButton(
    onClick: () -> Unit,
) {
    NegativeButton(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val backString = stringResource(id = R.string.back)
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = backString,
                tint = textColor,
            )
            Text(text = backString)
        }
    }
}

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

//region Dialog

@Composable
internal fun DefaultDialog(
    text: String,
    actionButtonText: String,
    abortText: String,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    onAbort: () -> Unit = onDismiss,
): Unit = Dialog(onDismissRequest = onDismiss) {
    Card {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = text)

            DefaultSpacer()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            ) {
                PositiveButton(onClick = onAbort) {
                    Text(text = abortText)
                }

                NegativeButton(onClick = {
                    onAction()
                    onDismiss()
                }) {
                    Text(text = actionButtonText)
                }
            }
        }
    }
}

//endregion

//region appBar

@Composable
internal fun ProfileButton(
    url: String?,
    isLoggedIn: Boolean,
    onClick: () -> Unit,
) {
    val accountCircle = rememberVectorPainter(
        image = Icons.Rounded.AccountCircle,
        tint = MaterialTheme.colorScheme.primary,
    )

    val requestURL = if (!isLoggedIn) null else url

    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .background(textColor, CircleShape)
            .size(44.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(requestURL)
                .crossfade(true)
                .transformations(CircleCropTransformation())
                .build(),
            placeholder = accountCircle,
            fallback = accountCircle,
            contentDescription = null,
            modifier = Modifier
                .size(size = 40.dp)
                .clickable {
                    onClick()
                },
        )
    }
}

@Composable
private fun rememberVectorPainter(
    image: ImageVector,
    tint: Color,
) = rememberVectorPainter(
    defaultWidth = image.defaultWidth,
    defaultHeight = image.defaultHeight,
    viewportWidth = image.viewportWidth,
    viewportHeight = image.viewportHeight,
    name = image.name,
    tintColor = tint,
    tintBlendMode = image.tintBlendMode,
    autoMirror = image.autoMirror,
    content = { _, _ -> RenderVectorGroup(group = image.root) }
)

@Composable
internal fun DefaultNavIcon(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier
            .size(32.dp)
            .clickable { onClick() },
        tint = textColor,
    )
}

@Composable
private fun DefaultDropdownIcon(
    imageVector: ImageVector,
    contentDescription: String,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(20.dp),
        tint = textColor,
    )
}

@Composable
internal fun DropdownRow(
    text: String,
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DefaultDropdownIcon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                )
                Text(text = text)
            }
        },
        onClick = onClick,
    )
}

//endregion

//region toggle

@Composable
internal fun OptionToggle(
    optionName: String,
    initialValue: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    var checked by remember { mutableStateOf(initialValue) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = optionName.colon())

        Switch(
            checked = checked,
            onCheckedChange = { value ->
                checked = value
                onCheckedChange(value)
            },
        )
    }
}

//endregion
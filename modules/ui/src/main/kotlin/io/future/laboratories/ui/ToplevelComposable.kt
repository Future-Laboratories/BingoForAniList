package io.future.laboratories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.common.plus
import io.future.laboratories.ui.components.DefaultNavIcon
import io.future.laboratories.ui.components.DropdownRow
import io.future.laboratories.ui.components.ProfileButton

public data class DropDownItemData(
    private val textId: () -> Int,
    private val contentDescription: String? = null,
    private val imageVector: ImageVector,
    private val isVisible: () -> Boolean,
    private val onClick: () -> Unit,
) {
    @Composable
    public operator fun invoke(onDismiss: () -> Unit) {
        if (isVisible()) {
            val text = stringResource(textId())
            DropdownRow(
                text = text,
                imageVector = imageVector,
                contentDescription = contentDescription ?: text,
                onClick = onClick + onDismiss,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun CustomScaffold(
    titleId: () -> Int,
    isNavVisible: () -> Boolean,
    onNavPress: () -> Unit,
    profilePictureUrl: String?,
    isLoggedIn: Boolean,
    vararg dropDownItems: DropDownItemData,
    content: @Composable ColumnScope.() -> Unit,
): Unit = Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
        TopAppBar(
            title = { Text(text = stringResource(id = titleId())) },
            navigationIcon = {
                if (isNavVisible()) {
                    DefaultNavIcon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        onClick = onNavPress,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = StyleProvider.gradientColor,
                titleContentColor = StyleProvider.onGradientColor,
                scrolledContainerColor = StyleProvider.gradientColor,
                navigationIconContentColor = StyleProvider.onGradientColor,
                actionIconContentColor = StyleProvider.onGradientColor,
            ),
            actions = {
                var showMenu by rememberSaveable { mutableStateOf(false) }
                ProfileButton(
                    url = profilePictureUrl,
                    isLoggedIn = isLoggedIn,
                    onClick = {
                        showMenu = !showMenu
                    }
                )

                DropdownMenu(
                    modifier = Modifier.background(StyleProvider.surfaceColor),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    dropDownItems.forEach { dropdownItem ->
                        dropdownItem {
                            showMenu = false
                        }
                    }
                }
            }
        )
    },
    contentWindowInsets = WindowInsets(
        left = 10.dp,
        top = 10.dp,
        right = 10.dp,
        bottom = 10.dp,
    ),
) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(top = 10.dp),
        content = content,
    )
}
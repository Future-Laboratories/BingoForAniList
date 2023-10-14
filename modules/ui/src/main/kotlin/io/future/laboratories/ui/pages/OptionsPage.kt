package io.future.laboratories.ui.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import io.future.laboratories.ui.components.BackButton

@Composable
public fun OptionsPage(
    onBackButtonPress: () -> Unit,
) {
    LazyColumn {
        item {
            BackButton(onClick = onBackButtonPress)
        }
    }
}
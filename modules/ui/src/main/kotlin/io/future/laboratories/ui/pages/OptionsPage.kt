package io.future.laboratories.ui.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.components.OptionGroup

@Composable
public fun OptionsPage(
    vararg options: OptionGroup,
) {
    LazyColumn {
        options.forEach { group ->
            item {
                DefaultHeader(group.text)
            }

            items(items = group.options) { item ->
                item()
            }
        }
    }
}
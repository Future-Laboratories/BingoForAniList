package io.future.laboratories.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.future.laboratories.ui.R
import io.future.laboratories.ui.colon
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.components.OptionGroup
import io.future.laboratories.ui.components.TextRow
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
public fun ColumnScope.OptionsPage(
    timestamp: Long,
    version: String,
    vararg options: OptionGroup,
) {
    LazyColumn(modifier = Modifier.weight(1f)) {
        options
            .filter{ it.isVisible?.invoke() != false }
            .forEach { group ->
            item {
                DefaultHeader(title = group.text)
            }

            items(items = group.options) { item ->
                item()
            }
        }
    }

    Column {
        TextRow(
            text = stringResource(id = R.string.version).colon(),
            value = version,
        )

        TextRow(
            text = stringResource(id = R.string.options_expire_date).colon(),
            value = getDateTimeInstance().format(Date(timestamp)).toString(),
        )
    }
}
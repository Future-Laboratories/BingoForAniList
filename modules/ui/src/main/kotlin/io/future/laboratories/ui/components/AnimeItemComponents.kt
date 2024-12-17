package io.future.laboratories.ui.components

import android.R.attr.label
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import eu.wewox.textflow.material3.TextFlow
import io.future.laboratories.anilistapi.data.DetailedMediaTag
import io.future.laboratories.anilistapi.data.FuzzyDate
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.R
import io.future.laboratories.ui.colon
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties

@Composable
internal fun GeneralInfo(
    imageURL: String,
    title: String,
    body: String?,
) {
    StyledContainer(modifier = Modifier.fillMaxWidth()) {
        DefaultHeader("General Info")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = TextDecoration.Underline,
                )

                if (body != null) {
                    TextFlow(
                        modifier = Modifier.fillMaxWidth(),
                        text = HtmlCompat.fromHtml(body, 0).toString(),
                        obstacleContent = {
                            AsyncImage(
                                modifier = Modifier
                                    .width(200.dp)
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageURL)
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun AnimeReleaseInfo(
    episodes: Int?,
    duration: Int?,
    startDate: FuzzyDate?,
    endDate: FuzzyDate?,
) {
    fun getDateString(date: FuzzyDate?): String {
        return "${date?.day ?: ""}.${date?.month ?: ""}.${date?.year ?: ""}"
    }

    StyledContainer(modifier = Modifier.fillMaxWidth()) {
        DefaultHeader("Anime Release Info")

        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoRow(
                valueAvailable = episodes != null,
                label = stringResource(R.string.episodes),
                info ="${episodes.toString()} ${stringResource(R.string.episodes)}"
            )

            InfoRow(
                valueAvailable = duration != null,
                label = stringResource(R.string.duration),
                info = stringResource(R.string.minutes, duration ?: 0)
            )

            InfoRow(
                valueAvailable = startDate != null,
                label = stringResource(R.string.date_start),
                info = getDateString(startDate)
            )

            InfoRow(
                valueAvailable = endDate != null,
                label = stringResource(R.string.date_end),
                info = getDateString(endDate)
            )
        }
    }
}

@Composable
internal fun InfoRow(
    valueAvailable: Boolean = true,
    label: String,
    info: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label.colon())

        Text(if (valueAvailable) info else "N/A")
    }
}

@Composable
internal fun AnimeTagsCharts(
    tags: List<DetailedMediaTag>,
) {
    val color = StyleProvider.containerHorizontalGradient
    val tagCount by remember { mutableIntStateOf(tags.size.coerceAtMost(15)) }
    val barHeight by remember { mutableStateOf(15.dp) }
    val barSpacing by remember { mutableStateOf(5.dp) }
    val height by remember { mutableStateOf((barHeight + barSpacing) * tagCount) }

    fun defaultBar(
        label: String,
        value: Int = 0,
    ): Bars {
        return Bars(
            label = label,
            values = listOf(
                Bars.Data(label = label, value = value.toDouble(), color = color),
            )
        )
    }
    StyledContainer(modifier = Modifier) {
        DefaultHeader("Anime Tags")

        RowChart(
            modifier = Modifier
                .height(height)
                .padding(vertical = 12.dp, horizontal = 22.dp),
            indicatorProperties = VerticalIndicatorProperties(enabled = false),
            labelHelperProperties = LabelHelperProperties(enabled = false),
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
            ),
            data = remember {
                tags.take(tagCount).map { tag -> defaultBar(label = tag.name, value = tag.rank) }
            },
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 10.dp, bottomRight = 10.dp),
                spacing = 10.dp,
                thickness = barHeight
            ),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            ),
            maxValue = 100.0
        )
    }
}
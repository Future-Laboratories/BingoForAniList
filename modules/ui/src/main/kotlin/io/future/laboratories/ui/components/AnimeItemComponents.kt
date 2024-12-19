package io.future.laboratories.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
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
import io.future.laboratories.anilistapi.data.ScoreDistribution
import io.future.laboratories.anilistapi.data.StatusDistribution
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.R
import io.future.laboratories.ui.colon
import io.future.laboratories.ui.percentage
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties

@Composable
internal fun GeneralInfo(
    imageURL: String,
    bannerImage: String?,
    title: String,
    body: String?,
) {
    StyledContainer(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DefaultHeader("General Info")

            if (bannerImage != null) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 100.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bannerImage)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
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
}

@Composable
internal fun AnimeReleaseInfo(
    episodes: Int?,
    duration: Int?,
    startDate: FuzzyDate?,
    endDate: FuzzyDate?,
) {
    StyledContainer(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    info = "${episodes.toString()} ${stringResource(R.string.episodes)}"
                )

                InfoRow(
                    valueAvailable = duration != null,
                    label = stringResource(R.string.duration),
                    info = stringResource(R.string.minutes, duration ?: 0)
                )

                InfoRow(
                    valueAvailable = startDate != null,
                    label = stringResource(R.string.date_start),
                    info = startDate.toString()
                )

                InfoRow(
                    valueAvailable = endDate != null,
                    label = stringResource(R.string.date_end),
                    info = endDate.toString()
                )
            }
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
    val barSpacing by remember { mutableStateOf(10.dp) }
    val height by remember { mutableStateOf(((barHeight + barSpacing) * tagCount).coerceAtLeast(100.dp)) }

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
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    tags.take(tagCount)
                        .map { tag -> defaultBar(label = tag.name, value = tag.rank) }
                },
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(
                        topRight = 10.dp,
                        bottomRight = 10.dp
                    ),
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
}

@Composable
internal fun ScoreCharts(
    scoreDistribution: List<ScoreDistribution>?,
    averageScore: Float?,
    meanScore: Float?,
    popularity: Int?,
) {
    val color = StyleProvider.containerVerticalGradient

    StyledContainer(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DefaultHeader("Score Info")

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

            if (scoreDistribution != null) {
                ColumnChart(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(vertical = 12.dp, horizontal = 22.dp),
                    indicatorProperties = HorizontalIndicatorProperties(enabled = false),
                    labelHelperProperties = LabelHelperProperties(enabled = false),
                    labelProperties = LabelProperties(
                        enabled = true,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    ),
                    data = remember {
                        scoreDistribution.map { distribution ->
                            defaultBar(
                                label = distribution.score.toString(),
                                value = distribution.amount,
                            )
                        }
                    },
                    barProperties = BarProperties(
                        cornerRadius = Bars.Data.Radius.Rectangle(
                            topLeft = 10.dp,
                            topRight = 10.dp,
                        ),
                        spacing = 5.dp,
                        thickness = 20.dp,
                    ),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoRow(
                    valueAvailable = averageScore != null,
                    label = stringResource(R.string.score_average),
                    info = averageScore.toString().percentage()
                )

                InfoRow(
                    valueAvailable = meanScore != null,
                    label = stringResource(R.string.score_mean),
                    info = meanScore.toString().percentage()
                )

                InfoRow(
                    valueAvailable = popularity != null,
                    label = stringResource(R.string.popularity),
                    info = popularity.toString()
                )
            }
        }
    }
}

@Composable
internal fun WatchStatusChart(
    status: List<StatusDistribution>?,
) {
    if (status.isNullOrEmpty()) return

    val alphaColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
    val colorList = listOf(
        alphaColor.compositeOver(Color(0xFF00FF00)),
        alphaColor.compositeOver(Color(0xFF009999)),
        alphaColor.compositeOver(Color(0xFF660000)),
        alphaColor.compositeOver(Color(0xFF630488)),
        alphaColor.compositeOver(Color(0xFFDF3378)),
    )
    var data by remember {
        mutableStateOf(
            status.mapIndexed { index, statusDistribution ->
                Pie(
                    label = statusDistribution.status.value,
                    data = statusDistribution.amount.toDouble(),
                    color = colorList[index]
                )
            }
        )
    }

    StyledContainer(modifier = Modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DefaultHeader("Watch Status")

            PieChart(
                modifier = Modifier
                    .padding(20.dp)
                    .size(250.dp),
                data = data,
                onPieClick = {
                    val pieIndex = data.indexOf(it)
                    data = data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                },
                selectedScale = 1.2f,
                scaleAnimEnterSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                spaceDegree = 0f,
                selectedPaddingDegree = 0f,
                style = Pie.Style.Stroke(width = 30.dp),
            )

            SelectedLabel(pie = data.firstOrNull { it.selected })

            LegendItems(items = data)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectedLabel(
    pie: Pie?,
) {
    if(pie == null) return

    LabelRow(
        pie = pie,
        text = "${pie.label.orEmpty().colon()} ${pie.data.toInt()}"
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LegendItems(
    items: List<Pie>,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items.forEach { pie ->
            LabelRow(pie = pie)
        }
    }
}

@Composable
private fun LabelRow(
    pie: Pie,
    text: String = pie.label.orEmpty(),
) {
    Row {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(pie.color)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = text)
    }
}
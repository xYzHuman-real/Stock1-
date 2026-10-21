package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimeRange
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketRed
import com.example.ui.theme.MarketRedBg
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

enum class ChartDisplayMode(val label: String) {
    VICO_LINE("Smooth Line"),
    CANDLESTICK("Derived Candles"),
    PULSE_FLOW("Live Scrub")
}

@Composable
fun UpgradedStockDetailChart(
    historicalPoints: List<Double>,
    currentPrice: Double,
    currency: String,
    selectedTimeRange: TimeRange,
    onSelectTimeRange: (TimeRange) -> Unit,
    isPositive: Boolean = true,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var displayMode by remember { mutableStateOf(ChartDisplayMode.VICO_LINE) }
    var selectedIndex by remember(historicalPoints) { mutableStateOf<Int?>(null) }

    val activeColor = if (isPositive) PurplePrimary else MarketRed
    val gradientColor = if (isPositive) PurpleSoftLavender else MarketRedBg

    val displayPrice = selectedIndex?.let { historicalPoints.getOrNull(it) } ?: currentPrice
    val startPrice = historicalPoints.firstOrNull() ?: currentPrice
    val periodDelta = displayPrice - startPrice
    val periodDeltaPercent = if (startPrice > 0) (periodDelta / startPrice) * 100.0 else 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("upgraded_stock_detail_chart")
    ) {
        // Time Range Selectors Row (1D, 1W, 1M, 1Y, ALL)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF6F4FA))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimeRange.values().forEach { range ->
                val isSelected = selectedTimeRange == range
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PurplePrimary else Color.Transparent)
                        .clickable {
                            selectedIndex = null
                            onSelectTimeRange(range)
                        }
                        .padding(vertical = 7.dp)
                        .testTag("chart_range_${range.label}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = range.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price Header & Mode Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$currency%,.2f".format(displayPrice),
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selectedIndex != null) {
                            "Scrub point #${selectedIndex!! + 1} of ${historicalPoints.size}"
                        } else {
                            "Current Simulated Price"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )

                    if (selectedIndex != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        val isGain = periodDelta >= 0
                        Text(
                            text = "${if (isGain) "+" else ""}$currency%,.2f (${"%.2f".format(periodDeltaPercent)}%)".format(periodDelta),
                            color = if (isGain) MarketGreen else MarketRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Chart Mode Switcher Pills
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F0FA))
                    .padding(3.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    ChartDisplayMode.values().forEach { mode ->
                        val isModeSelected = displayMode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isModeSelected) PurplePrimary else Color.Transparent)
                                .clickable { displayMode = mode }
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (mode) {
                                    ChartDisplayMode.VICO_LINE -> Icons.Default.QueryStats
                                    ChartDisplayMode.CANDLESTICK -> Icons.Default.CandlestickChart
                                    ChartDisplayMode.PULSE_FLOW -> Icons.Default.ShowChart
                                },
                                contentDescription = mode.label,
                                tint = if (isModeSelected) Color.White else TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Chart Canvas Container / States
        when {
            // Loading State
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFAF9FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = PurplePrimary,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Loading ${selectedTimeRange.label} simulated history...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Error State
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEF2F2))
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MarketRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Failed to load chart points",
                            fontWeight = FontWeight.Bold,
                            color = MarketRed,
                            fontSize = 13.sp
                        )
                        Text(
                            text = errorMessage,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Empty State (Insufficient History)
            historicalPoints.size < 2 -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFAF9FD))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PurpleSoftLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Insufficient Historical Data",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The Stock1 simulator records historical points progressively. It avoids inventing artificial prices when actual recorded steps are insufficient for ${selectedTimeRange.label}.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Mode 1: Smooth Compose line chart using recorded simulated samples.
            displayMode == ChartDisplayMode.VICO_LINE -> {
                SmoothLineCanvas(
                    historicalPoints = historicalPoints,
                    isPositive = isPositive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            // Mode 2: Japanese Candlestick Chart
            displayMode == ChartDisplayMode.CANDLESTICK -> {
                CandlestickCanvas(
                    historicalPoints = historicalPoints,
                    isPositive = isPositive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            // Mode 3: Pulse Flow Chart with Live Scrubbing
            displayMode == ChartDisplayMode.PULSE_FLOW -> {
                PulseFlowCanvas(
                    historicalPoints = historicalPoints,
                    isPositive = isPositive,
                    selectedIndex = selectedIndex,
                    onSelectIndex = { selectedIndex = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }
        }

        if (displayMode == ChartDisplayMode.CANDLESTICK) {
            Text(
                text = "Derived from recorded simulated prices; exchange OHLC data is not available.",
                fontSize = 10.sp,
                color = TextTertiary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Range Stats & Labels Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val minP = historicalPoints.minOrNull() ?: currentPrice
            val maxP = historicalPoints.maxOrNull() ?: currentPrice

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("Period Low", fontSize = 10.sp, color = TextTertiary)
                    Text("$currency%,.2f".format(minP), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text("Period High", fontSize = 10.sp, color = TextTertiary)
                    Text("$currency%,.2f".format(maxP), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${historicalPoints.size} Data Points",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                DemoDataBadge()
            }
        }
    }
}


@Composable
private fun SmoothLineCanvas(
    historicalPoints: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isPositive) PurplePrimary else MarketRed
    val areaColor = if (isPositive) PurpleSoftLavender else MarketRedBg

    Canvas(modifier = modifier) {
        if (historicalPoints.size < 2) return@Canvas
        val minValue = historicalPoints.minOrNull() ?: return@Canvas
        val maxValue = historicalPoints.maxOrNull() ?: return@Canvas
        val range = (maxValue - minValue).coerceAtLeast(0.01)
        val inset = 12.dp.toPx()
        val points = historicalPoints.mapIndexed { index, value ->
            val x = index.toFloat() / (historicalPoints.size - 1) * size.width
            val y = size.height - inset -
                ((value - minValue) / range).toFloat() * (size.height - inset * 2)
            Offset(x, y)
        }
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (index in 0 until points.lastIndex) {
                val a = points[index]
                val b = points[index + 1]
                val midX = (a.x + b.x) / 2f
                cubicTo(midX, a.y, midX, b.y, b.x, b.y)
            }
        }
        val areaPath = Path().apply {
            addPath(linePath)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(areaColor.copy(alpha = 0.45f), Color.Transparent),
                startY = 0f,
                endY = size.height
            )
        )
        drawPath(
            linePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
private fun CandlestickCanvas(
    historicalPoints: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val min = historicalPoints.minOrNull() ?: 1.0
    val max = historicalPoints.maxOrNull() ?: 2.0
    val range = (max - min).coerceAtLeast(1.0)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val candleCount = historicalPoints.size
        val barWidth = (width / candleCount) * 0.65f

        for (i in 0 until candleCount) {
            val pClose = historicalPoints[i]
            // This view has close samples only; derive open from the prior recorded close
            // and do not invent high/low wick values without OHLC source data.
            val pOpen = if (i > 0) historicalPoints[i - 1] else pClose
            val isBullish = pClose >= pOpen

            val highVal = maxOf(pOpen, pClose)
            val lowVal = minOf(pOpen, pClose)

            val yOpen = height - ((pOpen - min) / range * height).toFloat()
            val yClose = height - ((pClose - min) / range * height).toFloat()
            val yHigh = height - ((highVal - min) / range * height).toFloat()
            val yLow = height - ((lowVal - min) / range * height).toFloat()

            val x = (i.toFloat() / (candleCount - 1).coerceAtLeast(1)) * (width - barWidth) + (barWidth / 2f)
            val candleColor = if (isBullish) Color(0xFF10B981) else Color(0xFFEF4444)

            // Wick
            drawLine(
                color = candleColor,
                start = Offset(x, yHigh),
                end = Offset(x, yLow),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Body
            val top = minOf(yOpen, yClose)
            val bottom = maxOf(yOpen, yClose).coerceAtLeast(top + 3f)
            drawRect(
                color = candleColor,
                topLeft = Offset(x - barWidth / 2f, top),
                size = androidx.compose.ui.geometry.Size(barWidth, bottom - top)
            )
        }
    }
}

@Composable
private fun PulseFlowCanvas(
    historicalPoints: List<Double>,
    isPositive: Boolean,
    selectedIndex: Int?,
    onSelectIndex: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = if (isPositive) PurplePrimary else MarketRed
    val gradientColor = if (isPositive) PurpleSoftLavender else MarketRedBg

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_flow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(tween(1300), repeatMode = RepeatMode.Restart),
        label = "pulseAlpha"
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 13f,
        animationSpec = infiniteRepeatable(tween(1300), repeatMode = RepeatMode.Restart),
        label = "pulseRadius"
    )

    val min = historicalPoints.minOrNull() ?: 1.0
    val max = historicalPoints.maxOrNull() ?: 2.0
    val range = (max - min).coerceAtLeast(1.0)

    Canvas(
        modifier = modifier
            .pointerInput(historicalPoints) {
                detectTapGestures(
                    onPress = { offset ->
                        val idx = ((offset.x / size.width) * (historicalPoints.size - 1))
                            .toInt()
                            .coerceIn(0, historicalPoints.size - 1)
                        onSelectIndex(idx)
                    }
                )
            }
            .pointerInput(historicalPoints) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        val idx = ((change.position.x / size.width) * (historicalPoints.size - 1))
                            .toInt()
                            .coerceIn(0, historicalPoints.size - 1)
                        onSelectIndex(idx)
                    },
                    onDragEnd = { onSelectIndex(null) },
                    onDragCancel = { onSelectIndex(null) }
                )
            }
    ) {
        val width = size.width
        val height = size.height

        val points = historicalPoints.mapIndexed { i, value ->
            val x = (i.toFloat() / (historicalPoints.size - 1)) * width
            val y = height - ((value - min) / range * (height - 30.dp.toPx())).toFloat() - 15.dp.toPx()
            Offset(x, y)
        }

        if (points.size < 2) return@Canvas

        val fillPath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(gradientColor.copy(alpha = 0.45f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = strokePath,
            color = activeColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Live pulse indicator on the latest point if no scrub
        if (selectedIndex == null) {
            val lastPoint = points.last()
            drawCircle(
                color = activeColor.copy(alpha = pulseAlpha),
                radius = pulseRadius.dp.toPx(),
                center = lastPoint
            )
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = lastPoint
            )
            drawCircle(
                color = activeColor,
                radius = 3.5f.dp.toPx(),
                center = lastPoint
            )
        } else {
            val scrubPoint = points[selectedIndex]
            drawLine(
                color = PurplePrimary.copy(alpha = 0.5f),
                start = Offset(scrubPoint.x, 0f),
                end = Offset(scrubPoint.x, height),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
            drawCircle(
                color = PurplePrimary,
                radius = 6.dp.toPx(),
                center = scrubPoint
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = scrubPoint
            )
        }
    }
}

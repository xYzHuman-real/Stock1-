package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Instrument
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketGreenBg
import com.example.ui.theme.MarketRed
import com.example.ui.theme.MarketRedBg
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurpleGlassBorder
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shapeRadius: Dp = 20.dp,
    elevation: Dp = 1.dp,
    backgroundColor: Color = Color.White.copy(alpha = 0.94f),
    borderColor: Color = BorderSubtle,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(shapeRadius)
    val cardModifier = modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = PurpleDeep.copy(alpha = 0.05f),
            spotColor = PurplePrimary.copy(alpha = 0.08f)
        )
        .clip(shape)
        .background(backgroundColor)
        .border(0.8.dp, borderColor, shape)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun DemoDataBadge(
    text: String = "DEMO DATA",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(PurpleSoftLavender)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Demo data indicator",
            tint = PurpleDeep,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = text,
            color = PurpleDeep,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SimulatedNoticeBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFBEB))
            .border(0.8.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Notice",
            tint = Color(0xFFD97706),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "SIMULATED TRADING — NO REAL MONEY. Strictly for financial learning.",
            color = Color(0xFF92400E),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriceChangeBadge(
    change: Double,
    changePercent: Double,
    currency: String = "₹",
    modifier: Modifier = Modifier
) {
    val isPositive = change >= 0
    val bgColor = if (isPositive) MarketGreenBg else MarketRedBg
    val contentColor = if (isPositive) MarketGreen else MarketRed

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
            contentDescription = if (isPositive) "Price up" else "Price down",
            tint = contentColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        val sign = if (isPositive) "+" else ""
        Text(
            text = "$sign${"%.2f".format(changePercent)}%",
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MarketSparkline(
    data: List<Double>,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = if (isPositive) MarketGreen else MarketRed,
    strokeWidth: Float = 2f
) {
    if (data.size < 2) return

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val minVal = data.minOrNull() ?: 0.0
        val maxVal = data.maxOrNull() ?: 1.0
        val range = (maxVal - minVal).coerceAtLeast(0.001)

        val points = data.mapIndexed { index, value ->
            val x = (index.toFloat() / (data.size - 1)) * width
            val y = height - (((value - minVal) / range).toFloat() * (height - 4f)) - 2f
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cx = (p0.x + p1.x) / 2
                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun InteractiveStockChart(
    historicalPoints: List<Double>,
    currentPrice: Double,
    currency: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true
) {
    if (historicalPoints.size < 2) return

    var selectedIndex by remember(historicalPoints) { mutableStateOf<Int?>(null) }
    var isCandlestickMode by remember { mutableStateOf(false) }

    val animationProgress = remember(historicalPoints) { Animatable(0.1f) }
    LaunchedEffect(historicalPoints) {
        animationProgress.snapTo(0.1f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    // Restrained live pulse animation for the latest ticker price
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )

    val activeColor = if (isPositive) PurplePrimary else MarketRed
    val gradientColor = if (isPositive) PurpleSoftLavender else MarketRedBg

    val displayPrice = selectedIndex?.let { historicalPoints.getOrNull(it) } ?: currentPrice

    Column(modifier = modifier.fillMaxWidth()) {
        // Active price scrub preview & chart style toggle
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
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (selectedIndex != null) "Scrubbing chart sample" else "Latest Simulated Value",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Line / Candle Switcher
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3F0FA))
                        .padding(2.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isCandlestickMode) PurplePrimary else Color.Transparent)
                                .clickable { isCandlestickMode = false }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Line Chart",
                                tint = if (!isCandlestickMode) Color.White else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCandlestickMode) PurplePrimary else Color.Transparent)
                                .clickable { isCandlestickMode = true }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CandlestickChart,
                                contentDescription = "Candlestick Chart",
                                tint = if (isCandlestickMode) Color.White else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                DemoDataBadge()
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .pointerInput(historicalPoints) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            selectedIndex = (fraction * (historicalPoints.size - 1)).toInt()
                        },
                        onDragEnd = { selectedIndex = null },
                        onDragCancel = { selectedIndex = null },
                        onDrag = { change, _ ->
                            change.consume()
                            val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                            selectedIndex = (fraction * (historicalPoints.size - 1)).toInt()
                        }
                    )
                }
                .pointerInput(historicalPoints) {
                    detectTapGestures(
                        onPress = { offset ->
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            selectedIndex = (fraction * (historicalPoints.size - 1)).toInt()
                            tryAwaitRelease()
                            selectedIndex = null
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height

            val minVal = historicalPoints.minOrNull() ?: 0.0
            val maxVal = historicalPoints.maxOrNull() ?: 1.0
            val range = (maxVal - minVal).coerceAtLeast(0.001)

            val points = historicalPoints.mapIndexed { index, value ->
                val x = (index.toFloat() / (historicalPoints.size - 1)) * width
                val y = height - (((value - minVal) / range).toFloat() * (height - 24f)) - 12f
                Offset(x, y)
            }

            val animFactor = animationProgress.value

            if (isCandlestickMode) {
                // Render Japanese Candlesticks from data steps with animated reveal
                val candleCount = historicalPoints.size
                val visibleCount = (candleCount * animFactor).toInt().coerceAtLeast(1)
                val barWidth = (width / candleCount) * 0.65f

                for (i in 0 until visibleCount) {
                    val prevPrice = if (i > 0) historicalPoints[i - 1] else historicalPoints[i]
                    val currPrice = historicalPoints[i]
                    val openPrice = prevPrice
                    val closePrice = currPrice
                    val highPrice = maxOf(openPrice, closePrice) * (1.0 + (Math.abs(Math.sin(i * 1.3)) * 0.003))
                    val lowPrice = minOf(openPrice, closePrice) * (1.0 - (Math.abs(Math.cos(i * 1.1)) * 0.003))

                    val cx = (i.toFloat() / (candleCount - 1)) * width
                    val openY = height - (((openPrice - minVal) / range).toFloat() * (height - 24f)) - 12f
                    val closeY = height - (((closePrice - minVal) / range).toFloat() * (height - 24f)) - 12f
                    val highY = height - (((highPrice - minVal) / range).toFloat() * (height - 24f)) - 12f
                    val lowY = height - (((lowPrice - minVal) / range).toFloat() * (height - 24f)) - 12f

                    val isGreen = closePrice >= openPrice
                    val candleColor = if (isGreen) MarketGreen else MarketRed

                    // Wick
                    drawLine(
                        color = candleColor,
                        start = Offset(cx, highY),
                        end = Offset(cx, lowY),
                        strokeWidth = 1.5f
                    )

                    // Body
                    val topY = minOf(openY, closeY)
                    val bodyHeight = Math.max(Math.abs(closeY - openY), 3f)
                    drawRoundRect(
                        color = candleColor,
                        topLeft = Offset(cx - (barWidth / 2f), topY),
                        size = androidx.compose.ui.geometry.Size(barWidth, bodyHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                    )
                }
            } else {
                val visibleWidth = (width * animFactor).coerceAtLeast(2f)

                // Line Chart with smooth Bézier curve and gradient fill clipped by animation
                clipRect(left = 0f, top = 0f, right = visibleWidth, bottom = height) {
                    val strokePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx = (p0.x + p1.x) / 2
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }

                    // Fill gradient below curve
                    val fillPath = Path().apply {
                        addPath(strokePath)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                activeColor.copy(alpha = 0.28f),
                                gradientColor.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Main line stroke
                    drawPath(
                        path = strokePath,
                        color = activeColor,
                        style = Stroke(
                            width = 2.8f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Animated Live Pulse Dot at the latest revealed point
                val targetIndex = ((points.size - 1) * animFactor).toInt().coerceIn(points.indices)
                val currentPoint = points[targetIndex]
                drawCircle(
                    color = activeColor.copy(alpha = pulseAlpha),
                    radius = pulseRadius,
                    center = currentPoint
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = currentPoint
                )
                drawCircle(
                    color = activeColor,
                    radius = 3.5f,
                    center = currentPoint
                )
            }

            // Draw scrubbing cursor if user touches
            selectedIndex?.let { index ->
                if (index in points.indices) {
                    val p = points[index]
                    drawLine(
                        color = PurplePrimary.copy(alpha = 0.45f),
                        start = Offset(p.x, 0f),
                        end = Offset(p.x, height),
                        strokeWidth = 1.2f
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = p
                    )
                    drawCircle(
                        color = PurplePrimary,
                        radius = 4f,
                        center = p
                    )
                }
            }
        }
    }
}

@Composable
fun Stock1Header(
    onAiTutorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Stock",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "1",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = PurplePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                DemoDataBadge()
            }
            Text(
                text = "Explore Markets. Master Money.",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        // Quick AI Financial Tutor Pill Button
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onAiTutorClick)
                .testTag("open_ai_tutor_button"),
            color = PurpleSoftLavender,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PurpleGlassBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Tutor",
                    tint = PurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI Tutor",
                    color = PurpleDeep,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

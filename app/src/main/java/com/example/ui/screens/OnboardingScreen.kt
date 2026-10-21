package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.PriceChangeBadge
import com.example.ui.components.SimulatedNoticeBanner
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketGreenBg
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurpleGlassBorder
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

data class OnboardingPageData(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val explanation: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = 3

    // Handle system back button: go back to previous page if > 0
    BackHandler(enabled = currentPage > 0) {
        currentPage -= 1
    }

    val pages = listOf(
        OnboardingPageData(
            stepIndex = 0,
            title = "The World of Markets, Simplified.",
            description = "Explore stocks, market indices, currencies, commodities, and more—all in one place.",
            explanation = "Financial markets are places where people and organisations buy and sell financial assets. Learning how they work helps you understand money, risk, and investment decisions."
        ),
        OnboardingPageData(
            stepIndex = 1,
            title = "Learn by Doing.",
            description = "Build confidence with virtual money before making real-world financial decisions.",
            explanation = "Practise simulated buying and selling, track a virtual portfolio, and understand potential gains and losses without risking real money. Markets move with economic news and corporate earnings—simulation gives you a safe space to learn."
        ),
        OnboardingPageData(
            stepIndex = 2,
            title = "Your Financial Journey Starts Here.",
            description = "Learn essential concepts, understand market movements, and test your knowledge at your own pace.",
            explanation = "Master fundamental & technical analysis, take bite-sized quizzes, and consult the AI Financial Tutor whenever you encounter complex financial concepts."
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White,
                        Color(0xFFFAF8FF),
                        Color(0xFFF4F0FE)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Top Navigation Bar (Branding + Skip)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wordmark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = PurplePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DemoDataBadge("LEARNING PLATFORM")
                }

                // Skip button on all pages
                TextButton(
                    onClick = onCompleteOnboarding,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PurplePrimary
                    )
                }
            }

            // Main Content Area with Animated Transition
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "onboarding_page_transition"
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual Card corresponding to the screen
                        when (pageIndex) {
                            0 -> Screen1VisualCard()
                            1 -> Screen2VisualCard()
                            2 -> Screen3VisualCard()
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Title
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Description
                        Text(
                            text = page.description,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Beginner-friendly Explanation Glass Card
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shapeRadius = 18.dp,
                            backgroundColor = Color.White.copy(alpha = 0.90f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = PurplePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DID YOU KNOW?",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PurpleDeep,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = page.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            // Bottom Navigation Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp, top = 8.dp)
            ) {
                // Page Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalPages) {
                        val isSelected = i == currentPage
                        val widthAnim by animateDpAsState(
                            targetValue = if (isSelected) 28.dp else 8.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "indicator_width"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(widthAnim)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) PurplePrimary else Color(0xFFDDD7F0))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button (hidden on page 0)
                    if (currentPage > 0) {
                        OutlinedButton(
                            onClick = { currentPage -= 1 },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("onboarding_back_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Back",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Next or Get Started Button
                    Button(
                        onClick = {
                            if (currentPage < totalPages - 1) {
                                currentPage += 1
                            } else {
                                onCompleteOnboarding()
                            }
                        },
                        modifier = Modifier
                            .weight(if (currentPage > 0) 1.5f else 1f)
                            .height(50.dp)
                            .testTag(if (currentPage == totalPages - 1) "onboarding_get_started_button" else "onboarding_next_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurplePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (currentPage == totalPages - 1) "Get Started" else "Next",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// Visual Card for Screen 1: Discover Financial Markets
@Composable
private fun Screen1VisualCard() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shapeRadius = 24.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PurpleSoftLavender.copy(alpha = 0.5f),
                            Color.White
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Global & Indian Markets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "NSE • BSE • NASDAQ • FX",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                PriceChangeBadge(change = 285.40, changePercent = 1.16)
            }

            // Visual market trend curve with candlesticks
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            ) {
                val width = size.width
                val height = size.height

                // Draw mock candlesticks in background
                val candleColors = listOf(MarketGreen, MarketGreen, Color(0xFFDC2626), MarketGreen, MarketGreen)
                val candlePositions = listOf(0.15f, 0.35f, 0.55f, 0.75f, 0.90f)
                val candleTops = listOf(0.55f, 0.40f, 0.60f, 0.30f, 0.18f)

                candlePositions.forEachIndexed { i, pos ->
                    val cx = width * pos
                    val topY = height * candleTops[i]
                    val color = candleColors[i]

                    // Wick
                    drawLine(
                        color = color.copy(alpha = 0.5f),
                        start = Offset(cx, topY - 12f),
                        end = Offset(cx, topY + 28f),
                        strokeWidth = 2f
                    )
                    // Body
                    drawRoundRect(
                        color = color.copy(alpha = 0.85f),
                        topLeft = Offset(cx - 7f, topY),
                        size = androidx.compose.ui.geometry.Size(14f, 18f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                    )
                }

                // Trend curve
                val path = Path().apply {
                    moveTo(0f, height * 0.75f)
                    cubicTo(
                        width * 0.25f, height * 0.70f,
                        width * 0.45f, height * 0.35f,
                        width * 0.70f, height * 0.45f
                    )
                    cubicTo(
                        width * 0.82f, height * 0.50f,
                        width * 0.92f, height * 0.18f,
                        width, height * 0.12f
                    )
                }

                drawPath(
                    path = path,
                    color = PurplePrimary,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )

                // Glow point at tip
                drawCircle(color = Color.White, radius = 6f, center = Offset(width, height * 0.12f))
                drawCircle(color = PurplePrimary, radius = 4f, center = Offset(width, height * 0.12f))
            }

            // Asset class pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("NIFTY 50", "Equities", "ETFs", "Gold", "Crypto").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1ECFF))
                            .border(0.5.dp, PurpleGlassBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                }
            }
        }
    }
}

// Visual Card for Screen 2: Practise Without Risk
@Composable
private fun Screen2VisualCard() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shapeRadius = 24.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF7F5FE),
                            Color.White
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PurpleDeep),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Virtual Paper Trading",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Zero Financial Risk",
                            style = MaterialTheme.typography.labelSmall,
                            color = MarketGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                DemoDataBadge("100% SIMULATED")
            }

            // Virtual Balance Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Simulated Starting Cash", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            text = "₹10,00,000.00",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MarketGreenBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+₹18,450 (Today)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MarketGreen
                        )
                    }
                }
            }

            // Simulated Safety Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFFBEB))
                    .border(0.8.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "No real money required. Practise investing with zero real risk.",
                    color = Color(0xFF92400E),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Visual Card for Screen 3: Learn. Explore. Grow.
@Composable
private fun Screen3VisualCard() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shapeRadius = 24.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PurpleSoftLavender.copy(alpha = 0.5f),
                            Color.White
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Finance Academy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "14 Lessons & Quizzes",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // AI Tutor badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleSoftLavender)
                        .border(1.dp, PurpleGlassBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Tutor Ready",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                }
            }

            // Featured Lesson Snapshot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MarketGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lesson 1: What is the Stock Market?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Understand shares, IPOs & exchanges",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1ECFF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Quiz Included",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                }
            }

            // AI Question pill teaser
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF6F4FA))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "💬 \"Explain P/E ratio simply\"",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF6F4FA))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "💬 \"What is NIFTY 50?\"",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}


@Composable
fun EmailPasswordLoginScreen(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var createAccount by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var demoEmail by remember { mutableStateOf<String?>(null) }
    var demoPassword by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color.White, Color(0xFFFAF8FF), Color(0xFFF4F0FE))
        )).statusBarsPadding().navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Stock1", fontSize = 36.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(if (createAccount) "Create your learning account" else "Welcome back", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text("Learn markets safely with simulated money.", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            androidx.compose.material3.OutlinedTextField(
                value = email, onValueChange = { email = it.trim(); error = null },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                label = { Text("Email address") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(12.dp))
            androidx.compose.material3.OutlinedTextField(
                value = password, onValueChange = { password = it; error = null },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                label = { Text("Password") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password),
                shape = RoundedCornerShape(16.dp)
            )
            if (error != null) {
                Spacer(Modifier.height(10.dp))
                Text(error!!, color = Color(0xFFB3261E), fontSize = 13.sp)
            }
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        error = "Enter a valid email address."
                    } else if (password.length < 6) {
                        error = "Password must be at least 6 characters."
                    } else {
                        busy = true
                        // DEMO ONLY: credentials are held in memory and are not sent to a server or saved.
                        if (createAccount) {
                            demoEmail = email
                            demoPassword = password
                            busy = false
                            createAccount = false
                            error = "Demo account created on this device session. Sign in with the same details."
                        } else if (email == demoEmail && password == demoPassword) {
                            busy = false
                            onAuthenticated()
                        } else {
                            busy = false
                            error = "Demo sign-in failed. Create a demo account first, then use the same email and password."
                        }
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text(if (busy) "Please wait…" else if (createAccount) "Create account" else "Sign in", fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { createAccount = !createAccount; error = null }) {
                Text(if (createAccount) "Already have an account? Sign in" else "New to Stock1? Create an account", color = PurpleDeep)
            }
            Text("DEMO AUTH ONLY · Credentials stay in temporary app memory and are lost when the app restarts. Do not use a real password.", fontSize = 11.sp, color = TextTertiary, textAlign = TextAlign.Center)
        }
    }
}

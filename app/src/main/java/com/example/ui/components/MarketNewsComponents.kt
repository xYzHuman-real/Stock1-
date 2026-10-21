package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NewsUiState
import com.example.data.model.MarketNewsItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MarketNewsSection(
    newsState: NewsUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedArticle by remember { mutableStateOf<MarketNewsItem?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    val categories = listOf("All", "Indian Markets", "Global Markets", "Commodities")

    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(PurpleSoftLavender),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = "News",
                        tint = PurplePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Market News & Insights",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        DemoDataBadge("DEMO FEED")
                    }
                    if (newsState is NewsUiState.Success) {
                        Text(
                            text = "Simulated feed • Updated at ${newsState.lastFetchedTime}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    isRefreshing = true
                    onRefresh()
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, BorderSubtle, CircleShape)
                    .testTag("refresh_news_button")
            ) {
                val isSpinning = newsState is NewsUiState.Loading || isRefreshing
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh News",
                    tint = PurplePrimary,
                    modifier = Modifier
                        .size(18.dp)
                        .then(if (isSpinning) Modifier.rotate(spinAngle) else Modifier)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategoryFilter == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PurplePrimary else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) PurplePrimary else BorderSubtle,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedCategoryFilter = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // News Content State
        when (newsState) {
            is NewsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = PurplePrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Updating market briefings...",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            is NewsUiState.Success -> {
                val filtered = if (selectedCategoryFilter == "All") {
                    newsState.items
                } else {
                    newsState.items.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filtered.forEach { item ->
                        NewsCard(
                            item = item,
                            onClick = { selectedArticle = item }
                        )
                    }
                }
            }

            is NewsUiState.Error -> {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shapeRadius = 14.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Could not load news feed",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = onRefresh) {
                            Text("Try Again", color = PurplePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            is NewsUiState.Offline -> {
                // Not applicable in prototype
            }
        }
    }

    // Article Reader Modal
    selectedArticle?.let { article ->
        ArticleDetailBottomSheet(
            article = article,
            onDismiss = { selectedArticle = null }
        )
    }
}

@Composable
private fun NewsCard(
    item: MarketNewsItem,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shapeRadius = 16.dp,
        backgroundColor = Color.White,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Source & Category & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PurpleSoftLavender)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.sourceName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                Text(
                    text = item.publishedTimeFormatted,
                    fontSize = 10.sp,
                    color = TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Headline
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Read More link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Read Full Analysis",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailBottomSheet(
    article: MarketNewsItem,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PurpleSoftLavender)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${article.category} • ${article.sourceName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDeep
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${article.publishedTimeFormatted} • Simulated Educational Feed",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Full Article Body
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Educational Context Takeaway Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 14.dp,
                backgroundColor = Color(0xFFFBF9FF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Why This Matters for Your Learning",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PurpleDeep
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    val takeaway = when (article.category) {
                        "Indian Markets" ->
                            "Domestic equity indices like NIFTY 50 reflect corporate earnings expectations and institutional capital inflows. Keeping an eye on RBI monetary decisions helps explain interest rate cycles and sector valuations."
                        "Global Markets" ->
                            "International tech equities drive broader risk sentiment and currency exchange rates. Global trends frequently influence Indian IT exporters and mutual fund foreign allocations."
                        "Commodities" ->
                            "Gold acts as a counter-cyclical hedge during inflationary periods, while crude oil directly impacts transportation costs and domestic consumer inflation."
                        else ->
                            "Macroeconomic events directly influence asset valuations across stocks, bonds, and commodities. Understanding how news catalysts move prices is fundamental to disciplined simulated trading."
                    }
                    Text(
                        text = takeaway,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Demo disclaimer badge
            DemoDataBadge("EDUCATIONAL DEMO HEADLINE — NOT REAL-TIME WIRE")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

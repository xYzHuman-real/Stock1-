package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NewsUiState
import com.example.data.PortfolioSummary
import com.example.data.model.Instrument
import com.example.data.model.MarketCategory
import com.example.data.model.TimeRange
import com.example.ui.AppDestination
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.InteractiveStockChart
import com.example.ui.components.MarketNewsSection
import com.example.ui.components.MarketSparkline
import com.example.ui.components.PriceChangeBadge
import com.example.ui.components.Stock1Header
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketRed
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.PurpleSurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.util.Calendar

@Composable
fun HomeScreen(
    portfolioSummary: PortfolioSummary,
    indices: List<Instrument>,
    watchlist: List<Instrument>,
    topMovers: List<Instrument>,
    newsState: NewsUiState,
    onRefreshNews: () -> Unit,
    activeAlertsCount: Int = 0,
    onOpenAlertsManager: () -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onInstrumentClick: (String) -> Unit,
    onCategoryClick: (MarketCategory) -> Unit,
    onAiTutorClick: () -> Unit,
    onOpenLesson: (String) -> Unit,
    watchlistIds: Set<String> = emptySet(),
    onToggleWatchlist: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedIndexTab by remember { mutableStateOf("idx_nifty50") }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ONE_DAY) }

    val activeIndex = indices.find { it.id == selectedIndexTab } ?: indices.firstOrNull()

    // Greeting logic
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Branding Header
        item {
            Stock1Header(onAiTutorClick = onAiTutorClick)
        }

        // Greeting & Search bar
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "$greeting, Investor 👋",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Markets are open in simulation mode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar Trigger
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar"),
                    onClick = { onNavigate(AppDestination.MARKETS) },
                    shapeRadius = 14.dp,
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search Indian stocks, US tech, ETFs, Gold...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    }
                }
            }
        }

        // Virtual Portfolio Summary Card
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 22.dp,
                    backgroundColor = Color.White,
                    onClick = { onNavigate(AppDestination.PORTFOLIO) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        PurpleSoftLavender.copy(alpha = 0.5f),
                                        Color.White
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PurplePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Virtual Portfolio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            DemoDataBadge("PAPER TRADING")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Total Portfolio Net Worth",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹%,.2f".format(portfolioSummary.totalPortfolioValue),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            PriceChangeBadge(
                                change = portfolioSummary.unrealizedPnL,
                                changePercent = portfolioSummary.unrealizedPnLPercent
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Cash vs Invested breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Available Virtual Cash", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "₹%,.2f".format(portfolioSummary.availableCash),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleDeep
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Invested in Holdings", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "₹%,.2f".format(portfolioSummary.investedValue),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Market Overview: Indices & Trend Chart
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Major Indian Indices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    DemoDataBadge()
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Indices mini cards row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    indices.forEach { idx ->
                        val isSelected = idx.id == selectedIndexTab
                        GlassCard(
                            modifier = Modifier.width(170.dp),
                            shapeRadius = 16.dp,
                            backgroundColor = if (isSelected) PurpleSoftLavender.copy(alpha = 0.8f) else Color.White,
                            borderColor = if (isSelected) PurplePrimary else BorderSubtle,
                            onClick = { selectedIndexTab = idx.id }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = idx.symbol,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "₹%,.2f".format(idx.currentPrice),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PriceChangeBadge(change = idx.change, changePercent = idx.changePercent)
                                    MarketSparkline(
                                        data = idx.sparkline,
                                        isPositive = idx.change >= 0,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Chart for selected index
                if (activeIndex != null) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shapeRadius = 20.dp,
                        backgroundColor = Color.White
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = activeIndex.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Sample Historical Movement",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }

                                // Time Range Selectors (1D, 1W, 1M, 1Y, ALL)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    TimeRange.values().forEach { tr ->
                                        val isTrSelected = selectedTimeRange == tr
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isTrSelected) PurplePrimary else Color(0xFFF3F0FA))
                                                .clickable { selectedTimeRange = tr }
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = tr.label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isTrSelected) Color.White else TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val chartData = activeIndex.historicalData[selectedTimeRange.label]
                                ?: activeIndex.sparkline

                            InteractiveStockChart(
                                historicalPoints = chartData,
                                currentPrice = activeIndex.currentPrice,
                                currency = activeIndex.currency,
                                isPositive = activeIndex.change >= 0
                            )
                        }
                    }
                }
            }
        }

        // Market Asset Categories Filter Pills
        item {
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                PaddingValues(horizontal = 20.dp).let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Explore Asset Classes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        TextButton(onClick = { onNavigate(AppDestination.MARKETS) }) {
                            Text("See All", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MarketCategory.values()) { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .clickable {
                                    onCategoryClick(cat)
                                    onNavigate(AppDestination.MARKETS)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Market Equities & Simulated Top Movers Section
        item {
            var selectedMoversTab by remember { mutableStateOf("Gainers") }
            val filteredInstruments = remember(topMovers, selectedMoversTab) {
                when (selectedMoversTab) {
                    "Gainers" -> topMovers.filter { it.category != MarketCategory.INDICES && it.change >= 0 }
                        .sortedByDescending { it.changePercent }.take(5)
                    "Losers" -> topMovers.filter { it.category != MarketCategory.INDICES && it.change < 0 }
                        .sortedBy { it.changePercent }.take(5)
                    else -> topMovers.filter { it.category != MarketCategory.INDICES }.take(5)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Market Equities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        DemoDataBadge()
                    }

                    // Filter Tabs: Gainers, All, Losers
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF3F0FA))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf("Gainers", "All", "Losers").forEach { tab ->
                            val isTabSelected = selectedMoversTab == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isTabSelected) PurplePrimary else Color.Transparent)
                                    .clickable { selectedMoversTab = tab }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 11.sp,
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isTabSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredInstruments.forEach { item ->
                        val isWatchlisted = watchlistIds.contains(item.id)
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_instrument_${item.symbol}"),
                            shapeRadius = 14.dp,
                            backgroundColor = Color.White,
                            onClick = { onInstrumentClick(item.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.symbol,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PurpleSoftLavender)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = item.exchange,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PurplePrimary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.name,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }

                                MarketSparkline(
                                    data = item.sparkline,
                                    isPositive = item.change >= 0,
                                    modifier = Modifier
                                        .width(55.dp)
                                        .height(26.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${item.currency}%,.2f".format(item.currentPrice),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    PriceChangeBadge(change = item.change, changePercent = item.changePercent)
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Direct Watchlist Action Button
                                IconButton(
                                    onClick = { onToggleWatchlist(item.id) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isWatchlisted) PurpleSoftLavender else Color(0xFFFAF9FD))
                                        .testTag("home_watchlist_toggle_${item.symbol}")
                                ) {
                                    Icon(
                                        imageVector = if (isWatchlisted) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                        contentDescription = "Watchlist toggle",
                                        tint = PurplePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Watchlist Preview
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Watchlist Preview",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    TextButton(onClick = { onNavigate(AppDestination.MARKETS) }) {
                        Text("Add Instruments", color = PurplePrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (watchlist.isEmpty()) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shapeRadius = 16.dp,
                        backgroundColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Watchlist is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the bookmark icon on any stock or ETF to monitor its prices here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        watchlist.take(4).forEach { item ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shapeRadius = 14.dp,
                                backgroundColor = Color.White,
                                onClick = { onInstrumentClick(item.id) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.symbol,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = item.name,
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            maxLines = 1
                                        )
                                    }

                                    MarketSparkline(
                                        data = item.sparkline,
                                        isPositive = item.change >= 0,
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(28.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${item.currency}%,.2f".format(item.currentPrice),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                        PriceChangeBadge(change = item.change, changePercent = item.changePercent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Price Target Alerts Entry Card
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_price_alerts_card"),
                    shapeRadius = 18.dp,
                    backgroundColor = Color.White,
                    onClick = onOpenAlertsManager
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PurpleSoftLavender),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Alerts",
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Price Target Alerts",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    if (activeAlertsCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PurplePrimary)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$activeAlertsCount Active",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (activeAlertsCount > 0)
                                        "Monitoring simulated market crossovers"
                                    else
                                        "Set target notifications on watchlist instruments",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open Alerts",
                            tint = PurplePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Market News & Financial Insights Section
        item {
            MarketNewsSection(
                newsState = newsState,
                onRefresh = onRefreshNews,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Daily Financial Learning Card
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 20.dp,
                    backgroundColor = Color.White,
                    onClick = { onOpenLesson("les_1") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        PurpleSoftLavender,
                                        Color(0xFFEDE9FE)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = PurpleDeep,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CONCEPT OF THE DAY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PurpleDeep,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "3 min read",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "What is the Stock Market? Understanding Public Ownership",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Discover how fractional corporate ownership works and why companies list shares through IPOs to fund growth.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Start Lesson & Quiz →",
                                style = MaterialTheme.typography.labelLarge,
                                color = PurplePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { onOpenLesson("les_1") },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Read Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PortfolioSummary
import com.example.data.model.Instrument
import com.example.data.model.TradeAction
import com.example.data.model.TransactionRecord
import com.example.ui.AppDestination
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.PriceChangeBadge
import com.example.ui.components.SimulatedNoticeBanner
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketRed
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurpleGlassBorder
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun PortfolioScreen(
    summary: PortfolioSummary,
    transactions: List<TransactionRecord>,
    instrumentsMap: Map<String, Instrument>,
    onInstrumentClick: (String) -> Unit,
    onOpenTradeForHolding: (Instrument) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onResetPortfolio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Holdings, 1: Order History
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MarketRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Reset Virtual Portfolio?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This will restore your virtual cash balance to ₹10,00,000 and clear all simulated holdings and transaction history. This action cannot be undone.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetPortfolio()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MarketRed)
                ) {
                    Text("Yes, Reset Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Virtual Portfolio",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Paper-trading simulation workspace",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("reset_portfolio_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Prominent Simulated Notice
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                SimulatedNoticeBanner()
            }
        }

        // Main Portfolio Summary Card
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 24.dp,
                    backgroundColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        PurpleSoftLavender.copy(alpha = 0.6f),
                                        Color.White
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL PORTFOLIO VALUE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleDeep,
                                letterSpacing = 0.5.sp
                            )
                            DemoDataBadge("VIRTUAL ASSETS")
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "₹%,.2f".format(summary.totalPortfolioValue),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Unrealised P&L: ",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                val pnlSign = if (summary.unrealizedPnL >= 0) "+₹%,.2f".format(summary.unrealizedPnL) else "-₹%,.2f".format(Math.abs(summary.unrealizedPnL))
                                Text(
                                    text = pnlSign,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.unrealizedPnL >= 0) MarketGreen else MarketRed
                                )
                            }
                            PriceChangeBadge(
                                change = summary.unrealizedPnL,
                                changePercent = summary.unrealizedPnLPercent
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Grid: Cash, Invested, Realized
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF8F6FD))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Available Cash", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "₹%,.2f".format(summary.availableCash),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleDeep
                                )
                            }
                            Column {
                                Text("Invested Capital", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "₹%,.2f".format(summary.investedValue),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Realised P&L", fontSize = 11.sp, color = TextSecondary)
                                val sign = if (summary.realizedPnL >= 0) "+₹%,.2f".format(summary.realizedPnL) else "-₹%,.2f".format(Math.abs(summary.realizedPnL))
                                Text(
                                    text = sign,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (summary.realizedPnL >= 0) MarketGreen else MarketRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Asset Allocation Visual Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp,
                    backgroundColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Asset Allocation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            val investedPct = if (summary.totalPortfolioValue > 0)
                                (summary.investedValue / summary.totalPortfolioValue) * 100.0
                            else 0.0
                            Text(
                                text = "${"%.1f".format(investedPct)}% Invested",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PurplePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual progress bar
                        val cashFraction = if (summary.totalPortfolioValue > 0)
                            (summary.availableCash / summary.totalPortfolioValue).toFloat().coerceIn(0f, 1f)
                        else 1f
                        val investedFraction = (1f - cashFraction).coerceIn(0f, 1f)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFFE9E5F5))
                        ) {
                            if (investedFraction > 0.005f) {
                                Box(
                                    modifier = Modifier
                                        .weight(investedFraction.coerceAtLeast(0.01f))
                                        .fillMaxSize()
                                        .background(PurplePrimary)
                                )
                            }
                            if (cashFraction > 0.005f) {
                                Box(
                                    modifier = Modifier
                                        .weight(cashFraction.coerceAtLeast(0.01f))
                                        .fillMaxSize()
                                        .background(Color(0xFF93C5FD))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(PurplePrimary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Holdings (${summary.holdings.size} positions)", fontSize = 11.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF93C5FD))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Virtual Cash", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Tabs: Holdings vs Order History
        item {
            Spacer(modifier = Modifier.height(12.dp))
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PurplePrimary,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Current Holdings (${summary.holdings.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) PurplePrimary else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Order History (${transactions.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) PurplePrimary else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Tab Content
        if (selectedTab == 0) {
            // HOLDINGS
            if (summary.holdings.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shapeRadius = 20.dp,
                        backgroundColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Holdings in Portfolio Yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Practise investing with your ₹10,00,000 virtual cash. Browse Indian blue-chips, ETFs, or global tech in the Markets explorer.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigate(AppDestination.MARKETS) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                            ) {
                                Text("Explore Markets to Invest", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(summary.holdings) { item ->
                    val inst = instrumentsMap[item.holding.instrumentId]
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shapeRadius = 18.dp,
                            backgroundColor = Color.White,
                            onClick = { onInstrumentClick(item.holding.instrumentId) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = item.holding.symbol,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${item.holding.quantity} units • Avg ${item.holding.currency}%,.2f".format(item.holding.averageBuyPrice),
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹%,.2f".format(item.currentValueRupees),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        val pnlSign = if (item.unrealizedPnL >= 0) "+₹%,.2f".format(item.unrealizedPnL) else "-₹%,.2f".format(Math.abs(item.unrealizedPnL))
                                        Text(
                                            text = "$pnlSign (${"%.2f".format(item.unrealizedPnLPercent)}%)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (item.unrealizedPnL >= 0) MarketGreen else MarketRed
                                        )
                                    }
                                }

                                if (inst != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = { onOpenTradeForHolding(inst) },
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, PurpleGlassBorder),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                        ) {
                                            Text("Trade", fontSize = 11.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ORDER HISTORY
            if (transactions.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shapeRadius = 20.dp,
                        backgroundColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Simulated Orders Yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your simulated buy and sell executions will be documented here with timestamps and execution prices.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(transactions) { tx ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shapeRadius = 16.dp,
                            backgroundColor = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (tx.action == TradeAction.BUY) Color(0xFFE6F7F0) else Color(0xFFFEECEB))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tx.action.label.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (tx.action == TradeAction.BUY) MarketGreen else MarketRed
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${tx.symbol} (${tx.quantity} units)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${tx.formattedDate} • ${tx.orderType.label}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹%,.2f".format(tx.totalAmount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "@ %,.2f".format(tx.executedPrice),
                                        fontSize = 11.sp,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

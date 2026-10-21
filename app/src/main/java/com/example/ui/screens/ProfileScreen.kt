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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PortfolioSummary
import com.example.ui.AppDestination
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketRed
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProfileScreen(
    portfolioSummary: PortfolioSummary,
    completedLessonsCount: Int,
    totalLessonsCount: Int,
    tradesCount: Int,
    watchlistCount: Int,
    onNavigate: (AppDestination) -> Unit,
    onOpenAiTutor: () -> Unit,
    onRevisitOnboarding: () -> Unit,
    onResetAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = "Reset All App Data?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This will reset your virtual portfolio cash back to ₹10,00,000, remove all simulated holdings, clear order history, and clear completed lesson badges. Continue?",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MarketRed)
                ) {
                    Text("Reset All Data", color = Color.White, fontWeight = FontWeight.Bold)
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
        // Top Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    text = "Profile & Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Account, preferences & simulation settings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Profile Avatar Card
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 22.dp,
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PurpleSoftLavender)
                                .border(1.5.dp, PurplePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PurpleDeep,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Market Apprentice",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val rank = when {
                                completedLessonsCount >= 6 -> "Advanced Market Strategist"
                                completedLessonsCount >= 3 -> "Intermediate Trader"
                                else -> "Level 1 Financial Learner"
                            }
                            Text(
                                text = rank,
                                fontSize = 12.sp,
                                color = PurplePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        DemoDataBadge("PROTOTYPE")
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricBox(
                        title = "Lessons Done",
                        value = "$completedLessonsCount / $totalLessonsCount",
                        sub = "${((completedLessonsCount.toFloat() / totalLessonsCount.coerceAtLeast(1)) * 100).toInt()}% Complete",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Trades Placed",
                        value = "$tradesCount",
                        sub = "Simulated Orders",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Watchlist",
                        value = "$watchlistCount",
                        sub = "Instruments",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Shortcuts
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "Learning & Tools",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp,
                    backgroundColor = Color.White
                ) {
                    Column {
                        ActionRow(
                            icon = Icons.Default.AutoAwesome,
                            title = "AI Financial Tutor",
                            subtitle = "Ask questions on market terminology & charts",
                            onClick = onOpenAiTutor
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))
                        ActionRow(
                            icon = Icons.Default.School,
                            title = "Finance Academy",
                            subtitle = "Continue 14 lessons & interactive quizzes",
                            onClick = { onNavigate(AppDestination.LEARN) }
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderSubtle))
                        ActionRow(
                            icon = Icons.Default.Bookmark,
                            title = "Saved Watchlist",
                            subtitle = "$watchlistCount tracked instruments",
                            onClick = { onNavigate(AppDestination.MARKETS) }
                        )
                    }
                }
            }
        }

        // Data Source & App Architecture Info
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text(
                    text = "System & Data Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            Text("Data Engine", fontSize = 12.sp, color = TextSecondary)
                            Text("Demo Feed v1.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurpleDeep)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Exchange Coverage", fontSize = 12.sp, color = TextSecondary)
                            Text("NSE, BSE, NASDAQ, MCX, FX", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Broker Integration", fontSize = 12.sp, color = TextSecondary)
                            Text("Paper Trading (No Real Money)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MarketGreen)
                        }
                    }
                }
            }
        }

        // Educational & Legal Disclaimers
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 16.dp,
                    backgroundColor = Color(0xFFFAF8FE)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Important Disclaimers",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleDeep
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Stock1 is exclusively an educational and simulated paper-trading platform.\n• All market prices, statistics, and charts are sample/demo figures.\n• No actual securities or currency transactions are executed. No real money is deposited, held, or traded.\n• Educational explanations and AI Tutor responses do NOT constitute financial, investment, legal, or tax advice.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Reset App Data Button
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_all_data_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEECEB),
                        contentColor = MarketRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset All App & Simulation Data",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Stock1 v1.0 • Built with Kotlin & Jetpack Compose",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shapeRadius = 14.dp,
        backgroundColor = Color.White
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, fontSize = 9.sp, color = PurpleDeep, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PurpleSoftLavender),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
        )
    }
}

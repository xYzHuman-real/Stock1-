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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Instrument
import com.example.data.model.MarketCategory
import com.example.ui.MarketSortOption
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.MarketSparkline
import com.example.ui.components.PriceChangeBadge
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MarketsScreen(
    instruments: List<Instrument>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: MarketCategory?,
    onSelectCategory: (MarketCategory?) -> Unit,
    sortOption: MarketSortOption,
    onSelectSortOption: (MarketSortOption) -> Unit,
    watchlistIds: Set<String>,
    onToggleWatchlist: (String) -> Unit,
    onInstrumentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD))
    ) {
        // Top Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Markets Explorer",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Explore & learn asset movements",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // Sort Selector Button
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleSoftLavender)
                        .clickable { sortMenuExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Sort",
                            tint = PurplePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortOption.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    MarketSortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label, fontSize = 13.sp) },
                            onClick = {
                                onSelectSortOption(option)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Search Input Field
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("markets_search_input"),
                placeholder = {
                    Text(
                        "Search by symbol (e.g. RELIANCE, AAPL, BTC)...",
                        color = TextTertiary,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isSelected = selectedCategory == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PurplePrimary else Color.White)
                        .border(1.dp, if (isSelected) PurplePrimary else BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onSelectCategory(null) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "All Assets",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }

            items(MarketCategory.values()) { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PurplePrimary else Color.White)
                        .border(1.dp, if (isSelected) PurplePrimary else BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = category.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Instruments Count & Feed status banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${instruments.size} Instruments",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            DemoDataBadge("OFFLINE SIMULATED FEED")
        }

        // Instruments List
        if (instruments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PurplePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Instruments Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your search query or switching categories.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(instruments, key = { it.id }) { item ->
                    val isWatchlisted = watchlistIds.contains(item.id)

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shapeRadius = 16.dp,
                        backgroundColor = Color.White,
                        onClick = { onInstrumentClick(item.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Symbol & Name
                            Column(modifier = Modifier.weight(1.3f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.symbol,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFF1ECFF))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = item.exchange,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = PurpleDeep
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }

                            // Sparkline
                            MarketSparkline(
                                data = item.sparkline,
                                isPositive = item.change >= 0,
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(28.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Price & Change
                            Column(
                                modifier = Modifier.weight(1.1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "${item.currency}%,.2f".format(item.currentPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                PriceChangeBadge(change = item.change, changePercent = item.changePercent)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Watchlist Button
                            IconButton(
                                onClick = { onToggleWatchlist(item.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isWatchlisted) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = if (isWatchlisted) "Remove from watchlist" else "Add to watchlist",
                                    tint = if (isWatchlisted) PurplePrimary else TextTertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

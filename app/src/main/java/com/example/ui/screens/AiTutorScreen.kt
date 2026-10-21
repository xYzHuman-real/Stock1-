package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.InstrumentTutorContext
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.PriceChangeBadge
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
fun AiTutorScreen(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    context: InstrumentTutorContext? = null,
    isGeminiConfigured: Boolean = false,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val starterQuestions = remember(context) {
        if (context != null) {
            val sym = context.instrument.symbol
            val range = context.selectedTimeRange.label
            listOf(
                "What is $sym?",
                "What does this $range chart show?",
                "What does ${context.instrument.changePercent}% change mean?",
                "What is volatility for this asset?",
                "What risks should a beginner understand?",
                "Simulated movement vs verified real market?"
            )
        } else {
            listOf(
                "Explain P/E Ratio like I'm 15",
                "How does NIFTY 50 work?",
                "What is portfolio diversification?",
                "Difference between ETF and Mutual Fund",
                "Market Order vs Limit Order",
                "Simulated movement vs real market?"
            )
        }
    }

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD))
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurpleSoftLavender),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Stock1 AI Financial Tutor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isGeminiConfigured) "Gemini Multimodal Financial Mentor" else "Interactive Markets & Finance Mentor",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGeminiConfigured) Color(0xFFE8F5E9) else PurpleSoftLavender)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isGeminiConfigured) "GEMINI AI" else "SIMULATOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isGeminiConfigured) Color(0xFF2E7D32) else PurpleDeep
                    )
                }
            }
        }

        // Active Instrument Context Banner if opened from detail screen
        if (context != null) {
            val inst = context.instrument
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F0FA))
                    .border(1.dp, PurpleGlassBorder)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = inst.symbol,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = inst.name,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Active context: ${context.selectedTimeRange.label} chart & market feed attached",
                            fontSize = 10.sp,
                            color = PurpleDeep,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${inst.currency}%,.2f".format(inst.currentPrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PriceChangeBadge(change = inst.change, changePercent = inst.changePercent)
                            Spacer(modifier = Modifier.width(4.dp))
                            DemoDataBadge()
                        }
                    }
                }
            }
        }

        // Disclaimer Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF6F4FA))
                .border(0.5.dp, BorderSubtle)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PurpleDeep,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Strictly for educational guidance. Stock1 does not provide financial or trading advice.",
                    fontSize = 11.sp,
                    color = PurpleDeep,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Chat Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == ChatSender.USER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PurpleSoftLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (isUser) 18.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 18.dp
                                )
                            )
                            .background(if (isUser) PurplePrimary else Color.White)
                            .border(
                                1.dp,
                                if (isUser) PurplePrimary else BorderSubtle,
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (isUser) 18.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 18.dp
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = msg.text,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = if (isUser) Color.White else TextPrimary
                            )
                        }
                    }

                    if (isUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE9E5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PurpleDeep,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PurpleSoftLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = PurplePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyzing financial concept...",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Starter Question Suggestion Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(starterQuestions) { q ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, PurpleGlassBorder, RoundedCornerShape(12.dp))
                        .clickable { onSendMessage(q) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = q,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PurpleDeep
                    )
                }
            }
        }

        // Input Area
        Surface(
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask a question about markets, ratios, charts...", fontSize = 13.sp, color = TextTertiary) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_tutor_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = Color(0xFFFAF9FD),
                        unfocusedContainerColor = Color(0xFFFAF9FD)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val q = inputText
                            inputText = ""
                            onSendMessage(q)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) PurplePrimary else PurpleSoftLavender)
                        .testTag("ai_tutor_send_button"),
                    enabled = inputText.isNotBlank() && !isThinking
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.White else PurpleDeep,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

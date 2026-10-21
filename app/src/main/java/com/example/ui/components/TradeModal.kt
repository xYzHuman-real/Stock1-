package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Instrument
import com.example.data.model.OrderType
import com.example.data.model.TradeAction
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketRed
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeBottomSheet(
    instrument: Instrument,
    initialAction: TradeAction,
    availableCash: Double,
    ownedQuantity: Int,
    usdToInrRate: Double,
    onDismiss: () -> Unit,
    onExecuteTrade: (instrumentId: String, action: TradeAction, orderType: OrderType, quantity: Int, customPrice: Double?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedAction by remember { mutableStateOf(initialAction) }
    var selectedOrderType by remember { mutableStateOf(OrderType.MARKET) }
    var quantityText by remember { mutableStateOf("1") }

    val currentPriceInRupees = if (instrument.currency == "$") {
        instrument.currentPrice * usdToInrRate
    } else {
        instrument.currentPrice
    }

    var customLimitPrice by remember {
        mutableDoubleStateOf(currentPriceInRupees)
    }

    val quantity = quantityText.toIntOrNull() ?: 0
    val executionUnitRupees = if (selectedOrderType == OrderType.MARKET) currentPriceInRupees else customLimitPrice
    val totalOrderValueRupees = executionUnitRupees * quantity

    val canAfford = selectedAction == TradeAction.SELL || totalOrderValueRupees <= availableCash
    val hasEnoughShares = selectedAction == TradeAction.BUY || ownedQuantity >= quantity
    val isValidQuantity = quantity > 0

    val errorMessage = when {
        !isValidQuantity -> "Enter a valid quantity (1 or more)"
        selectedAction == TradeAction.BUY && !canAfford ->
            "Insufficient virtual cash (Need ₹%,.2f, Have ₹%,.2f)".format(totalOrderValueRupees, availableCash)
        selectedAction == TradeAction.SELL && !hasEnoughShares ->
            "Cannot sell more units than you own (You own $ownedQuantity)"
        else -> null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 28.dp)
                .navigationBarsPadding()
        ) {
            // Header with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Simulated Order",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${instrument.name} (${instrument.symbol})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close trade sheet",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notice
            SimulatedNoticeBanner()

            Spacer(modifier = Modifier.height(16.dp))

            // Buy / Sell Tabs
            TabRow(
                selectedTabIndex = if (selectedAction == TradeAction.BUY) 0 else 1,
                containerColor = PurpleSoftLavender,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[if (selectedAction == TradeAction.BUY) 0 else 1])
                            .clip(RoundedCornerShape(10.dp)),
                        color = if (selectedAction == TradeAction.BUY) MarketGreen else MarketRed,
                        height = 4.dp
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedAction == TradeAction.BUY,
                    onClick = { selectedAction = TradeAction.BUY },
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = {
                        Text(
                            text = "BUY",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedAction == TradeAction.BUY) MarketGreen else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedAction == TradeAction.SELL,
                    onClick = { selectedAction = TradeAction.SELL },
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = {
                        Text(
                            text = "SELL",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedAction == TradeAction.SELL) MarketRed else TextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Market Price & Context summary
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F7FD)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Simulated Price",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${instrument.currency}%,.2f".format(instrument.currentPrice),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (instrument.currency == "$") {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(≈ ₹%,.2f)".format(currentPriceInRupees),
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (selectedAction == TradeAction.BUY) "Available Cash" else "You Own",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = if (selectedAction == TradeAction.BUY)
                                "₹%,.2f".format(availableCash)
                            else
                                "$ownedQuantity units",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PurpleDeep
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Type Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrderType.values().forEach { type ->
                    val isSelected = selectedOrderType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PurplePrimary else Color.White)
                            .border(1.dp, if (isSelected) PurplePrimary else BorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { selectedOrderType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity Stepper & Input
            Text(
                text = "Order Quantity",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = {
                        val current = quantityText.toIntOrNull() ?: 1
                        if (current > 1) quantityText = (current - 1).toString()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleSoftLavender)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease quantity", tint = PurpleDeep)
                }

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { newVal ->
                        if (newVal.all { it.isDigit() } && newVal.length <= 6) {
                            quantityText = newVal
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("trade_quantity_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                IconButton(
                    onClick = {
                        val current = quantityText.toIntOrNull() ?: 0
                        quantityText = (current + 1).toString()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PurpleSoftLavender)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase quantity", tint = PurpleDeep)
                }
            }

            // Quick add pill chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 10, 25, 50).forEach { inc ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F0FA))
                            .clickable {
                                val current = quantityText.toIntOrNull() ?: 0
                                quantityText = (current + inc).toString()
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "+$inc",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                }

                if (selectedAction == TradeAction.SELL && ownedQuantity > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEECEB))
                            .clickable { quantityText = ownedQuantity.toString() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "MAX ($ownedQuantity)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MarketRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Order Value Summary Box
            Card(
                colors = CardDefaults.cardColors(containerColor = PurpleSoftLavender.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Value", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "₹%,.2f".format(totalOrderValueRupees),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Brokerage Fee", fontSize = 12.sp, color = TextSecondary)
                        Text("₹0.00 (Simulated)", fontSize = 12.sp, color = MarketGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage,
                    color = MarketRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Execute Button
            Button(
                onClick = {
                    onExecuteTrade(
                        instrument.id,
                        selectedAction,
                        selectedOrderType,
                        quantity,
                        if (selectedOrderType == OrderType.LIMIT) customLimitPrice else null
                    )
                },
                enabled = errorMessage == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_trade_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAction == TradeAction.BUY) MarketGreen else MarketRed,
                    disabledContainerColor = Color(0xFFE2E0EB)
                )
            ) {
                Text(
                    text = if (selectedAction == TradeAction.BUY)
                        "Confirm Simulated Buy (₹%,.2f)".format(totalOrderValueRupees)
                    else
                        "Confirm Simulated Sell (₹%,.2f)".format(totalOrderValueRupees),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

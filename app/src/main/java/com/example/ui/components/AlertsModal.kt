package com.example.ui.components

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertCondition
import com.example.data.model.Instrument
import com.example.data.model.PriceAlert
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketGreenBg
import com.example.ui.theme.MarketRed
import com.example.ui.theme.MarketRedBg
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePriceAlertDialog(
    instrument: Instrument,
    onDismiss: () -> Unit,
    onSaveAlert: (instrumentId: String, targetPrice: Double, condition: AlertCondition) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var targetPriceText by remember {
        mutableStateOf(String.format("%.2f", instrument.currentPrice))
    }
    var selectedCondition by remember {
        mutableStateOf(AlertCondition.ABOVE)
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Android 13+ Notification permission requester
    var hasPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleSoftLavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alert",
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Set Price Alert",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${instrument.symbol} • ${instrument.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Price Display card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shapeRadius = 14.dp,
                backgroundColor = Color(0xFFFAF9FD)
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
                        Text(
                            text = "${instrument.currency}%,.2f".format(instrument.currentPrice),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                    DemoDataBadge("SIMULATED TICKER")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Condition Selection
            Text(
                text = "Alert Trigger Condition",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ConditionCard(
                    title = "Price rises above",
                    subtitle = "≥ Target Price",
                    icon = Icons.Default.ArrowUpward,
                    accentColor = MarketGreen,
                    isSelected = selectedCondition == AlertCondition.ABOVE,
                    onClick = { selectedCondition = AlertCondition.ABOVE },
                    modifier = Modifier.weight(1f)
                )

                ConditionCard(
                    title = "Price falls below",
                    subtitle = "≤ Target Price",
                    icon = Icons.Default.ArrowDownward,
                    accentColor = MarketRed,
                    isSelected = selectedCondition == AlertCondition.BELOW,
                    onClick = { selectedCondition = AlertCondition.BELOW },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Price Input
            Text(
                text = "Target Price (${instrument.currency})",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = targetPriceText,
                onValueChange = {
                    targetPriceText = it
                    validationError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alert_target_price_input"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = validationError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                leadingIcon = {
                    Text(
                        text = instrument.currency,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary,
                        fontSize = 16.sp
                    )
                }
            )

            if (validationError != null) {
                Text(
                    text = validationError ?: "",
                    color = MarketRed,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Target Presets (+2%, +5%, -2%, -5%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(-5.0, -2.0, 2.0, 5.0).forEach { pct ->
                    val presetPrice = instrument.currentPrice * (1.0 + (pct / 100.0))
                    val label = if (pct > 0) "+${pct.toInt()}%" else "${pct.toInt()}%"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F0FA))
                            .clickable {
                                targetPriceText = String.format("%.2f", presetPrice)
                                selectedCondition = if (pct > 0) AlertCondition.ABOVE else AlertCondition.BELOW
                                validationError = null
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleDeep
                        )
                    }
                }
            }

            // Notification permission prompt if needed
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Notifications are muted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Enable permission to receive device alerts when target is hit.",
                            fontSize = 10.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Enable", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Submit Button
            Button(
                onClick = {
                    val price = targetPriceText.toDoubleOrNull()
                    if (price == null || price <= 0.0) {
                        validationError = "Please enter a valid target price greater than 0"
                        return@Button
                    }
                    onSaveAlert(instrument.id, price, selectedCondition)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_price_alert_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Alert", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ConditionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PurpleSoftLavender else Color(0xFFF9F8FD))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) PurplePrimary else BorderSubtle,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) PurpleDeep else TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsManagerSheet(
    alerts: List<PriceAlert>,
    onToggleAlert: (String) -> Unit,
    onDeleteAlert: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableStateOf(0) } // 0 = Active, 1 = Triggered History

    val activeAlerts = alerts.filter { !it.isTriggered }
    val triggeredAlerts = alerts.filter { it.isTriggered }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleSoftLavender),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Price Target Alerts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Simulated notifications for market levels",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF6F4FA),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PurplePrimary
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Active (${activeAlerts.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) PurpleDeep else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "History (${triggeredAlerts.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) PurpleDeep else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val currentDisplayList = if (selectedTab == 0) activeAlerts else triggeredAlerts

            if (currentDisplayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.NotificationsNone else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = PurpleSoftLavender,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0) "No Active Price Alerts" else "No Triggered Alerts Yet",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedTab == 0)
                                "Tap the bell icon on any instrument to set a price target."
                            else
                                "When prices cross your targets, they will appear here.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentDisplayList, key = { it.id }) { alert ->
                        AlertItemCard(
                            alert = alert,
                            onToggle = { onToggleAlert(alert.id) },
                            onDelete = { onDeleteAlert(alert.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun AlertItemCard(
    alert: PriceAlert,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isAbove = alert.condition == AlertCondition.ABOVE
    val conditionColor = if (isAbove) MarketGreen else MarketRed

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shapeRadius = 14.dp,
        backgroundColor = if (alert.isTriggered) Color(0xFFF9F7FD) else Color.White
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
                        text = alert.symbol,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(conditionColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = alert.condition.shortLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = conditionColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Target: ${alert.currency}%,.2f".format(alert.targetPrice),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleDeep
                )

                Spacer(modifier = Modifier.height(3.dp))
                if (alert.isTriggered) {
                    Text(
                        text = "Triggered at ${alert.currency}%,.2f on %s".format(
                            alert.triggeredPrice ?: alert.targetPrice,
                            alert.triggeredAtFormatted ?: ""
                        ),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MarketGreen
                    )
                } else {
                    Text(
                        text = "Created ${alert.createdAtFormatted}",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!alert.isTriggered) {
                    Switch(
                        checked = alert.isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PurplePrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE2E0EB)
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Alert",
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

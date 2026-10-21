package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppDestination
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

data class NavItem(
    val destination: AppDestination,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun GlassBottomNav(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            destination = AppDestination.HOME,
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        NavItem(
            destination = AppDestination.MARKETS,
            label = "Markets",
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            testTag = "nav_markets"
        ),
        NavItem(
            destination = AppDestination.PORTFOLIO,
            label = "Portfolio",
            selectedIcon = Icons.Filled.AccountBalanceWallet,
            unselectedIcon = Icons.Outlined.AccountBalanceWallet,
            testTag = "nav_portfolio"
        ),
        NavItem(
            destination = AppDestination.LEARN,
            label = "Learn",
            selectedIcon = Icons.Filled.School,
            unselectedIcon = Icons.Outlined.School,
            testTag = "nav_learn"
        ),
        NavItem(
            destination = AppDestination.PROFILE,
            label = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "nav_profile"
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = PurpleDeep.copy(alpha = 0.08f),
                    spotColor = PurplePrimary.copy(alpha = 0.15f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White.copy(alpha = 0.95f))
                .border(1.dp, BorderSubtle, RoundedCornerShape(26.dp))
                .padding(vertical = 8.dp, horizontal = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentDestination == item.destination
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) PurplePrimary else TextSecondary,
                        label = "nav_color"
                    )

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigate(item.destination) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag(item.testTag),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PurpleSoftLavender else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PurpleDeep else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

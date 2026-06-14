package com.ecodala.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.navigation.EcoDalaRoute
import com.ecodala.core.navigation.bottomNavDestinations
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun EcoDalaBottomBar(
    currentRoute: String?,
    onDestinationClick: (String) -> Unit
) {
    val strings = LocalEcoStrings.current
    val compactHeight = LocalConfiguration.current.screenHeightDp < 820
    val compactWidth = LocalConfiguration.current.screenWidthDp < 380
    val barHeight = if (compactHeight) 60.dp else 70.dp
    val verticalPadding = if (compactHeight) 2.dp else 6.dp
    val submitSize = if (compactHeight) 36.dp else 40.dp
    val normalItemSize = if (compactHeight) 24.dp else 28.dp
    val submitIconSize = if (compactHeight) 21.dp else 23.dp
    val itemIconSize = if (compactHeight) 17.dp else 19.dp
    val labelFontSize = if (compactWidth) 9.sp else 10.sp

    Surface(
        modifier = Modifier.navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .padding(horizontal = 10.dp, vertical = verticalPadding),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavDestinations.forEach { destination ->
                val label = when (destination.route) {
                    EcoDalaRoute.Home.route -> strings.home
                    EcoDalaRoute.Map.route -> strings.map
                    EcoDalaRoute.SubmitWaste.route -> strings.submit
                    EcoDalaRoute.Leaderboard.route -> strings.leads
                    EcoDalaRoute.Profile.route -> strings.profile
                    else -> destination.label
                }
                val isSelected = currentRoute == destination.route
                val isSubmit = destination.label == "Submit"
                val contentColor = if (isSelected || isSubmit) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDestinationClick(destination.route) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = if (isSubmit) {
                            Modifier
                                .size(submitSize)
                                .shadow(8.dp, CircleShape)
                                .background(EcoGreen, CircleShape)
                        } else {
                            Modifier.size(normalItemSize)
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = label,
                            tint = if (isSubmit) Color.White else contentColor,
                            modifier = Modifier.size(if (isSubmit) submitIconSize else itemIconSize)
                        )
                    }

                    Text(
                        text = label,
                        color = contentColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = labelFontSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

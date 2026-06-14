package com.ecodala.feature.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.settings.AppSettingsStore
import com.ecodala.core.ui.adaptive.horizontalScreenPadding
import com.ecodala.core.ui.adaptive.isCompactHeight
import com.ecodala.core.ui.adaptive.isExtraCompactHeight
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen
import com.ecodala.core.ui.theme.EcoGreenLight

@Composable
fun HomeRoute(
    onMapClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onChallengesClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onTreeClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by AppSettingsStore.settings.collectAsState()

    HomeScreen(
        uiState = uiState,
        selectedLanguageTag = settings.appLanguageTag,
        onMapClick = onMapClick,
        onSubmitClick = onSubmitClick,
        onChallengesClick = onChallengesClick,
        onLeaderboardClick = onLeaderboardClick,
        onNotificationsClick = onNotificationsClick,
        onRatingClick = onLeaderboardClick,
        onTreeClick = onTreeClick,
        onAchievementsClick = onAchievementsClick,
        onLanguageSelected = { languageTag ->
            AppSettingsStore.update { it.copy(appLanguageTag = languageTag) }
        },
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    selectedLanguageTag: String = "en",
    onMapClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onChallengesClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onRatingClick: () -> Unit,
    onTreeClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onLanguageSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current
    val compactHeight = isCompactHeight()
    val extraCompactHeight = isExtraCompactHeight()
    val horizontalPadding = horizontalScreenPadding(regular = 20.dp, compact = 16.dp)
    val pageTopPadding = if (compactHeight) 14.dp else 20.dp
    val smallGap = if (compactHeight) 12.dp else 20.dp
    val sectionGap = if (compactHeight) 16.dp else 24.dp

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(top = pageTopPadding, bottom = 18.dp)
        ) {
            HomeHeader(
                userName = uiState.userName,
                selectedLanguageTag = selectedLanguageTag,
                onLanguageSelected = onLanguageSelected,
                onNotificationsClick = onNotificationsClick
            )

            Spacer(modifier = Modifier.height(smallGap))

            EcoRatingCard(
                points = uiState.ecoPoints,
                rank = uiState.globalRank,
                level = uiState.level,
                compactHeight = compactHeight,
                onClick = onRatingClick
            )

            Spacer(modifier = Modifier.height(smallGap))

            MonthlyImpactCard(
                points = uiState.thisMonthPoints,
                recycledKg = uiState.thisMonthKg,
                submissions = uiState.thisMonthSubmissions,
                streakDays = uiState.streakDays,
                nextRewardPoints = uiState.nextStreakRewardPoints
            )

            Spacer(modifier = Modifier.height(smallGap))

            VirtualTreeCard(
                progressPercent = uiState.treeProgressPercent,
                level = uiState.level,
                compactHeight = compactHeight,
                onClick = onTreeClick
            )

            Spacer(modifier = Modifier.height(sectionGap))

            Text(
                text = strings.quickActions,
                color = Color(0xFF5E665F),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(if (compactHeight) 10.dp else 14.dp))

            QuickActionsRow(
                compactHeight = extraCompactHeight,
                onMapClick = onMapClick,
                onSubmitClick = onSubmitClick,
                onChallengesClick = onChallengesClick,
                onLeaderboardClick = onLeaderboardClick
            )

            Spacer(modifier = Modifier.height(sectionGap))

            RecentAchievementsCard(
                achievements = uiState.achievements,
                onClick = onAchievementsClick
            )
        }
    }
}

@Composable
private fun MonthlyImpactCard(
    points: Int,
    recycledKg: Double,
    submissions: Int,
    streakDays: Int,
    nextRewardPoints: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "This Month Impact",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$streakDays day streak - next +$nextRewardPoints pts",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(EcoGreen.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Whatshot,
                        contentDescription = null,
                        tint = EcoGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ImpactMetric(
                    icon = Icons.Filled.Recycling,
                    value = "${recycledKg.toInt()} kg",
                    label = "Recycled",
                    modifier = Modifier.weight(1f)
                )
                ImpactMetric(
                    icon = Icons.Filled.Spa,
                    value = "+$points",
                    label = "Points",
                    modifier = Modifier.weight(1f)
                )
                ImpactMetric(
                    icon = Icons.Filled.TaskAlt,
                    value = submissions.toString(),
                    label = "Submits",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ImpactMetric(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    selectedLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    onNotificationsClick: () -> Unit
) {
    val strings = LocalEcoStrings.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = strings.hello(userName),
            modifier = Modifier.weight(1f),
            color = EcoGreen,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeLanguageSwitcher(
                selectedLanguageTag = selectedLanguageTag,
                onLanguageSelected = onLanguageSelected
            )

            Box {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = EcoGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                        .background(Color(0xFFE53935), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun HomeLanguageSwitcher(
    selectedLanguageTag: String,
    onLanguageSelected: (String) -> Unit
) {
    val options = listOf("en" to "EN", "ru" to "RU", "kk" to "KZ")

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (tag, label) ->
            Text(
                text = label,
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(if (selectedLanguageTag == tag) EcoGreen else Color.Transparent)
                    .clickable { onLanguageSelected(tag) }
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                color = if (selectedLanguageTag == tag) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EcoRatingCard(
    points: Int,
    rank: Int,
    level: Int,
    compactHeight: Boolean,
    onClick: () -> Unit
) {
    val strings = LocalEcoStrings.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactHeight) 118.dp else 142.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF3AA248), Color(0xFF247F32))
                    )
                )
                .padding(if (compactHeight) 16.dp else 20.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = strings.currentEcoRating,
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.points(points),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = strings.level(level),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = strings.globalRank(rank),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun VirtualTreeCard(
    progressPercent: Int,
    level: Int,
    compactHeight: Boolean,
    onClick: () -> Unit
) {
    val strings = LocalEcoStrings.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactHeight) 104.dp else 122.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (compactHeight) 12.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TreeThumbnail(level = level)

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.virtualTreeTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = strings.progressToLevel(progressPercent, level + 1),
                    color = Color(0xFF656E67),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EcoGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TreeThumbnail(level: Int) {
    val strings = LocalEcoStrings.current

    Box(
        modifier = Modifier
            .size(width = 92.dp, height = 72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(EcoGreenLight),
        contentAlignment = Alignment.BottomEnd
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTreeIllustration()
        }

        Text(
            text = strings.level(level),
            modifier = Modifier
                .padding(5.dp)
                .background(EcoGreen, RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp),
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickActionsRow(
    compactHeight: Boolean,
    onMapClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onChallengesClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val strings = LocalEcoStrings.current
        QuickActionItem(strings.map, Icons.Filled.Map, compactHeight, onMapClick)
        QuickActionItem(strings.submit, Icons.Filled.AddCircleOutline, compactHeight, onSubmitClick)
        QuickActionItem(strings.challenges, Icons.Filled.EmojiEvents, compactHeight, onChallengesClick)
        QuickActionItem(strings.leads, Icons.Filled.Leaderboard, compactHeight, onLeaderboardClick)
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    compactHeight: Boolean,
    onClick: () -> Unit
) {
    val iconBoxSize = if (compactHeight) 46.dp else 54.dp
    val iconSize = if (compactHeight) 21.dp else 24.dp

    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(iconBoxSize)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = EcoGreen,
                modifier = Modifier.size(iconSize)
            )
        }
        Spacer(modifier = Modifier.height(if (compactHeight) 5.dp else 8.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RecentAchievementsCard(
    achievements: List<HomeAchievementUi>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalEcoStrings.current.recentAchievements,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Open achievements",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            achievements.take(3).forEach { achievement ->
                AchievementRow(achievement = achievement)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AchievementRow(achievement: HomeAchievementUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (background, iconTint, icon) = when (achievement.iconName) {
            "star" -> Triple(Color(0xFFEFF4F8), Color(0xFF6E8398), Icons.Filled.Spa)
            "group" -> Triple(Color(0xFFF7EDE2), Color(0xFFA66024), Icons.Filled.Recycling)
            else -> Triple(Color(0xFFFFF5D9), Color(0xFFD59A12), Icons.Filled.EmojiEvents)
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(background, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(14.dp))

        Column {
            Text(
                text = achievement.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = achievement.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun DrawScope.drawTreeIllustration() {
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color(0xFFC8EAA4), Color(0xFFEAF5D8))
        ),
        size = size
    )
    drawRoundRect(
        color = Color(0xFF6B4424),
        topLeft = Offset(size.width * 0.48f, size.height * 0.46f),
        size = Size(size.width * 0.08f, size.height * 0.32f),
        cornerRadius = CornerRadius(5.dp.toPx())
    )
    drawCircle(Color(0xFF9FD33E), size.width * 0.13f, Offset(size.width * 0.42f, size.height * 0.38f))
    drawCircle(Color(0xFF83BF32), size.width * 0.15f, Offset(size.width * 0.55f, size.height * 0.32f))
    drawCircle(Color(0xFFB8E24A), size.width * 0.11f, Offset(size.width * 0.62f, size.height * 0.47f))
    drawOval(
        color = Color(0xFF6D4C2E),
        topLeft = Offset(size.width * 0.20f, size.height * 0.78f),
        size = Size(size.width * 0.64f, size.height * 0.10f)
    )
    drawOval(
        color = Color(0xFF243522),
        topLeft = Offset(size.width * 0.24f, size.height * 0.80f),
        size = Size(size.width * 0.58f, size.height * 0.05f)
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomeScreenPreview() {
    EcoDalaTheme {
        HomeScreen(
            uiState = HomeUiState(),
            onMapClick = {},
            onSubmitClick = {},
            onChallengesClick = {},
            onLeaderboardClick = {},
            onNotificationsClick = {},
            onRatingClick = {},
            onTreeClick = {},
            onAchievementsClick = {}
        )
    }
}

package com.ecodala.feature.achievements.presentation

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Yard
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen
import kotlin.math.roundToInt

@Composable
fun AchievementsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = viewModel()
) {
    val achievements by viewModel.achievements.collectAsState()

    AchievementsScreen(
        achievements = achievements,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun AchievementsScreen(
    achievements: List<Achievement>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unlocked = achievements.filter { it.isUnlocked }
    val locked = achievements.filterNot { it.isUnlocked }
    val progress = if (achievements.isEmpty()) 0f else unlocked.size / achievements.size.toFloat()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            AchievementsTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                AchievementSummaryCard(
                    unlockedCount = unlocked.size,
                    totalCount = achievements.size,
                    progress = progress
                )

                Spacer(modifier = Modifier.height(22.dp))

                SectionTitle("Unlocked", "${unlocked.size} earned")
                Spacer(modifier = Modifier.height(12.dp))
                unlocked.forEach { achievement ->
                    AchievementCard(achievement = achievement)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                SectionTitle("In Progress", "${locked.size} remaining")
                Spacer(modifier = Modifier.height(12.dp))
                locked.forEachIndexed { index, achievement ->
                    AchievementCard(
                        achievement = achievement,
                        progressPercent = listOf(72, 45, 30).getOrElse(index) { 25 }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AchievementsTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = EcoGreen
            )
        }

        Text(
            text = LocalEcoStrings.current.achievements,
            color = EcoGreen,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .size(22.dp)
        )
    }
}

@Composable
private fun AchievementSummaryCard(
    unlockedCount: Int,
    totalCount: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = LocalEcoStrings.current.ecoProgress,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = LocalEcoStrings.current.achievementsUnlocked(unlockedCount, totalCount),
                        color = Color.White.copy(alpha = 0.76f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${(progress * 100).roundToInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(7.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.30f)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String, trailing: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = trailing,
            color = Color(0xFF758077),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    progressPercent: Int? = null
) {
    val visual = achievement.visual()
    val isLocked = !achievement.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.78f else 1f)
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(visual.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Filled.Lock else visual.icon,
                    contentDescription = null,
                    tint = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else visual.tint,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = achievement.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (achievement.isUnlocked) {
                        Spacer(modifier = Modifier.size(6.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = EcoGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = achievement.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(7.dp))
                if (achievement.isUnlocked) {
                    Text(
                        text = achievement.unlockedAt.orEmpty(),
                        color = EcoGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { (progressPercent ?: 0) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = EcoGreen,
                        trackColor = Color(0xFFE0E6DF)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = LocalEcoStrings.current.completePercent(progressPercent ?: 0),
                        color = Color(0xFF758077),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

private data class AchievementVisual(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

private fun Achievement.visual(): AchievementVisual {
    return when (iconName) {
        "star" -> AchievementVisual(Icons.Filled.Spa, Color(0xFFEFF4F8), Color(0xFF6E8398))
        "group" -> AchievementVisual(Icons.Filled.Leaderboard, Color(0xFFF7EDE2), Color(0xFFA66024))
        "tree" -> AchievementVisual(Icons.Filled.Yard, Color(0xFFE5F6EA), EcoGreen)
        "map" -> AchievementVisual(Icons.Filled.Map, Color(0xFFE4F0FF), Color(0xFF3D78D8))
        "scanner" -> AchievementVisual(Icons.Filled.CameraAlt, Color(0xFFEBD9FF), Color(0xFF8D55E6))
        else -> AchievementVisual(Icons.Filled.Recycling, Color(0xFFFFF5D9), Color(0xFFD59A12))
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AchievementsScreenPreview() {
    EcoDalaTheme {
        AchievementsScreen(
            achievements = DummyEcoData.achievements,
            onBackClick = {}
        )
    }
}

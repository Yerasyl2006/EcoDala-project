package com.ecodala.feature.challenges.presentation

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.Challenge
import com.ecodala.core.domain.model.ChallengeStatus
import com.ecodala.core.domain.model.ChallengeType
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun ChallengesRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengesViewModel = viewModel()
) {
    val challenges by viewModel.challenges.collectAsState()

    ChallengesScreen(
        challenges = challenges,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun ChallengesScreen(
    challenges: List<Challenge>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ChallengeType.Daily) }
    val daily = challenges.filter { it.type == ChallengeType.Daily }
    val weekly = challenges.filter { it.type == ChallengeType.Weekly }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ChallengesTopBar(onBackClick = onBackClick)
            ChallengeTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 24.dp)
            ) {
                when (selectedTab) {
                    ChallengeType.Daily -> {
                        SectionHeader(title = LocalEcoStrings.current.dailyChallenges, trailing = LocalEcoStrings.current.left(daily.count { it.status == ChallengeStatus.Active }))
                        Spacer(modifier = Modifier.height(14.dp))
                        daily.forEach { challenge ->
                            ChallengeCard(challenge = challenge)
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        SectionHeader(title = LocalEcoStrings.current.weeklyChallenges)
                        Spacer(modifier = Modifier.height(14.dp))
                        weekly.firstOrNull()?.let { ChallengeCard(challenge = it) }
                        Spacer(modifier = Modifier.height(28.dp))
                        EcoSpecialBanner()
                    }
                    ChallengeType.Weekly -> {
                        SectionHeader(title = LocalEcoStrings.current.weeklyChallenges)
                        Spacer(modifier = Modifier.height(14.dp))
                        weekly.forEach { challenge ->
                            ChallengeCard(challenge = challenge)
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                        EcoSpecialBanner()
                    }
                    ChallengeType.Special -> {
                        EcoSpecialBanner()
                    }
                }
            }
        }
    }
}

@Composable
private fun ChallengesTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
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
            text = LocalEcoStrings.current.challenges,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ChallengeTabs(
    selectedTab: ChallengeType,
    onTabSelected: (ChallengeType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        listOf(ChallengeType.Daily, ChallengeType.Weekly, ChallengeType.Special).forEach { tab ->
            val isSelected = selectedTab == tab
            Column(
                modifier = Modifier
                    .height(56.dp)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = LocalEcoStrings.current.challengeTypeName(tab),
                    color = if (isSelected) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 3.dp)
                        .background(if (isSelected) EcoGreen else Color.Transparent, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    trailing: String? = null
) {
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
        trailing?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ChallengeCard(challenge: Challenge) {
    val isLocked = challenge.status == ChallengeStatus.Locked
    val isCompleted = challenge.status == ChallengeStatus.Completed
    val progress = if (challenge.target == 0) 0f else challenge.progress / challenge.target.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.42f else 1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChallengeIcon(challenge = challenge)
                    Spacer(modifier = Modifier.size(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = challenge.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = challenge.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    StatusBadge(challenge.status)
                }

                Spacer(modifier = Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = EcoGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = "${if (isCompleted) "✓ " else "+"}${LocalEcoStrings.current.points(challenge.rewardPoints)}",
                    modifier = Modifier.align(Alignment.End),
                    color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else EcoGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ChallengeIcon(challenge: Challenge) {
    val (icon, background, tint) = when {
        challenge.status == ChallengeStatus.Locked -> Triple(
            Icons.Filled.Spa,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        challenge.title.contains("Visit", ignoreCase = true) -> Triple(Icons.Filled.LocationOn, EcoGreen.copy(alpha = 0.16f), EcoGreen)
        else -> Triple(Icons.Filled.Recycling, EcoGreen.copy(alpha = 0.16f), EcoGreen)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(23.dp)
        )
    }
}

@Composable
private fun StatusBadge(status: ChallengeStatus) {
    val text = when (status) {
        ChallengeStatus.Active -> LocalEcoStrings.current.active
        ChallengeStatus.Completed -> LocalEcoStrings.current.completed
        ChallengeStatus.Locked -> "${LocalEcoStrings.current.locked} 🔒"
    }
    val background = when (status) {
        ChallengeStatus.Active -> EcoGreen.copy(alpha = 0.16f)
        ChallengeStatus.Completed -> MaterialTheme.colorScheme.surfaceVariant
        ChallengeStatus.Locked -> MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == ChallengeStatus.Completed) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.size(3.dp))
        }
        Text(
            text = text,
            color = if (status == ChallengeStatus.Active) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EcoSpecialBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(EcoGreen)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEcoSpecialBackground()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(22.dp)
        ) {
            Text(
                text = LocalEcoStrings.current.ecoSpecialTitle,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = LocalEcoStrings.current.ecoSpecialSubtitle,
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun DrawScope.drawEcoSpecialBackground() {
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(Color(0xFF0E6D26), Color(0xFF24A438))
        )
    )
    repeat(18) { index ->
        val x = index * size.width / 18f
        drawLine(
            color = Color.White.copy(alpha = 0.10f),
            start = Offset(x, 0f),
            end = Offset(x + 26.dp.toPx(), size.height),
            strokeWidth = 1.dp.toPx()
        )
    }
    drawCircle(
        color = Color.White.copy(alpha = 0.09f),
        radius = size.width * 0.28f,
        center = Offset(size.width * 0.76f, size.height * 0.25f)
    )

    val skyline = Path().apply {
        moveTo(size.width * 0.12f, size.height)
        lineTo(size.width * 0.12f, size.height * 0.66f)
        lineTo(size.width * 0.18f, size.height * 0.66f)
        lineTo(size.width * 0.18f, size.height * 0.56f)
        lineTo(size.width * 0.26f, size.height * 0.56f)
        lineTo(size.width * 0.26f, size.height * 0.70f)
        lineTo(size.width * 0.36f, size.height * 0.70f)
        lineTo(size.width * 0.36f, size.height * 0.48f)
        lineTo(size.width * 0.44f, size.height * 0.48f)
        lineTo(size.width * 0.44f, size.height)
        close()
    }
    drawPath(path = skyline, color = Color(0xFF0A4F20).copy(alpha = 0.24f))
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChallengesScreenPreview() {
    EcoDalaTheme {
        ChallengesScreen(
            challenges = DummyEcoData.challenges,
            onBackClick = {}
        )
    }
}


package com.ecodala.feature.leaderboard.presentation

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.LeaderboardEntry
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.adaptive.horizontalScreenPadding
import com.ecodala.core.ui.adaptive.isCompactHeight
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun LeaderboardRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val faculties by viewModel.faculties.collectAsState()

    LeaderboardScreen(
        entries = entries,
        faculties = faculties,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    faculties: List<FacultyLeaderboardEntry>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGroup by remember { mutableStateOf("Students") }
    val sortedEntries = entries.sortedBy { it.rank }
    val topEntries = sortedEntries.take(3)
    val listEntries = sortedEntries.drop(3)
    val compactHeight = isCompactHeight()
    val horizontalPadding = horizontalScreenPadding(regular = 20.dp, compact = 16.dp)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            LeaderboardTopBar(
                compactHeight = compactHeight,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding)
                    .padding(top = if (compactHeight) 8.dp else 14.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedControl(
                    compactHeight = compactHeight,
                    selected = selectedGroup,
                    onSelectedChange = { selectedGroup = it }
                )

                Spacer(modifier = Modifier.height(if (compactHeight) 10.dp else 18.dp))

                if (selectedGroup == "Students") {
                    PodiumSection(entries = topEntries, compactHeight = compactHeight)

                    Spacer(modifier = Modifier.height(if (compactHeight) 14.dp else 26.dp))

                    LeaderboardList(entries = listEntries, compactHeight = compactHeight)
                } else {
                    FacultyLeaderboardContent(faculties = faculties)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTopBar(
    compactHeight: Boolean,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactHeight) 52.dp else 64.dp)
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
            text = LocalEcoStrings.current.leaderboard,
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
private fun SegmentedControl(
    compactHeight: Boolean,
    selected: String,
    onSelectedChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactHeight) 38.dp else 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val strings = LocalEcoStrings.current
        listOf("Students" to strings.students, "Faculties" to strings.faculties).forEach { (key, label) ->
            val isSelected = key == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) EcoGreen else Color.Transparent)
                    .clickable { onSelectedChange(key) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PodiumSection(
    entries: List<LeaderboardEntry>,
    compactHeight: Boolean
) {
    val first = entries.getOrNull(0)
    val second = entries.getOrNull(1)
    val third = entries.getOrNull(2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compactHeight) 206.dp else 246.dp)
    ) {
        val sideAvatar = if (compactHeight) 58.dp else 66.dp
        val centerAvatar = if (compactHeight) 72.dp else 82.dp

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(if (compactHeight) 92.dp else 110.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            PodiumBlock(modifier = Modifier.weight(1f), height = if (compactHeight) 70.dp else 84.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            PodiumBlock(modifier = Modifier.weight(1f), height = if (compactHeight) 104.dp else 122.dp, color = Color(0xFFFFD84D))
            PodiumBlock(modifier = Modifier.weight(1f), height = if (compactHeight) 70.dp else 84.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        }

        second?.let {
            PodiumUser(
                entry = it,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 22.dp, top = if (compactHeight) 38.dp else 48.dp),
                avatarSize = sideAvatar,
                accentColor = Color(0xFFBFC8BE)
            )
        }
        first?.let {
            PodiumUser(
                entry = it,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp),
                avatarSize = centerAvatar,
                accentColor = Color(0xFFFFD600),
                showCrown = true
            )
        }
        third?.let {
            PodiumUser(
                entry = it,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 14.dp, top = if (compactHeight) 54.dp else 66.dp),
                avatarSize = sideAvatar,
                accentColor = Color(0xFFFF8A2A)
            )
        }
    }
}

@Composable
private fun PodiumBlock(
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(color.copy(alpha = 0.82f))
    )
}

@Composable
private fun PodiumUser(
    entry: LeaderboardEntry,
    avatarSize: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    modifier: Modifier = Modifier,
    showCrown: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showCrown) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFD600),
                modifier = Modifier.size(24.dp)
            )
        }

        Box(contentAlignment = Alignment.BottomEnd) {
            AvatarCircle(
                name = entry.name,
                size = avatarSize,
                borderColor = accentColor
            )
            RankBadge(rank = entry.rank, color = accentColor)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = entry.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = LocalEcoStrings.current.points(entry.points),
            color = EcoGreen,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RankBadge(rank: Int, color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LeaderboardList(
    entries: List<LeaderboardEntry>,
    compactHeight: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            entries.forEachIndexed { index, entry ->
                LeaderboardRow(entry = entry, compactHeight = compactHeight)
                if (index != entries.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 18.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    )
                }
            }
        }
    }
}

@Composable
private fun FacultyLeaderboardContent(faculties: List<FacultyLeaderboardEntry>) {
    val sorted = faculties.sortedBy { it.rank }
    val leader = sorted.firstOrNull()
    val totalPoints = sorted.sumOf { it.points }
    val totalMembers = sorted.sumOf { it.members }

    if (leader != null) {
        FacultyHeroCard(
            leader = leader,
            totalPoints = totalPoints,
            totalMembers = totalMembers
        )
        Spacer(modifier = Modifier.height(18.dp))
    }

    FacultyLeaderboardList(faculties = sorted)
}

@Composable
private fun FacultyHeroCard(
    leader: FacultyLeaderboardEntry,
    totalPoints: Int,
    totalMembers: Int
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
                        text = LocalEcoStrings.current.topFaculty,
                        color = Color.White.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = leader.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${LocalEcoStrings.current.points(leader.points)} • ${LocalEcoStrings.current.thisWeek(leader.weeklyGrowthPercent)}",
                        color = Color.White.copy(alpha = 0.80f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FacultyStatChip(
                    label = LocalEcoStrings.current.total,
                    value = LocalEcoStrings.current.points(totalPoints),
                    icon = Icons.Filled.EmojiEvents,
                    modifier = Modifier.weight(1f)
                )
                FacultyStatChip(
                    label = LocalEcoStrings.current.members,
                    value = totalMembers.toString(),
                    icon = Icons.Filled.Groups,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FacultyStatChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.66f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FacultyLeaderboardList(faculties: List<FacultyLeaderboardEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            faculties.forEachIndexed { index, faculty ->
                FacultyLeaderboardRow(faculty = faculty)
                if (index != faculties.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 18.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                    )
                }
            }
        }
    }
}

@Composable
private fun FacultyLeaderboardRow(faculty: FacultyLeaderboardEntry) {
    val maxPoints = 8500f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (faculty.isCurrentFaculty) EcoGreen.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = faculty.rank.toString(),
            modifier = Modifier.size(width = 26.dp, height = 28.dp),
            color = if (faculty.isCurrentFaculty) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        FacultyBadge(shortName = faculty.shortName, rank = faculty.rank)

        Spacer(modifier = Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faculty.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = faculty.points.toString(),
                    color = EcoGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (faculty.points / maxPoints).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = EcoGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalEcoStrings.current.topContributor(faculty.members, faculty.topContributor),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = EcoGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " ${faculty.weeklyGrowthPercent}%",
                        color = EcoGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FacultyBadge(
    shortName: String,
    rank: Int
) {
    val background = when (rank) {
        1 -> Color(0xFFFFD84D).copy(alpha = 0.24f)
        2 -> EcoGreen.copy(alpha = 0.16f)
        3 -> Color(0xFFFF8A2A).copy(alpha = 0.18f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val tint = when (rank) {
        1 -> Color(0xFFC99713)
        2 -> EcoGreen
        3 -> Color(0xFFE87522)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = shortName,
            color = tint,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
    compactHeight: Boolean
) {
    val strings = LocalEcoStrings.current
    val displayName = if (entry.isCurrentUser) strings.currentUserLeaderboardName else entry.name

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (entry.isCurrentUser) EcoGreen.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface)
            .padding(horizontal = 18.dp, vertical = if (compactHeight) 10.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.rank.toString(),
            modifier = Modifier.size(width = 34.dp, height = 28.dp),
            color = if (entry.isCurrentUser) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        AvatarCircle(
            name = displayName,
            size = if (compactHeight) 36.dp else 42.dp,
            borderColor = if (entry.isCurrentUser) EcoGreen else Color.Transparent
        )

        Spacer(modifier = Modifier.size(14.dp))

        Text(
            text = displayName,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = strings.points(entry.points),
            color = EcoGreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AvatarCircle(
    name: String,
    size: androidx.compose.ui.unit.Dp,
    borderColor: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFDDE8D7)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAvatar(seed = name)
        }
        if (borderColor != Color.Transparent) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = borderColor,
                    radius = this.size.minDimension / 2 - 2.dp.toPx(),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

private fun DrawScope.drawAvatar(seed: String) {
    val hash = seed.fold(0) { acc, char -> acc + char.code }
    val skin = listOf(Color(0xFFD49A68), Color(0xFF8E573B), Color(0xFFC8875C))[hash % 3]
    val shirt = listOf(Color(0xFF1E6B3A), Color(0xFF6E8B52), Color(0xFF263B2D))[hash % 3]
    drawCircle(
        brush = Brush.verticalGradient(listOf(Color(0xFF325D3B), Color(0xFF0E2B1A))),
        radius = size.minDimension / 2
    )
    drawCircle(
        color = skin,
        radius = size.minDimension * 0.18f,
        center = Offset(size.width * 0.50f, size.height * 0.36f)
    )
    drawOval(
        color = Color(0xFF1C1A16),
        topLeft = Offset(size.width * 0.34f, size.height * 0.14f),
        size = Size(size.width * 0.32f, size.height * 0.22f)
    )
    drawRoundRect(
        color = shirt,
        topLeft = Offset(size.width * 0.28f, size.height * 0.58f),
        size = Size(size.width * 0.44f, size.height * 0.32f),
        cornerRadius = CornerRadius(size.width * 0.14f)
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LeaderboardScreenPreview() {
    EcoDalaTheme {
        LeaderboardScreen(
            entries = DummyEcoData.leaderboard,
            faculties = LeaderboardViewModel().faculties.value,
            onBackClick = {}
        )
    }
}


package com.ecodala.feature.tree.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.BuildConfig
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.TreeGrowthEvent
import com.ecodala.core.domain.model.VirtualTree
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen
import java.time.LocalDateTime
import java.time.Month

@Composable
fun VirtualTreeRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VirtualTreeViewModel = viewModel()
) {
    val tree by viewModel.tree.collectAsState()

    VirtualTreeScreen(
        tree = tree,
        onBackClick = onBackClick,
        onPreviewLevelSelected = viewModel::previewLevel,
        modifier = modifier
    )
}

@Composable
fun VirtualTreeScreen(
    tree: VirtualTree,
    onBackClick: () -> Unit,
    onPreviewLevelSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TreeTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 22.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TreeIllustrationCard(tree = tree)
                Spacer(modifier = Modifier.height(18.dp))
                LevelBadge(level = tree.level)
                if (BuildConfig.DEBUG && onPreviewLevelSelected != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    LevelPreviewStrip(
                        selectedLevel = tree.level,
                        onLevelSelected = onPreviewLevelSelected
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
                ProgressCard(tree = tree)
                Spacer(modifier = Modifier.height(18.dp))
                NextGoalCard(tree = tree)
                Spacer(modifier = Modifier.height(18.dp))
                GrowthHistoryCard(events = tree.growthHistory)
            }
        }
    }
}

@Composable
private fun TreeTopBar(onBackClick: () -> Unit) {
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = EcoGreen
            )
        }

        Text(
            text = LocalEcoStrings.current.myEcoTree,
            color = EcoGreen,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TreeIllustrationCard(tree: VirtualTree) {
    val scene = remember { TreeScene.from(LocalDateTime.now()) }
    var started by remember(tree.level, tree.progressPercent) { mutableStateOf(false) }
    LaunchedEffect(tree.level, tree.progressPercent) {
        started = true
    }
    val animatedGrowth by animateFloatAsState(
        targetValue = if (started) tree.growthRatio() else 0f,
        animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
        label = "tree-growth"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "tree-sway")
    val sway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tree-sway"
    )

    Box(
        modifier = Modifier
            .size(232.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLevelTree(growth = animatedGrowth, level = tree.level, sway = sway, scene = scene)
        }
    }
}

@Composable
private fun LevelPreviewStrip(
    selectedLevel: Int,
    onLevelSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (0..10).forEach { level ->
            val selected = level == selectedLevel
            Text(
                text = level.toString(),
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) EcoGreen else MaterialTheme.colorScheme.surface)
                    .clickable { onLevelSelected(level) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LevelBadge(level: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(EcoGreen)
            .padding(horizontal = 20.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = LocalEcoStrings.current.level(level),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(7.dp))
        Icon(
            imageVector = Icons.Filled.Spa,
            contentDescription = null,
            tint = Color(0xFFB9E68A),
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun ProgressCard(tree: VirtualTree) {
    val maxLevel = tree.level >= 10
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (maxLevel) "Tree is fully grown" else LocalEcoStrings.current.progressToNextLevel(tree.level + 1),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { tree.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = EcoGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = LocalEcoStrings.current.xpStatus(tree.currentXp, tree.nextLevelXp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    text = if (maxLevel) "Max level" else LocalEcoStrings.current.xpToGo(tree.nextLevelXp - tree.currentXp),
                    color = EcoGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NextGoalCard(tree: VirtualTree) {
    val maxLevel = tree.level >= 10
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF237A31)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.16f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp)
                    .size(78.dp)
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.TrackChanges,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = LocalEcoStrings.current.nextGoal,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (maxLevel) "Your tree reached level 10. Keep earning points to protect your forest."
                    else LocalEcoStrings.current.collectXpGoal(tree.nextLevelXp - tree.currentXp, tree.level + 1),
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { tree.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.34f)
                )
            }
        }
    }
}

@Composable
private fun GrowthHistoryCard(events: List<TreeGrowthEvent>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = LocalEcoStrings.current.growthHistory,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            events.forEachIndexed { index, event ->
                GrowthHistoryRow(
                    event = event,
                    isLast = index == events.lastIndex
                )
            }
        }
    }
}

@Composable
private fun GrowthHistoryRow(
    event: TreeGrowthEvent,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.height(if (isLast) 46.dp else 64.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(3.dp)
                    .background(if (isLast) Color(0xFFBBC5BA) else EcoGreen, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 48.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                )
            }
        }

        Spacer(modifier = Modifier.size(18.dp))

        Column {
            Text(
                text = event.date,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${event.title} ${eventIcon(event.title)}",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun eventIcon(title: String): String {
    return if (title.contains("Level", ignoreCase = true)) "*" else "+"
}

private fun VirtualTree.growthRatio(): Float {
    val levelProgress = if (level >= 10) 1f else progressPercent.coerceIn(0, 100) / 100f
    return ((level.coerceIn(0, 10) + levelProgress) / 10f).coerceIn(0f, 1f)
}

private fun DrawScope.drawLevelTree(growth: Float, level: Int, sway: Float, scene: TreeScene) {
    drawOutdoorBackground(scene)

    val stage = growth.coerceIn(0f, 1f)
    val centerX = size.width * 0.50f
    val groundY = size.height * 0.80f
    val swayOffset = sway * lerp(0f, 4.dp.toPx(), stage)

    drawOval(
        color = Color.Black.copy(alpha = if (scene.period == DayPeriod.Night) 0.28f else 0.16f),
        topLeft = Offset(size.width * (0.30f - stage * 0.10f), groundY + 1.dp.toPx()),
        size = Size(size.width * (0.40f + stage * 0.32f), size.height * 0.07f)
    )

    if (stage < 0.07f) {
        drawTwig(stage = stage / 0.07f, centerX = centerX, groundY = groundY)
        return
    }

    if (stage < 0.20f) {
        drawSeedling(stage = (stage - 0.07f) / 0.13f, centerX = centerX, groundY = groundY)
        return
    }

    val trunkHeight = lerp(size.height * 0.25f, size.height * 0.50f, stage)
    val trunkTopY = groundY - trunkHeight
    val trunkHalfWidth = lerp(7.dp.toPx(), 27.dp.toPx(), stage)

    val trunk = Path().apply {
        moveTo(centerX - trunkHalfWidth, groundY)
        cubicTo(centerX - trunkHalfWidth * 1.18f, groundY - trunkHeight * 0.30f, centerX - trunkHalfWidth * 0.52f + swayOffset * 0.25f, groundY - trunkHeight * 0.70f, centerX - trunkHalfWidth * 0.18f + swayOffset, trunkTopY)
        cubicTo(centerX + trunkHalfWidth * 0.45f + swayOffset, groundY - trunkHeight * 0.70f, centerX + trunkHalfWidth * 1.12f, groundY - trunkHeight * 0.30f, centerX + trunkHalfWidth, groundY)
        close()
    }
    drawPath(trunk, Color(0xFF5A3822))
    drawPath(trunk, Color(0xFF2C1B12).copy(alpha = 0.30f), style = Stroke(width = 2.dp.toPx()))
    drawBarkTexture(centerX = centerX, groundY = groundY, trunkHeight = trunkHeight, trunkHalfWidth = trunkHalfWidth, swayOffset = swayOffset)

    if (stage > 0.28f) {
        drawBranch(Offset(centerX + swayOffset * 0.25f, groundY - trunkHeight * 0.58f), Offset(centerX - size.width * lerp(0.13f, 0.24f, stage) + swayOffset, groundY - trunkHeight * lerp(0.68f, 0.92f, stage)), stage)
        drawBranch(Offset(centerX + 4.dp.toPx() + swayOffset * 0.35f, groundY - trunkHeight * 0.62f), Offset(centerX + size.width * lerp(0.14f, 0.25f, stage) + swayOffset, groundY - trunkHeight * lerp(0.72f, 0.94f, stage)), stage)
    }
    if (stage > 0.50f) {
        drawBranch(Offset(centerX - 2.dp.toPx() + swayOffset * 0.20f, groundY - trunkHeight * 0.42f), Offset(centerX - size.width * 0.28f + swayOffset, groundY - trunkHeight * 0.54f), stage)
        drawBranch(Offset(centerX + 2.dp.toPx() + swayOffset * 0.25f, groundY - trunkHeight * 0.42f), Offset(centerX + size.width * 0.30f + swayOffset, groundY - trunkHeight * 0.54f), stage)
    }

    val crownScale = lerp(0.55f, 1.10f, stage)
    val crownCenterY = groundY - trunkHeight * lerp(0.83f, 0.98f, stage)
    val palette = scene.leafPalette()
    val foliage = mutableListOf(
        TreeLeaf(Offset(centerX + swayOffset, crownCenterY), size.width * 0.22f * crownScale, palette[1], 0.20f),
        TreeLeaf(Offset(centerX - size.width * 0.13f * stage + swayOffset, crownCenterY + size.height * 0.06f), size.width * 0.18f * crownScale, palette[0], 0.28f),
        TreeLeaf(Offset(centerX + size.width * 0.14f * stage + swayOffset, crownCenterY + size.height * 0.06f), size.width * 0.18f * crownScale, palette[2], 0.34f)
    )

    if (stage > 0.48f) {
        foliage += TreeLeaf(Offset(centerX - size.width * 0.24f + swayOffset, crownCenterY + size.height * 0.13f), size.width * 0.15f * crownScale, palette[1], 0.46f)
        foliage += TreeLeaf(Offset(centerX + size.width * 0.25f + swayOffset, crownCenterY + size.height * 0.13f), size.width * 0.15f * crownScale, palette[3], 0.50f)
    }
    if (stage > 0.70f) {
        foliage += TreeLeaf(Offset(centerX - size.width * 0.14f + swayOffset, crownCenterY - size.height * 0.10f), size.width * 0.16f * crownScale, palette[0], 0.68f)
        foliage += TreeLeaf(Offset(centerX + size.width * 0.15f + swayOffset, crownCenterY - size.height * 0.11f), size.width * 0.16f * crownScale, palette[2], 0.70f)
        foliage += TreeLeaf(Offset(centerX + swayOffset, crownCenterY - size.height * 0.16f), size.width * 0.15f * crownScale, palette[1], 0.76f)
    }

    foliage.forEach { leaf ->
        val alpha = ((stage - leaf.unlockAt) / 0.18f).coerceIn(0f, 1f)
        drawCircle(color = leaf.color.copy(alpha = alpha), radius = leaf.radius * alpha, center = leaf.center)
        drawCircle(
            color = Color.Black.copy(alpha = 0.06f * alpha),
            radius = leaf.radius * 0.72f * alpha,
            center = leaf.center.copy(x = leaf.radius * 0.10f + leaf.center.x, y = leaf.center.y + leaf.radius * 0.16f)
        )
        drawCircle(
            color = Color.White.copy(alpha = if (scene.period == DayPeriod.Night) 0.02f * alpha else 0.07f * alpha),
            radius = leaf.radius * 0.42f * alpha,
            center = leaf.center.copy(x = leaf.center.x - leaf.radius * 0.24f, y = leaf.center.y - leaf.radius * 0.22f)
        )
    }

    val sparkCount = (level.coerceIn(0, 10) * 3).coerceAtMost(30)
    repeat(sparkCount) { index ->
        val x = size.width * (0.27f + (index % 7) * 0.075f)
        val y = crownCenterY - size.height * 0.13f + size.height * ((index / 7) * 0.075f)
        drawCircle(
            color = scene.sparkColor().copy(alpha = 0.10f + stage * 0.18f),
            radius = lerp(1.2.dp.toPx(), 2.4.dp.toPx(), stage),
            center = Offset(x, y)
        )
    }
}

private data class TreeLeaf(
    val center: Offset,
    val radius: Float,
    val color: Color,
    val unlockAt: Float
)

private enum class DayPeriod {
    Morning,
    Noon,
    Evening,
    Night
}

private enum class TreeSeason {
    Spring,
    Summer,
    Autumn,
    Winter
}

private data class TreeScene(
    val period: DayPeriod,
    val season: TreeSeason
) {
    fun leafPalette(): List<Color> {
        val base = when (season) {
            TreeSeason.Spring -> listOf(Color(0xFF8EBF53), Color(0xFF5F9E45), Color(0xFF3F7F3B), Color(0xFFA6C95A))
            TreeSeason.Summer -> listOf(Color(0xFF496F33), Color(0xFF315F2E), Color(0xFF244D28), Color(0xFF5E7F38))
            TreeSeason.Autumn -> listOf(Color(0xFFB9782C), Color(0xFF8F4F22), Color(0xFFD29A3A), Color(0xFF6E3D1D))
            TreeSeason.Winter -> listOf(Color(0xFF8A978A), Color(0xFF6F7D73), Color(0xFF56665F), Color(0xFFB8C2B9))
        }
        return if (period == DayPeriod.Night) base.map { it.copy(alpha = 0.82f) } else base
    }

    fun sparkColor(): Color {
        return when (period) {
            DayPeriod.Night -> Color(0xFFD8E6FF)
            DayPeriod.Evening -> Color(0xFFFFD18A)
            else -> Color(0xFFFFF0A8)
        }
    }

    companion object {
        fun from(now: LocalDateTime): TreeScene {
            val period = when (now.hour) {
                in 5..10 -> DayPeriod.Morning
                in 11..16 -> DayPeriod.Noon
                in 17..20 -> DayPeriod.Evening
                else -> DayPeriod.Night
            }
            val season = when (now.month) {
                Month.MARCH, Month.APRIL, Month.MAY -> TreeSeason.Spring
                Month.JUNE, Month.JULY, Month.AUGUST -> TreeSeason.Summer
                Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> TreeSeason.Autumn
                else -> TreeSeason.Winter
            }
            return TreeScene(period = period, season = season)
        }
    }
}

private fun DrawScope.drawOutdoorBackground(scene: TreeScene) {
    val sky = when (scene.period) {
        DayPeriod.Morning -> listOf(Color(0xFFFFF1D8), Color(0xFFD9EFCF), Color(0xFFB7D7C4))
        DayPeriod.Noon -> listOf(Color(0xFFE8F6DA), Color(0xFFCCE7BC), Color(0xFFB9D5A9))
        DayPeriod.Evening -> listOf(Color(0xFFF5C98B), Color(0xFFD7B577), Color(0xFF8FA36F))
        DayPeriod.Night -> listOf(Color(0xFF172536), Color(0xFF203527), Color(0xFF26341F))
    }
    drawRect(
        brush = Brush.verticalGradient(colors = sky),
        size = size
    )

    val orbCenter = when (scene.period) {
        DayPeriod.Morning -> Offset(size.width * 0.22f, size.height * 0.24f)
        DayPeriod.Noon -> Offset(size.width * 0.78f, size.height * 0.18f)
        DayPeriod.Evening -> Offset(size.width * 0.18f, size.height * 0.30f)
        DayPeriod.Night -> Offset(size.width * 0.78f, size.height * 0.22f)
    }
    val orbColor = if (scene.period == DayPeriod.Night) Color(0xFFE8EEF7) else Color(0xFFFFE5A3)
    drawCircle(
        color = orbColor.copy(alpha = if (scene.period == DayPeriod.Night) 0.62f else 0.48f),
        radius = if (scene.period == DayPeriod.Night) 12.dp.toPx() else 18.dp.toPx(),
        center = orbCenter
    )

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xFF3E5F32).copy(alpha = if (scene.period == DayPeriod.Night) 0.58f else 0.34f)),
            startY = size.height * 0.58f,
            endY = size.height
        )
    )

    if (scene.period == DayPeriod.Night) {
        repeat(14) { index ->
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                radius = 1.2.dp.toPx(),
                center = Offset(
                    x = size.width * (0.12f + (index % 7) * 0.12f),
                    y = size.height * (0.12f + (index / 7) * 0.12f)
                )
            )
        }
    }
}

private fun DrawScope.drawBarkTexture(
    centerX: Float,
    groundY: Float,
    trunkHeight: Float,
    trunkHalfWidth: Float,
    swayOffset: Float
) {
    val left = centerX - trunkHalfWidth * 0.48f
    val right = centerX + trunkHalfWidth * 0.48f
    repeat(5) { index ->
        val x = lerp(left, right, index / 4f)
        drawLine(
            color = Color(0xFF2A190F).copy(alpha = 0.22f),
            start = Offset(x, groundY - trunkHeight * 0.06f),
            end = Offset(x + swayOffset * 0.35f + if (index % 2 == 0) 3.dp.toPx() else -3.dp.toPx(), groundY - trunkHeight * 0.88f),
            strokeWidth = 1.2.dp.toPx()
        )
    }
}

private fun DrawScope.drawTwig(stage: Float, centerX: Float, groundY: Float) {
    val twig = stage.coerceIn(0f, 1f)
    val base = Offset(centerX - 18.dp.toPx(), groundY - 2.dp.toPx())
    val tip = Offset(centerX + lerp(5.dp.toPx(), 24.dp.toPx(), twig), groundY - lerp(15.dp.toPx(), 30.dp.toPx(), twig))

    drawLine(
        color = Color(0xFF6B3D22),
        start = base,
        end = tip,
        strokeWidth = lerp(3.dp.toPx(), 5.dp.toPx(), twig)
    )
    drawLine(
        color = Color(0xFF8A5A35).copy(alpha = 0.65f),
        start = Offset(centerX - 2.dp.toPx(), groundY - 13.dp.toPx()),
        end = Offset(centerX + 12.dp.toPx(), groundY - 28.dp.toPx()),
        strokeWidth = 2.dp.toPx()
    )
    drawCircle(
        color = Color(0xFF8BC34A).copy(alpha = 0.35f + twig * 0.55f),
        radius = lerp(3.dp.toPx(), 7.dp.toPx(), twig),
        center = tip
    )
}

private fun DrawScope.drawSeedling(stage: Float, centerX: Float, groundY: Float) {
    val sprout = stage.coerceIn(0f, 1f)
    drawLine(
        color = Color(0xFF2F7D39),
        start = Offset(centerX, groundY),
        end = Offset(centerX, groundY - lerp(18.dp.toPx(), 54.dp.toPx(), sprout)),
        strokeWidth = lerp(4.dp.toPx(), 7.dp.toPx(), sprout)
    )
    val leafRadius = lerp(10.dp.toPx(), 22.dp.toPx(), sprout)
    drawOval(
        color = Color(0xFF7FBF4D),
        topLeft = Offset(centerX - leafRadius * 1.6f, groundY - 42.dp.toPx() * sprout),
        size = Size(leafRadius * 1.9f, leafRadius)
    )
    drawOval(
        color = Color(0xFF2F8D46),
        topLeft = Offset(centerX + leafRadius * 0.10f, groundY - 48.dp.toPx() * sprout),
        size = Size(leafRadius * 1.9f, leafRadius)
    )
}

private fun DrawScope.drawBranch(start: Offset, end: Offset, growth: Float) {
    drawLine(
        color = Color(0xFF5B321D),
        start = start,
        end = end,
        strokeWidth = lerp(3.dp.toPx(), 8.dp.toPx(), growth)
    )
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction.coerceIn(0f, 1f)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun VirtualTreeScreenPreview() {
    EcoDalaTheme {
        VirtualTreeScreen(
            tree = DummyEcoData.tree,
            onBackClick = {}
        )
    }
}

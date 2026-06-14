package com.ecodala.feature.tree.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.TreeGrowthEvent
import com.ecodala.core.domain.model.VirtualTree
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

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
        modifier = modifier
    )
}

@Composable
fun VirtualTreeScreen(
    tree: VirtualTree,
    onBackClick: () -> Unit,
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
                TreeIllustrationCard()

                Spacer(modifier = Modifier.height(18.dp))

                LevelBadge(level = tree.level)

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
                imageVector = Icons.Filled.ArrowBack,
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
private fun TreeIllustrationCard() {
    Box(
        modifier = Modifier
            .size(232.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBigTree()
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = LocalEcoStrings.current.progressToNextLevel(tree.level + 1),
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
                    color = Color(0xFF818A83),
                    fontSize = 12.sp
                )
                Text(
                    text = LocalEcoStrings.current.xpToGo(tree.nextLevelXp - tree.currentXp),
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
                    text = LocalEcoStrings.current.collectXpGoal(tree.nextLevelXp - tree.currentXp, tree.level + 1),
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
        }
    }
}

private fun eventIcon(title: String): String {
    return if (title.equals("Tree", ignoreCase = true)) "🌳" else "🌱"
}

private fun DrawScope.drawBigTree() {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFEAF8D2), Color(0xFFD6E9BC)),
            center = Offset(size.width * 0.50f, size.height * 0.45f),
            radius = size.minDimension * 0.70f
        )
    )

    drawOval(
        color = Color(0xFF97B867).copy(alpha = 0.25f),
        topLeft = Offset(size.width * 0.18f, size.height * 0.78f),
        size = Size(size.width * 0.64f, size.height * 0.08f)
    )

    val trunk = Path().apply {
        moveTo(size.width * 0.46f, size.height * 0.76f)
        cubicTo(size.width * 0.44f, size.height * 0.60f, size.width * 0.47f, size.height * 0.48f, size.width * 0.50f, size.height * 0.35f)
        cubicTo(size.width * 0.56f, size.height * 0.50f, size.width * 0.58f, size.height * 0.62f, size.width * 0.56f, size.height * 0.76f)
        close()
    }
    drawPath(trunk, Color(0xFF6B3D22))
    drawPath(trunk, Color(0xFF3B2417).copy(alpha = 0.25f), style = Stroke(width = 2.dp.toPx()))

    drawBranch(Offset(size.width * 0.50f, size.height * 0.48f), Offset(size.width * 0.35f, size.height * 0.38f))
    drawBranch(Offset(size.width * 0.51f, size.height * 0.46f), Offset(size.width * 0.66f, size.height * 0.33f))
    drawBranch(Offset(size.width * 0.50f, size.height * 0.55f), Offset(size.width * 0.32f, size.height * 0.56f))
    drawBranch(Offset(size.width * 0.53f, size.height * 0.54f), Offset(size.width * 0.72f, size.height * 0.52f))

    val foliage = listOf(
        Triple(Offset(size.width * 0.34f, size.height * 0.38f), size.width * 0.17f, Color(0xFFC9D83F)),
        Triple(Offset(size.width * 0.49f, size.height * 0.31f), size.width * 0.19f, Color(0xFFBCCB35)),
        Triple(Offset(size.width * 0.64f, size.height * 0.39f), size.width * 0.17f, Color(0xFF577C44)),
        Triple(Offset(size.width * 0.40f, size.height * 0.51f), size.width * 0.20f, Color(0xFF94B83A)),
        Triple(Offset(size.width * 0.58f, size.height * 0.52f), size.width * 0.21f, Color(0xFF365F3C)),
        Triple(Offset(size.width * 0.50f, size.height * 0.43f), size.width * 0.22f, Color(0xFF8AA831))
    )

    foliage.forEach { (center, radius, color) ->
        drawCircle(color = color, radius = radius, center = center)
        drawCircle(color = Color.White.copy(alpha = 0.08f), radius = radius * 0.52f, center = center.copy(x = center.x - radius * 0.22f, y = center.y - radius * 0.22f))
    }

    repeat(18) { index ->
        val x = size.width * (0.32f + (index % 6) * 0.07f)
        val y = size.height * (0.34f + (index / 6) * 0.10f)
        drawCircle(
            color = Color(0xFFE5EF69).copy(alpha = 0.38f),
            radius = 3.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawBranch(start: Offset, end: Offset) {
    drawLine(
        color = Color(0xFF5B321D),
        start = start,
        end = end,
        strokeWidth = 7.dp.toPx()
    )
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

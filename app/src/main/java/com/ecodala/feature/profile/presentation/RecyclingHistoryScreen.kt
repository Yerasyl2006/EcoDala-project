package com.ecodala.feature.profile.presentation

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.components.EcoErrorState
import com.ecodala.core.ui.components.EcoInlineLoading
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun RecyclingHistoryRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecyclingHistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecyclingHistoryScreen(
        uiState = uiState,
        onTypeSelected = viewModel::onTypeSelected,
        onRetryClick = viewModel::refresh,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun RecyclingHistoryScreen(
    uiState: RecyclingHistoryUiState,
    onTypeSelected: (WasteType?) -> Unit,
    onRetryClick: () -> Unit = {},
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
            RecyclingHistoryTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                HistorySummaryCard(
                    totalKg = uiState.totalQuantityKg,
                    totalPoints = uiState.totalPoints,
                    totalSubmissions = uiState.submissions.size
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (uiState.isLoading) {
                    EcoInlineLoading(label = "Refreshing history...")
                    Spacer(modifier = Modifier.height(12.dp))
                }

                uiState.errorMessage?.let { message ->
                    EcoErrorState(
                        message = "Showing saved history where available. $message",
                        onRetryClick = onRetryClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                FilterRow(
                    selectedType = uiState.selectedType,
                    onTypeSelected = onTypeSelected
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = LocalEcoStrings.current.recentSubmissions,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                uiState.filteredSubmissions.forEach { submission ->
                    HistoryCard(submission = submission)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RecyclingHistoryTopBar(onBackClick: () -> Unit) {
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
            text = LocalEcoStrings.current.recyclingHistory,
            color = EcoGreen,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Filled.History,
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
private fun HistorySummaryCard(
    totalKg: Int,
    totalPoints: Int,
    totalSubmissions: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = LocalEcoStrings.current.yourImpact,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = LocalEcoStrings.current.impactSubtitle,
                color = Color.White.copy(alpha = 0.76f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryTile("$totalKg kg", LocalEcoStrings.current.wasteLabel, Modifier.weight(1f))
                SummaryTile(totalPoints.toString(), LocalEcoStrings.current.pointsLabel, Modifier.weight(1f))
                SummaryTile(totalSubmissions.toString(), LocalEcoStrings.current.submit, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun FilterRow(
    selectedType: WasteType?,
    onTypeSelected: (WasteType?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            label = LocalEcoStrings.current.all,
            selected = selectedType == null,
            onClick = { onTypeSelected(null) }
        )
        listOf(WasteType.Plastic, WasteType.Paper, WasteType.Glass, WasteType.Batteries, WasteType.Electronics).forEach { type ->
            FilterChip(
                label = LocalEcoStrings.current.wasteTypeName(type),
                selected = selectedType == type,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) EcoGreen else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        color = if (selected) Color.White else EcoGreen,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun HistoryCard(submission: WasteSubmission) {
    val strings = LocalEcoStrings.current
    val visual = submission.wasteType.visual()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(visual.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.tint,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.wasteTypeName(submission.wasteType),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+${strings.points(submission.earnedPoints)}",
                        color = EcoGreen,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${formatQuantity(submission.quantity)} ${submission.unit} • ${localizeSubmissionDate(submission.createdAt, strings)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                submission.comment?.let {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = localizeSubmissionComment(it, strings),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.size(10.dp))

            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = EcoGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun localizeSubmissionDate(value: String, strings: com.ecodala.core.localization.EcoStrings): String {
    val parts = value.split(",", limit = 2)
    return if (parts.size == 2) {
        "${strings.localizedDate(parts[0].trim())},${parts[1]}"
    } else {
        strings.localizedDate(value)
    }
}

private fun localizeSubmissionComment(value: String, strings: com.ecodala.core.localization.EcoStrings): String {
    return when (strings.languageTag) {
        "ru" -> when (value) {
            "Bottles from campus cleanup" -> "Бутылки после уборки кампуса"
            "Old notebooks and printed sheets" -> "Старые тетради и распечатки"
            "Used AA batteries" -> "Использованные батарейки AA"
            "Old chargers" -> "Старые зарядные устройства"
            else -> value
        }
        "kk" -> when (value) {
            "Bottles from campus cleanup" -> "Кампус тазалауынан қалған бөтелкелер"
            "Old notebooks and printed sheets" -> "Ескі дәптерлер мен баспа қағаздар"
            "Used AA batteries" -> "Қолданылған AA батареялар"
            "Old chargers" -> "Ескі қуаттағыштар"
            else -> value
        }
        else -> value
    }
}

private fun formatQuantity(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}

private data class HistoryVisual(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

private fun WasteType.visual(): HistoryVisual {
    return when (this) {
        WasteType.Plastic -> HistoryVisual(Icons.Filled.Recycling, Color(0xFFDCEBFF), Color(0xFF4385F5))
        WasteType.Paper -> HistoryVisual(Icons.Filled.InsertDriveFile, Color(0xFFFFE9D6), Color(0xFFFF8A3D))
        WasteType.Glass -> HistoryVisual(Icons.Filled.WineBar, Color(0xFFD7FAF4), Color(0xFF18A999))
        WasteType.Batteries -> HistoryVisual(Icons.Filled.BatteryChargingFull, Color(0xFFFFF6C8), Color(0xFFC99713))
        WasteType.Electronics -> HistoryVisual(Icons.Filled.Memory, Color(0xFFEBD9FF), Color(0xFF9B5DF5))
        WasteType.Organic -> HistoryVisual(Icons.Filled.Spa, Color(0xFFE3F8E2), EcoGreen)
        WasteType.Metal -> HistoryVisual(Icons.Filled.Recycling, Color(0xFFE8ECEF), Color(0xFF64727D))
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RecyclingHistoryScreenPreview() {
    EcoDalaTheme {
        RecyclingHistoryScreen(
            uiState = RecyclingHistoryUiState(DummyEcoData.wasteSubmissions),
            onTypeSelected = {},
            onBackClick = {}
        )
    }
}

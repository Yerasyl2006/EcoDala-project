package com.ecodala.feature.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.WaterStation
import com.ecodala.core.domain.model.WaterStationIssueType
import com.ecodala.core.domain.model.WaterStationStatus
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.localization.buildRouteWithMaps
import com.ecodala.core.localization.distance
import com.ecodala.core.localization.gps
import com.ecodala.core.localization.rating
import com.ecodala.core.localization.reviews
import com.ecodala.core.localization.savedWithPoints
import com.ecodala.core.localization.submitReport
import com.ecodala.core.localization.type
import com.ecodala.core.localization.uploadUpdatedPhoto
import com.ecodala.core.localization.verifyStationInformation
import com.ecodala.core.localization.waterQualityAndReports
import com.ecodala.core.localization.waterStationIssueName
import com.ecodala.core.localization.waterStationStatusName
import com.ecodala.core.localization.waterStationTypeName
import com.ecodala.core.localization.waterStations
import com.ecodala.core.localization.workingHours
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun WaterStationDetailsRoute(
    stationId: String?,
    onBackClick: () -> Unit,
    onBuildRouteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var station by remember {
        mutableStateOf(DummyEcoData.waterStations.firstOrNull { it.id == stationId } ?: DummyEcoData.waterStations.first())
    }
    LaunchedEffect(stationId) {
        stationId?.let { id ->
            ApiEcoRepository().waterStation(id).onSuccess { station = it }
        }
    }
    WaterStationDetailsScreen(station, onBackClick, onBuildRouteClick, modifier)
}

@Composable
fun WaterStationDetailsScreen(
    station: WaterStation,
    onBackClick: () -> Unit,
    onBuildRouteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIssue by remember { mutableStateOf<WaterStationIssueType?>(null) }
    var userRating by remember { mutableStateOf(5) }
    var reportSent by remember { mutableStateOf(false) }
    val strings = LocalEcoStrings.current

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(onBackClick)
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Hero(station)
                InfoCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(station.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(station.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${strings.gps}: ${station.latitude}, ${station.longitude}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                        StatusBadge(station.status)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Metric(Icons.Filled.LocationOn, "${station.distanceMeters} m", strings.distance, Modifier.weight(1f))
                        Metric(Icons.Filled.Star, String.format("%.1f", station.rating), strings.rating, Modifier.weight(1f))
                        Metric(Icons.Filled.WaterDrop, strings.waterStationTypeName(station.waterType), strings.type, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("${strings.workingHours}: ${station.workingHours}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = onBuildRouteClick,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Icon(Icons.Filled.Route, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(strings.buildRouteWithMaps, fontWeight = FontWeight.Bold)
                }
                ContributionCard(selectedIssue, { selectedIssue = it }, userRating, { userRating = it }, reportSent) {
                    reportSent = true
                }
                ReviewsCard(station)
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1976D2))
        }
        Text(LocalEcoStrings.current.waterStations, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Hero(station: WaterStation) {
    Box(
        modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFF1976D2)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            Text(station.photoLabel, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContributionCard(
    selectedIssue: WaterStationIssueType?,
    onIssueSelected: (WaterStationIssueType) -> Unit,
    userRating: Int,
    onRatingSelected: (Int) -> Unit,
    reportSent: Boolean,
    onSubmit: () -> Unit
) {
    InfoCard {
        val strings = LocalEcoStrings.current
        Text(strings.waterQualityAndReports, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { rating ->
                Icon(Icons.Filled.Star, contentDescription = null, tint = if (rating <= userRating) Color(0xFFF5A623) else MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(26.dp).clickable { onRatingSelected(rating) })
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            WaterStationIssueType.entries.forEach { issue ->
                IssueChip(issue, selectedIssue == issue) { onIssueSelected(issue) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(modifier = Modifier.size(10.dp))
            Text(strings.uploadUpdatedPhoto, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("+20 pts", color = EcoGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Verified, contentDescription = null, tint = EcoGreen)
            Spacer(modifier = Modifier.size(10.dp))
            Text(strings.verifyStationInformation, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("+15 pts", color = EcoGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onSubmit, enabled = selectedIssue != null, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)), shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Filled.Report, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(if (reportSent) strings.savedWithPoints else strings.submitReport, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReviewsCard(station: WaterStation) {
    InfoCard {
        Text(LocalEcoStrings.current.reviews, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        station.reviews.forEach { review ->
            Text("${review.userName} - ${review.rating}/5", fontWeight = FontWeight.Bold)
            Text(review.comment, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(review.createdAt, color = EcoGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun Metric(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun StatusBadge(status: WaterStationStatus) {
    val color = when (status) {
        WaterStationStatus.Available -> Color(0xFF1976D2)
        WaterStationStatus.Unknown -> Color(0xFFF5A623)
        WaterStationStatus.TemporarilyUnavailable, WaterStationStatus.Maintenance -> Color(0xFFD32F2F)
    }
    Text(LocalEcoStrings.current.waterStationStatusName(status), modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(color.copy(alpha = 0.14f)).padding(horizontal = 10.dp, vertical = 6.dp), color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
}

@Composable
private fun IssueChip(issue: WaterStationIssueType, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = LocalEcoStrings.current.waterStationIssueName(issue),
        modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(if (selected) Color(0xFF1976D2) else MaterialTheme.colorScheme.surfaceVariant).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
}

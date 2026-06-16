package com.ecodala.feature.map.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.WheelchairPickup
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.BiotoiletIssueType
import com.ecodala.core.domain.model.BiotoiletStatus
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.localization.accessible
import com.ecodala.core.localization.biotoiletIssueName
import com.ecodala.core.localization.biotoiletStatusName
import com.ecodala.core.localization.biotoiletTypeName
import com.ecodala.core.localization.biotoilets
import com.ecodala.core.localization.buildRouteWithMaps
import com.ecodala.core.localization.cleanliness
import com.ecodala.core.localization.communityVerified
import com.ecodala.core.localization.distance
import com.ecodala.core.localization.facilities
import com.ecodala.core.localization.familyFriendly
import com.ecodala.core.localization.hours
import com.ecodala.core.localization.no
import com.ecodala.core.localization.rateAndReport
import com.ecodala.core.localization.reportSavedWithPoints
import com.ecodala.core.localization.reviews
import com.ecodala.core.localization.submitReport
import com.ecodala.core.localization.type
import com.ecodala.core.localization.uploadUpdatedPhoto
import com.ecodala.core.localization.yes
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun BiotoiletDetailsRoute(
    toiletId: String?,
    onBackClick: () -> Unit,
    onBuildRouteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var toilet by remember {
        mutableStateOf(DummyEcoData.biotoilets.firstOrNull { it.id == toiletId } ?: DummyEcoData.biotoilets.first())
    }
    LaunchedEffect(toiletId) {
        toiletId?.let { id ->
            ApiEcoRepository().biotoilet(id).onSuccess { toilet = it }
        }
    }
    BiotoiletDetailsScreen(
        toilet = toilet,
        onBackClick = onBackClick,
        onBuildRouteClick = onBuildRouteClick,
        modifier = modifier
    )
}

@Composable
fun BiotoiletDetailsScreen(
    toilet: Biotoilet,
    onBackClick: () -> Unit,
    onBuildRouteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIssue by remember { mutableStateOf<BiotoiletIssueType?>(null) }
    var userRating by remember { mutableStateOf(4) }
    var reportSent by remember { mutableStateOf(false) }
    val strings = LocalEcoStrings.current

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(onBackClick = onBackClick)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhotoHero(toilet)
                HeaderCard(toilet)
                FacilitiesCard(toilet)
                Button(
                    onClick = onBuildRouteClick,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                ) {
                    Icon(Icons.Filled.Route, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(strings.buildRouteWithMaps, fontWeight = FontWeight.Bold)
                }
                ReportCard(
                    selectedIssue = selectedIssue,
                    onIssueSelected = { selectedIssue = it },
                    userRating = userRating,
                    onRatingSelected = { userRating = it },
                    reportSent = reportSent,
                    onSubmitReport = { reportSent = true }
                )
                ReviewsCard(toilet)
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = EcoGreen)
        }
        Text(LocalEcoStrings.current.biotoilets, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PhotoHero(toilet: Biotoilet) {
    Box(
        modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFCFEEDD), Color(0xFF5FAF7A)))),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha = 0.18f), 120f, Offset(size.width * 0.85f, size.height * 0.18f))
            drawCircle(Color.White.copy(alpha = 0.12f), 90f, Offset(size.width * 0.18f, size.height * 0.82f))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Wc, contentDescription = null, tint = Color.White, modifier = Modifier.size(54.dp))
            Text(toilet.photoLabel, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HeaderCard(toilet: Biotoilet) {
    val strings = LocalEcoStrings.current
    InfoCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(toilet.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(toilet.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(toilet.status)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Metric(Icons.Filled.LocationOn, "${toilet.distanceMeters} m", strings.distance, Modifier.weight(1f))
            Metric(Icons.Filled.Star, String.format("%.1f", toilet.cleanlinessRating), strings.cleanliness, Modifier.weight(1f))
            Metric(Icons.Filled.Wc, strings.biotoiletTypeName(toilet.type), strings.type, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("${strings.hours}: ${toilet.openingHours}", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun FacilitiesCard(toilet: Biotoilet) {
    val strings = LocalEcoStrings.current
    InfoCard {
        Text(strings.facilities, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        FacilityRow(Icons.Filled.WheelchairPickup, strings.accessible, toilet.isAccessible)
        FacilityRow(Icons.Filled.FamilyRestroom, strings.familyFriendly, toilet.isFamilyFriendly)
        FacilityRow(Icons.Filled.CheckCircle, strings.communityVerified, toilet.cleanlinessRating >= 4.0)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ReportCard(
    selectedIssue: BiotoiletIssueType?,
    onIssueSelected: (BiotoiletIssueType) -> Unit,
    userRating: Int,
    onRatingSelected: (Int) -> Unit,
    reportSent: Boolean,
    onSubmitReport: () -> Unit
) {
    val strings = LocalEcoStrings.current
    InfoCard {
        Text(strings.rateAndReport, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { rating ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (rating <= userRating) Color(0xFFF5A623) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(26.dp).clickable { onRatingSelected(rating) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BiotoiletIssueType.entries.forEach { issue ->
                IssueChip(issue, selectedIssue == issue) { onIssueSelected(issue) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = EcoGreen)
            Spacer(modifier = Modifier.size(10.dp))
            Text(strings.uploadUpdatedPhoto, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("+20 pts", color = EcoGreen, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onSubmitReport,
            enabled = selectedIssue != null,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Report, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(if (reportSent) strings.reportSavedWithPoints else strings.submitReport, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReviewsCard(toilet: Biotoilet) {
    InfoCard {
        Text(LocalEcoStrings.current.reviews, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        toilet.reviews.forEach { review ->
            Text("${review.userName} - ${review.rating}/5", fontWeight = FontWeight.Bold)
            Text(review.comment, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Text(review.createdAt, color = EcoGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun Metric(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(18.dp))
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun FacilityRow(icon: ImageVector, label: String, enabled: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = if (enabled) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.size(10.dp))
        Text(label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        val strings = LocalEcoStrings.current
        Text(if (enabled) strings.yes else strings.no, color = if (enabled) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatusBadge(status: BiotoiletStatus) {
    val color = when (status) {
        BiotoiletStatus.Open -> Color(0xFF188942)
        BiotoiletStatus.Unknown -> Color(0xFFF5A623)
        BiotoiletStatus.Closed, BiotoiletStatus.Maintenance -> Color(0xFFD32F2F)
    }
    Text(
        text = LocalEcoStrings.current.biotoiletStatusName(status),
        modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(color.copy(alpha = 0.14f)).padding(horizontal = 10.dp, vertical = 6.dp),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
}

@Composable
private fun IssueChip(issue: BiotoiletIssueType, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = LocalEcoStrings.current.biotoiletIssueName(issue),
        modifier = Modifier.clip(RoundedCornerShape(18.dp))
            .background(if (selected) EcoGreen else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
}

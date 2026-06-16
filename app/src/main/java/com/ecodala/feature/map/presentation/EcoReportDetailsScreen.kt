package com.ecodala.feature.map.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.data.repository.ApiEcoRepository
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoReportIssueType
import com.ecodala.core.domain.model.EcoReportSeverity
import com.ecodala.core.domain.model.EcoReportStatus
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.localization.comments
import com.ecodala.core.localization.communityActions
import com.ecodala.core.localization.distance
import com.ecodala.core.localization.ecoReportIssueName
import com.ecodala.core.localization.ecoReportSeverityName
import com.ecodala.core.localization.ecoReportStatusName
import com.ecodala.core.localization.ecoReports
import com.ecodala.core.localization.gps
import com.ecodala.core.localization.reportedBy
import com.ecodala.core.localization.sendUpdateToAdministration
import com.ecodala.core.localization.severity
import com.ecodala.core.localization.submitReportUpdate
import com.ecodala.core.localization.updateSavedWithPoints
import com.ecodala.core.localization.uploadUpdatedPhoto
import com.ecodala.core.localization.verifications
import com.ecodala.core.localization.verifiedWithPoints
import com.ecodala.core.localization.verifyCondition
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun EcoReportDetailsRoute(
    reportId: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var report by remember {
        mutableStateOf(DummyEcoData.ecoReports.firstOrNull { it.id == reportId } ?: DummyEcoData.ecoReports.first())
    }
    LaunchedEffect(reportId) {
        reportId?.let { id ->
            ApiEcoRepository().ecoReport(id).onSuccess { report = it }
        }
    }
    EcoReportDetailsScreen(report = report, onBackClick = onBackClick, modifier = modifier)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EcoReportDetailsScreen(
    report: EcoReport,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIssue by remember { mutableStateOf<EcoReportIssueType?>(null) }
    var verified by remember { mutableStateOf(false) }
    var reportSent by remember { mutableStateOf(false) }
    val strings = LocalEcoStrings.current

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(onBackClick)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Hero(report)
                InfoCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(report.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(report.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${strings.gps}: ${report.latitude}, ${report.longitude}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                        StatusBadge(report.status, report.severity)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(report.wasteDescription, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Metric(Icons.Filled.LocationOn, "${report.distanceMeters} m", strings.distance, Modifier.weight(1f))
                        Metric(Icons.Filled.Verified, "${report.verificationCount + if (verified) 1 else 0}", strings.verifications, Modifier.weight(1f))
                        Metric(Icons.Filled.Report, strings.ecoReportSeverityName(report.severity), strings.severity, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(strings.reportedBy(report.reportedBy, report.reportedAt), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                InfoCard {
                    Text(strings.communityActions, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { verified = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(if (verified) strings.verifiedWithPoints else strings.verifyCondition, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = EcoGreen)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(strings.uploadUpdatedPhoto, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text("+20 pts", color = EcoGreen, fontWeight = FontWeight.Bold)
                    }
                }

                InfoCard {
                    Text(strings.submitReportUpdate, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EcoReportIssueType.entries.forEach { issue ->
                            IssueChip(issue, selectedIssue == issue) { selectedIssue = issue }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { reportSent = true },
                        enabled = selectedIssue != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Report, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(if (reportSent) strings.updateSavedWithPoints else strings.sendUpdateToAdministration, fontWeight = FontWeight.Bold)
                    }
                }

                InfoCard {
                    Text(strings.comments, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    report.comments.forEach { comment ->
                        Text(comment.userName, fontWeight = FontWeight.Bold)
                        Text(comment.comment, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(comment.createdAt, color = EcoGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
        IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFD84315))
        }
        Text(LocalEcoStrings.current.ecoReports, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Hero(report: EcoReport) {
    Box(
        modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFFFCCBC), Color(0xFFD84315)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
            Text(report.photoLabel, color = Color.White, fontWeight = FontWeight.Bold)
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
        Icon(icon, contentDescription = null, tint = Color(0xFFD84315), modifier = Modifier.size(18.dp))
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
private fun StatusBadge(status: EcoReportStatus, severity: EcoReportSeverity) {
    val color = when (status) {
        EcoReportStatus.Resolved -> Color(0xFF2E7D32)
        EcoReportStatus.Rejected -> Color(0xFF757575)
        EcoReportStatus.InProgress -> Color(0xFFF5A623)
        EcoReportStatus.Submitted,
        EcoReportStatus.Verified -> if (severity == EcoReportSeverity.High) Color(0xFFD32F2F) else Color(0xFFD84315)
    }
    Text(LocalEcoStrings.current.ecoReportStatusName(status), modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(color.copy(alpha = 0.14f)).padding(horizontal = 10.dp, vertical = 6.dp), color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
}

@Composable
private fun IssueChip(issue: EcoReportIssueType, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = LocalEcoStrings.current.ecoReportIssueName(issue),
        modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(if (selected) Color(0xFFD84315) else MaterialTheme.colorScheme.surfaceVariant).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
}

package com.ecodala.feature.scanner.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Recycling
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.components.EcoCameraCapturePanel
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun AiWasteScannerRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AiWasteScannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AiWasteScannerScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onScanClick = viewModel::scanWaste,
        modifier = modifier
    )
}

@Composable
fun AiWasteScannerScreen(
    uiState: AiWasteScannerUiState,
    onBackClick: () -> Unit,
    onScanClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCamera by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            showCamera = true
        } else {
            onScanClick(null)
        }
    }
    fun openCameraScanner() {
        if (hasCameraPermission) {
            showCamera = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            ScannerTopBar(onBackClick = onBackClick)
            Spacer(modifier = Modifier.height(18.dp))
            ScannerHeroCard(
                isScanning = uiState.isScanning,
                onScanClick = ::openCameraScanner
            )
            if (showCamera) {
                Spacer(modifier = Modifier.height(14.dp))
                EcoCameraCapturePanel(
                    title = "AI camera scanner",
                    frameHint = "Place one waste item inside the frame",
                    captureLabel = "Capture and analyze",
                    onCloseClick = { showCamera = false },
                    onCaptured = { photoPath ->
                        showCamera = false
                        onScanClick(photoPath)
                    },
                    onCaptureFailed = {
                        showCamera = false
                        onScanClick(null)
                    }
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            uiState.scannerResult?.let { result ->
                ScannerResultCard(
                    result = result,
                    nearestPoint = uiState.nearestPoint
                )
            }
        }
    }
}

@Composable
private fun ScannerTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = EcoGreen)
        }
        Text(
            text = "AI Waste Scanner",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ScannerHeroCard(
    isScanning: Boolean,
    onScanClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .background(EcoGreen.copy(alpha = 0.13f))
                    .clickable(onClick = onScanClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isScanning) Icons.Filled.Psychology else Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isScanning) "Analyzing waste..." else "Scan waste with AI",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Detect waste type, recyclability and nearest accepted point.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onScanClick,
                enabled = !isScanning,
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Scan", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ScannerResultCard(
    result: ScannerResult,
    nearestPoint: RecyclingPoint?
) {
    val strings = LocalEcoStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ResultRow(
                icon = Icons.Filled.Recycling,
                title = strings.wasteTypeName(result.wasteType),
                subtitle = "${(result.confidence * 100).toInt()}% confidence"
            )
            ResultRow(
                icon = Icons.Filled.CheckCircle,
                title = "Recyclable",
                subtitle = result.disposalHint
            )
            nearestPoint?.let {
                ResultRow(
                    icon = Icons.Filled.LocationOn,
                    title = it.name,
                    subtitle = "${it.address} - ${it.distanceMeters} m away"
                )
            }
        }
    }
}

@Composable
private fun ResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(EcoGreen.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AiWasteScannerScreenPreview() {
    EcoDalaTheme {
        AiWasteScannerScreen(
            uiState = AiWasteScannerUiState(
                scannerResult = DummyEcoData.scannerResult,
                nearestPoint = DummyEcoData.recyclingPoints.first()
            ),
            onBackClick = {},
            onScanClick = {}
        )
    }
}

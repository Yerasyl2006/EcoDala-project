package com.ecodala.feature.submit.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.localization.acceptedByEcodalaPoints
import com.ecodala.core.localization.aiScannerUnavailable
import com.ecodala.core.localization.approvedPoints
import com.ecodala.core.localization.cameraCaptureFailed
import com.ecodala.core.localization.clear
import com.ecodala.core.localization.confidencePercent
import com.ecodala.core.localization.nearestAcceptedPoint
import com.ecodala.core.localization.recyclable
import com.ecodala.core.localization.submissionFailed
import com.ecodala.core.localization.submitCaptureAndScan
import com.ecodala.core.localization.submitFrameHint
import com.ecodala.core.localization.submitPhotoAttached
import com.ecodala.core.localization.submitPhotoScanSubtitle
import com.ecodala.core.localization.submitPhotoUploadSubtitle
import com.ecodala.core.localization.submitScanningWaste
import com.ecodala.core.localization.submittedForReview
import com.ecodala.core.localization.submittingWaste
import com.ecodala.core.ui.components.EcoFormError
import com.ecodala.core.ui.components.EcoCameraCapturePanel
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun SubmitWasteRoute(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SubmitWasteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SubmitWasteScreen(
        uiState = uiState,
        onWasteTypeChange = viewModel::onWasteTypeChange,
        onQuantityDecrease = viewModel::decreaseQuantity,
        onQuantityIncrease = viewModel::increaseQuantity,
        onUnitChange = viewModel::onUnitChange,
        onCommentChange = viewModel::onCommentChange,
        onScanPhotoClick = viewModel::scanWastePhoto,
        onCapturedPhotoScan = viewModel::scanCapturedPhoto,
        onCaptureFailed = viewModel::onCameraCaptureFailed,
        onClearScanClick = viewModel::clearScanResult,
        onBackClick = onBackClick,
        onSubmitClick = {
            viewModel.submitWaste(onSuccess = onSubmitClick)
        },
        modifier = modifier
    )
}

@Composable
fun SubmitWasteScreen(
    uiState: SubmitWasteUiState,
    onWasteTypeChange: (WasteType) -> Unit,
    onQuantityDecrease: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onUnitChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onScanPhotoClick: () -> Unit,
    onCapturedPhotoScan: (String) -> Unit,
    onCaptureFailed: () -> Unit,
    onClearScanClick: () -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current
    val context = LocalContext.current
    var showCameraScanner by remember { mutableStateOf(false) }
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
            showCameraScanner = true
        }
    }

    fun openCameraScanner() {
        if (hasCameraPermission) {
            showCameraScanner = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            SubmitTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                FormSection {
                    FieldLabel(strings.wasteType)
                    WasteTypeSelector(
                        selectedType = uiState.wasteType,
                        onTypeSelected = onWasteTypeChange
                    )
                }

                FormSection {
                    FieldLabel(strings.quantity)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuantityStepper(
                            quantity = uiState.quantity,
                            onDecrease = onQuantityDecrease,
                            onIncrease = onQuantityIncrease,
                            modifier = Modifier.weight(1f)
                        )
                        UnitSelector(
                            selectedUnit = uiState.unit,
                            onUnitSelected = onUnitChange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FormSection {
                    FieldLabel(strings.photoConfirmation)
                    PhotoUploadBox(
                        photoPath = uiState.photoPath,
                        aiScan = uiState.aiScan,
                        onScanPhotoClick = ::openCameraScanner,
                        onClearScanClick = onClearScanClick
                    )
                }

                FormSection {
                    FieldLabel(strings.commentOptional)
                    CommentBox(
                        value = uiState.comment,
                        onValueChange = onCommentChange
                    )
                }

                Button(
                    onClick = onSubmitClick,
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D8B39),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (uiState.isSubmitting) strings.submittingWaste else strings.submitWithPoints(uiState.rewardPoints),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                uiState.submitMessage?.let { message ->
                    val isError = message == SubmitWasteMessage.SubmissionFailed
                    Text(
                        text = strings.submitMessageText(message, uiState.submitMessagePoints),
                        color = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            EcoGreen
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

            if (showCameraScanner) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.48f))
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    EcoCameraCapturePanel(
                        title = strings.photoConfirmation,
                        frameHint = strings.submitFrameHint,
                        captureLabel = strings.submitCaptureAndScan,
                        onCloseClick = { showCameraScanner = false },
                        onCaptured = { photoPath ->
                            showCameraScanner = false
                            onCapturedPhotoScan(photoPath)
                        },
                        onCaptureFailed = {
                            showCameraScanner = false
                            onCaptureFailed()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmitTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = LocalEcoStrings.current.submitWaste,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FormSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun WasteTypeSelector(
    selectedType: WasteType,
    onTypeSelected: (WasteType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val strings = LocalEcoStrings.current

    Box {
        SelectBox(
            label = strings.wasteTypeName(selectedType),
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WasteType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(strings.wasteTypeName(type)) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepButton(icon = Icons.Filled.Remove, onClick = onDecrease)
        Text(
            text = quantity.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        StepButton(icon = Icons.Filled.Add, onClick = onIncrease)
    }
}

@Composable
private fun StepButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = EcoGreen,
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = onClick)
            .padding(3.dp)
    )
}

@Composable
private fun UnitSelector(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        SelectBox(
            label = selectedUnit,
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("kg", "pcs", "bag").forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectBox(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = null,
            tint = Color(0xFF717B73)
        )
    }
}

@Composable
private fun PhotoUploadBox(
    photoPath: String?,
    aiScan: AiScanUiState,
    onScanPhotoClick: () -> Unit,
    onClearScanClick: () -> Unit
) {
    val strings = LocalEcoStrings.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(148.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .dashedBorder(Color(0xFF8AC08B), 12.dp)
                .clickable(onClick = onScanPhotoClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(EcoGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (aiScan.isScanning) Icons.Filled.Psychology else Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when {
                        aiScan.isScanning -> strings.submitScanningWaste
                        photoPath != null -> strings.submitPhotoAttached
                        else -> strings.takePhotoUpload
                    },
                    color = EcoGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (photoPath != null) {
                        strings.submitPhotoUploadSubtitle
                    } else {
                        strings.submitPhotoScanSubtitle
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        aiScan.result?.let { result ->
            AiScannerResultCard(
                result = result,
                nearestPoint = aiScan.nearestPoint,
                onClearClick = onClearScanClick
            )
        }
        aiScan.errorMessage?.let { message ->
            EcoFormError(message = strings.submitMessageText(message, 0))
        }
    }
}

private fun com.ecodala.core.localization.EcoStrings.submitMessageText(
    message: SubmitWasteMessage,
    points: Int
): String {
    return when (message) {
        SubmitWasteMessage.AiScannerUnavailable -> aiScannerUnavailable
        SubmitWasteMessage.CameraCaptureFailed -> cameraCaptureFailed
        SubmitWasteMessage.SubmissionFailed -> submissionFailed
        SubmitWasteMessage.Approved -> approvedPoints(points)
        SubmitWasteMessage.SubmittedForReview -> submittedForReview
    }
}

@Composable
private fun AiScannerResultCard(
    result: ScannerResult,
    nearestPoint: RecyclingPoint?,
    onClearClick: () -> Unit
) {
    val strings = LocalEcoStrings.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(EcoGreen.copy(alpha = 0.10f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                text = "AI Waste Scanner",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = strings.clear,
                modifier = Modifier.clickable(onClick = onClearClick),
                color = EcoGreen,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AiInfoPill(
                icon = Icons.Filled.Recycling,
                title = strings.wasteTypeName(result.wasteType),
                subtitle = strings.confidencePercent((result.confidence * 100).toInt()),
                modifier = Modifier.weight(1f)
            )
            AiInfoPill(
                icon = Icons.Filled.CheckCircle,
                title = strings.recyclable,
                subtitle = strings.acceptedByEcodalaPoints,
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = result.disposalHint,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )

        nearestPoint?.let { point ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.nearestAcceptedPoint,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = point.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${point.distanceMeters} m",
                    color = EcoGreen,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AiInfoPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun CommentBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = LocalEcoStrings.current.addNote,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        )
    }
}

private fun Modifier.dashedBorder(color: Color, cornerRadius: androidx.compose.ui.unit.Dp): Modifier {
    return drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SubmitWasteScreenPreview() {
    EcoDalaTheme {
        SubmitWasteScreen(
            uiState = SubmitWasteUiState(),
            onWasteTypeChange = {},
            onQuantityDecrease = {},
            onQuantityIncrease = {},
            onUnitChange = {},
            onCommentChange = {},
            onScanPhotoClick = {},
            onCapturedPhotoScan = {},
            onCaptureFailed = {},
            onClearScanClick = {},
            onBackClick = {},
            onSubmitClick = {}
        )
    }
}


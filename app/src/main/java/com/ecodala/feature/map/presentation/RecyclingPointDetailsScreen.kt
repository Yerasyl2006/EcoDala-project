package com.ecodala.feature.map.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun RecyclingPointDetailsRoute(
    onBackClick: () -> Unit,
    onBuildRouteClick: () -> Unit,
    onCallClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecyclingPointDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    uiState.point?.let { point ->
        RecyclingPointDetailsScreen(
            point = point,
            sustainableFact = uiState.sustainableFact,
            onBackClick = onBackClick,
            onBuildRouteClick = onBuildRouteClick,
            onCallClick = { context.openDialer(point.phone) },
            onShareClick = { context.sharePoint(point) },
            modifier = modifier
        )
    }
}

@Composable
fun RecyclingPointDetailsScreen(
    point: RecyclingPoint,
    sustainableFact: String,
    onBackClick: () -> Unit,
    onBuildRouteClick: () -> Unit,
    onCallClick: () -> Unit,
    onShareClick: () -> Unit,
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
                .verticalScroll(rememberScrollState())
        ) {
            DetailsTopBar(
                onBackClick = onBackClick,
                onShareClick = onShareClick
            )

            FacilityHero()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 20.dp, bottom = 28.dp)
            ) {
                Text(
                    text = point.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                PointInfoRow(Icons.Filled.LocationOn, point.address)
                Spacer(modifier = Modifier.height(9.dp))
                PointInfoRow(Icons.Filled.Phone, point.phone)
                Spacer(modifier = Modifier.height(9.dp))
                PointInfoRow(
                    icon = Icons.Filled.AccessTime,
                    text = point.openingHours,
                    trailing = {
                        Text(
                            text = LocalEcoStrings.current.open,
                            modifier = Modifier
                                .background(Color(0xFFDDF1DC), RoundedCornerShape(20.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp),
                            color = EcoGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = LocalEcoStrings.current.acceptedWasteTypes,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                AcceptedWasteGrid(types = point.acceptedWasteTypes)

                Spacer(modifier = Modifier.height(24.dp))

                SustainableFactCard(text = sustainableFact)

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onBuildRouteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Route,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = LocalEcoStrings.current.buildRoute,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedActionButton(
                        label = LocalEcoStrings.current.call,
                        icon = Icons.Filled.Call,
                        onClick = onCallClick,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedActionButton(
                        label = LocalEcoStrings.current.share,
                        icon = Icons.Filled.Share,
                        onClick = onShareClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsTopBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color(0xFFFCFFF8))
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FacilityHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(206.dp)
            .background(Color(0xFFDDEDE8)),
        contentAlignment = Alignment.BottomEnd
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFacilityImage()
        }
        Text(
            text = "1 / 3",
            modifier = Modifier
                .padding(18.dp)
                .background(Color.Black.copy(alpha = 0.42f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PointInfoRow(
    icon: ImageVector,
    text: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = text,
            color = Color(0xFF4D574F),
            style = MaterialTheme.typography.bodyMedium
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.size(10.dp))
            trailing()
        }
    }
}

@Composable
private fun AcceptedWasteGrid(types: List<WasteType>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        types.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { type ->
                    WasteTypeCard(
                        type = type,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WasteTypeCard(
    type: WasteType,
    modifier: Modifier = Modifier
) {
    val spec = type.visualSpec()
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(spec.background, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = spec.icon,
                contentDescription = null,
                tint = spec.tint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            text = LocalEcoStrings.current.wasteTypeName(type),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun SustainableFactCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE3F8E2))
            .border(1.dp, Color(0xFFBDE8BC), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.size(14.dp))
        Column {
            Text(
                text = LocalEcoStrings.current.sustainableFact,
                color = Color(0xFF2C6E36),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = text,
                color = Color(0xFF497250),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun OutlinedActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EcoGreen)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class WasteVisualSpec(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

private fun WasteType.visualSpec(): WasteVisualSpec {
    return when (this) {
        WasteType.Plastic -> WasteVisualSpec(Icons.Filled.Recycling, Color(0xFFDCEBFF), Color(0xFF4385F5))
        WasteType.Paper -> WasteVisualSpec(Icons.Filled.InsertDriveFile, Color(0xFFFFE9D6), Color(0xFFFF8A3D))
        WasteType.Glass -> WasteVisualSpec(Icons.Filled.WineBar, Color(0xFFD7FAF4), Color(0xFF18A999))
        WasteType.Batteries -> WasteVisualSpec(Icons.Filled.BatteryChargingFull, Color(0xFFFFF6C8), Color(0xFFC99713))
        WasteType.Electronics -> WasteVisualSpec(Icons.Filled.Memory, Color(0xFFEBD9FF), Color(0xFF9B5DF5))
        WasteType.Organic -> WasteVisualSpec(Icons.Filled.Recycling, Color(0xFFE3F8E2), EcoGreen)
        WasteType.Metal -> WasteVisualSpec(Icons.Filled.Recycling, Color(0xFFE8ECEF), Color(0xFF64727D))
    }
}

private fun DrawScope.drawFacilityImage() {
    drawRect(
        brush = Brush.verticalGradient(
            listOf(Color(0xFFCAE9EA), Color(0xFFBCE0DE), Color(0xFFEFEFDA))
        )
    )
    repeat(6) { index ->
        val y = 22.dp.toPx() + index * 15.dp.toPx()
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(0f, y),
            end = Offset(size.width, y - 20.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
    }
    drawRect(
        color = Color(0xFFE8E4D2),
        topLeft = Offset(0f, size.height * 0.70f),
        size = Size(size.width, size.height * 0.30f)
    )
    drawRoundRect(
        color = Color(0xFF254D32),
        topLeft = Offset(size.width * 0.18f, size.height * 0.40f),
        size = Size(size.width * 0.70f, size.height * 0.34f),
        cornerRadius = CornerRadius(2.dp.toPx())
    )
    drawRect(
        color = Color(0xFF153522),
        topLeft = Offset(size.width * 0.76f, size.height * 0.54f),
        size = Size(size.width * 0.07f, size.height * 0.20f)
    )
    drawCircle(
        color = Color(0xFF5F9A41),
        radius = 38.dp.toPx(),
        center = Offset(size.width * 0.05f, size.height * 0.67f)
    )
    drawPath(
        path = Path().apply {
            moveTo(0f, size.height * 0.72f)
            cubicTo(size.width * 0.18f, size.height * 0.68f, size.width * 0.42f, size.height * 0.72f, size.width, size.height * 0.66f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        },
        color = Color(0xFFD4D0BC)
    )
}

private fun Context.openDialer(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    startActivity(intent)
}

private fun Context.sharePoint(point: RecyclingPoint) {
    val text = "${point.name}\n${point.address}\nhttps://www.google.com/maps/search/?api=1&query=${point.latitude},${point.longitude}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, "Share recycling point"))
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RecyclingPointDetailsScreenPreview() {
    EcoDalaTheme {
        RecyclingPointDetailsScreen(
            point = DummyEcoData.recyclingPoints.first(),
            sustainableFact = "Recycling one ton of paper saves about 17 trees and thousands of liters of water.",
            onBackClick = {},
            onBuildRouteClick = {},
            onCallClick = {},
            onShareClick = {}
        )
    }
}

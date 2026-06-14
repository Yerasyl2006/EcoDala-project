package com.ecodala.feature.splash.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.adaptive.isCompactHeight
import com.ecodala.core.ui.theme.EcoDalaTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableStateOf(false) }
    val compactHeight = isCompactHeight()
    val logoSize = if (compactHeight) 96.dp else 112.dp
    val titleGap = if (compactHeight) 18.dp else 22.dp
    val bottomOffset = if (compactHeight) (-24).dp else (-34).dp
    val progress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1_350,
            easing = FastOutSlowInEasing
        ),
        label = "splashProgress"
    )

    LaunchedEffect(Unit) {
        animationStarted = true
        delay(1_650)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D6724),
                        Color(0xFF16772B),
                        Color(0xFF258B38)
                    )
                )
            )
    ) {
        DecorativeSplashIcons()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = progress
                    scaleX = 0.86f + (progress * 0.14f)
                    scaleY = 0.86f + (progress * 0.14f)
                    translationY = (1f - progress) * 22.dp.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EcoLogo(logoSize = logoSize)

            Spacer(modifier = Modifier.height(titleGap))

            Text(
                text = "EcoDala",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = LocalEcoStrings.current.splashTagline,
                color = Color.White.copy(alpha = 0.64f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        BottomExperienceText(
            progress = progress,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = bottomOffset)
        )
    }
}

@Composable
private fun EcoLogo(
    logoSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(logoSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }

        Box(
            modifier = Modifier
                .size(58.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(34.dp)) {
                drawLeaf(color = Color(0xFF16823B))
            }
        }
    }
}

@Composable
private fun DecorativeSplashIcons() {
    SplashIcon(
        imageVector = Icons.Filled.Spa,
        modifier = Modifier
            .offset(x = 62.dp, y = 104.dp)
            .rotate(-18f)
    )
    SplashIcon(
        imageVector = Icons.Filled.Spa,
        modifier = Modifier
            .alignWithOffset(x = 286.dp, y = 164.dp)
            .rotate(18f)
    )
    SplashIcon(
        imageVector = Icons.Filled.LocalFlorist,
        modifier = Modifier
            .alignWithOffset(x = 274.dp, y = 430.dp)
            .rotate(14f)
    )
    SplashIcon(
        imageVector = Icons.Filled.Eco,
        modifier = Modifier
            .offset(x = 92.dp, y = 518.dp)
            .rotate(-12f)
    )
}

@Composable
private fun SplashIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = Color.White.copy(alpha = 0.13f),
        modifier = modifier.size(24.dp)
    )
}

@Composable
private fun BottomExperienceText(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.alpha(0.55f + progress * 0.45f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 170.dp, height = 3.dp)
                .background(Color.White.copy(alpha = 0.24f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(3.dp)
                    .background(Color.White.copy(alpha = 0.92f), CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = LocalEcoStrings.current.splashExperience,
            color = Color.White.copy(alpha = 0.22f),
            fontSize = 10.sp,
            letterSpacing = 1.3.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun DrawScope.drawLeaf(color: Color) {
    val leaf = Path().apply {
        moveTo(size.width * 0.18f, size.height * 0.62f)
        cubicTo(
            size.width * 0.22f,
            size.height * 0.18f,
            size.width * 0.68f,
            size.height * 0.05f,
            size.width * 0.88f,
            size.height * 0.18f
        )
        cubicTo(
            size.width * 0.92f,
            size.height * 0.66f,
            size.width * 0.56f,
            size.height * 0.92f,
            size.width * 0.18f,
            size.height * 0.62f
        )
        close()
    }

    drawPath(path = leaf, color = color)
    drawLine(
        color = Color.White.copy(alpha = 0.78f),
        start = Offset(size.width * 0.30f, size.height * 0.70f),
        end = Offset(size.width * 0.72f, size.height * 0.32f),
        strokeWidth = 2.dp.toPx()
    )
}

private fun Modifier.alignWithOffset(x: androidx.compose.ui.unit.Dp, y: androidx.compose.ui.unit.Dp): Modifier {
    return this.offset(x = x, y = y)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SplashScreenPreview() {
    EcoDalaTheme {
        SplashScreen()
    }
}


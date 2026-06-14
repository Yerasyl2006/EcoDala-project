package com.ecodala.feature.auth.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun EmailVerificationRoute(
    onContinueClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmailVerificationScreen(
        onContinueClick = onContinueClick,
        onBackToLoginClick = onBackToLoginClick,
        modifier = modifier
    )
}

@Composable
fun EmailVerificationScreen(
    onContinueClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var started by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "verificationSuccess"
    )

    LaunchedEffect(Unit) {
        started = true
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(126.dp)) {
                    drawCircle(
                        color = EcoGreen.copy(alpha = 0.16f),
                        radius = size.minDimension / 2
                    )
                    drawArc(
                        color = EcoGreen,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx())
                    )
                }
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .scale(0.84f + progress * 0.16f)
                        .background(EcoGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MarkEmailRead,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Verify your email",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We sent a verification link to your email. Confirm it to secure your EcoDala account.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Demo mode: email verification is simulated. In production this screen connects to Firebase email verification.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
            ) {
                Text(text = "Continue to EcoDala", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Back to login",
                modifier = Modifier.clickable(onClick = onBackToLoginClick),
                color = EcoGreen,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailVerificationScreenPreview() {
    EcoDalaTheme {
        EmailVerificationScreen(
            onContinueClick = {},
            onBackToLoginClick = {}
        )
    }
}

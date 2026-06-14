package com.ecodala.core.ui.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun isCompactHeight(): Boolean = LocalConfiguration.current.screenHeightDp < 820

@Composable
fun isExtraCompactHeight(): Boolean = LocalConfiguration.current.screenHeightDp < 720

@Composable
fun isCompactWidth(): Boolean = LocalConfiguration.current.screenWidthDp < 380

@Composable
fun horizontalScreenPadding(
    regular: Dp = 22.dp,
    compact: Dp = 16.dp
): Dp = if (isCompactWidth()) compact else regular

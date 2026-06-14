package com.ecodala.feature.auth.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.adaptive.horizontalScreenPadding
import com.ecodala.core.ui.adaptive.isCompactHeight
import com.ecodala.core.ui.adaptive.isExtraCompactHeight
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun LoginRoute(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = onLoginClick,
        onRegisterClick = onRegisterClick,
        onForgotPasswordClick = onForgotPasswordClick,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current
    val compactHeight = isCompactHeight()
    val extraCompactHeight = isExtraCompactHeight()
    val horizontalPadding = horizontalScreenPadding(regular = 22.dp, compact = 18.dp)
    val headerHeight = when {
        extraCompactHeight -> 168.dp
        compactHeight -> 190.dp
        else -> 248.dp
    }
    val contentTopPadding = if (compactHeight) 12.dp else 22.dp
    val fieldGap = if (compactHeight) 8.dp else 14.dp
    val sectionGap = if (compactHeight) 14.dp else 24.dp
    val socialGap = if (compactHeight) 12.dp else 20.dp
    val bottomGap = if (compactHeight) 14.dp else 36.dp

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            LoginHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .padding(top = contentTopPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.welcomeBack,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = strings.signInSubtitle,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(sectionGap))

                EcoAuthTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    placeholder = strings.emailAddress,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(fieldGap))

                PasswordTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.forgotPassword,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onForgotPasswordClick),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(sectionGap))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D8B39),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = strings.login,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(if (compactHeight) 16.dp else 22.dp))

                OrDivider()

                Spacer(modifier = Modifier.height(socialGap))

                GoogleSignInButton(onClick = {})

                Spacer(modifier = Modifier.height(bottomGap))

                RegisterPrompt(
                    onRegisterClick = onRegisterClick,
                    modifier = Modifier.padding(bottom = if (compactHeight) 12.dp else 34.dp)
                )
            }
        }
    }
}

@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHeaderArtwork()
        }

        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(EcoGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun EcoAuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EcoGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLeadingIconColor = EcoGreen,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTrailingIconColor = EcoGreen,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    EcoAuthTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = LocalEcoStrings.current.password,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = LocalEcoStrings.current.or,
            modifier = Modifier.padding(horizontal = 14.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "G",
            color = Color(0xFF4285F4),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = LocalEcoStrings.current.continueWithGoogle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RegisterPrompt(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append(strings.noAccountPrefix)
            }
            withStyle(SpanStyle(color = EcoGreen, fontWeight = FontWeight.Medium)) {
                append(strings.registerNow)
            }
        },
        modifier = modifier.clickable(onClick = onRegisterClick),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

private fun DrawScope.drawHeaderArtwork() {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.10f)),
            center = Offset(size.width * 0.52f, size.height * 0.48f),
            radius = size.minDimension * 0.42f
        ),
        radius = size.minDimension * 0.40f,
        center = Offset(size.width * 0.52f, size.height * 0.48f)
    )

    rotate(degrees = -28f, pivot = Offset(size.width * 0.34f, size.height * 0.42f)) {
        drawDecorativeLeaf(
            topLeft = Offset(size.width * 0.17f, size.height * 0.20f),
            size = Size(size.width * 0.35f, size.height * 0.42f),
            color = Color(0xFF82B5A4).copy(alpha = 0.66f)
        )
    }
    rotate(degrees = 28f, pivot = Offset(size.width * 0.70f, size.height * 0.62f)) {
        drawDecorativeLeaf(
            topLeft = Offset(size.width * 0.57f, size.height * 0.40f),
            size = Size(size.width * 0.30f, size.height * 0.39f),
            color = Color(0xFF6FA99A).copy(alpha = 0.60f)
        )
    }
    rotate(degrees = 18f, pivot = Offset(size.width * 0.58f, size.height * 0.23f)) {
        drawDecorativeLeaf(
            topLeft = Offset(size.width * 0.48f, size.height * 0.04f),
            size = Size(size.width * 0.26f, size.height * 0.32f),
            color = Color(0xFF6DA895).copy(alpha = 0.42f)
        )
    }

    repeat(18) { index ->
        val x = size.width * (0.25f + (index % 6) * 0.09f)
        val y = size.height * (0.18f + (index / 6) * 0.18f)
        drawCircle(
            color = Color(0xFF619C8B).copy(alpha = 0.20f),
            radius = 2.2.dp.toPx(),
            center = Offset(x, y)
        )
    }

    drawRoundRect(
        color = Color.White.copy(alpha = 0.58f),
        topLeft = Offset(size.width * 0.36f, size.height * 0.58f),
        size = Size(size.width * 0.30f, 12.dp.toPx()),
        cornerRadius = CornerRadius(8.dp.toPx())
    )
}

private fun DrawScope.drawDecorativeLeaf(
    topLeft: Offset,
    size: Size,
    color: Color
) {
    val leaf = Path().apply {
        moveTo(topLeft.x + size.width * 0.10f, topLeft.y + size.height * 0.82f)
        cubicTo(
            topLeft.x + size.width * 0.08f,
            topLeft.y + size.height * 0.26f,
            topLeft.x + size.width * 0.72f,
            topLeft.y + size.height * 0.02f,
            topLeft.x + size.width * 0.94f,
            topLeft.y + size.height * 0.12f
        )
        cubicTo(
            topLeft.x + size.width * 0.98f,
            topLeft.y + size.height * 0.66f,
            topLeft.x + size.width * 0.52f,
            topLeft.y + size.height,
            topLeft.x + size.width * 0.10f,
            topLeft.y + size.height * 0.82f
        )
        close()
    }

    drawPath(path = leaf, color = color)
    drawLine(
        color = Color.White.copy(alpha = 0.22f),
        start = Offset(topLeft.x + size.width * 0.20f, topLeft.y + size.height * 0.78f),
        end = Offset(topLeft.x + size.width * 0.78f, topLeft.y + size.height * 0.18f),
        strokeWidth = 2.dp.toPx()
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LoginScreenPreview() {
    EcoDalaTheme {
        LoginScreen(
            uiState = AuthUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {}
        )
    }
}

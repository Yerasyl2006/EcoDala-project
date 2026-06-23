package com.ecodala.feature.auth.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
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
import com.ecodala.core.domain.model.SocialAuthProvider
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.adaptive.horizontalScreenPadding
import com.ecodala.core.ui.adaptive.isCompactHeight
import com.ecodala.core.ui.adaptive.isExtraCompactHeight
import com.ecodala.core.ui.components.EcoFormError
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen
import com.ecodala.R

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
        onRememberMeChange = viewModel::onRememberMeChange,
        onLoginClick = { viewModel.onLoginSubmit(onLoginClick) },
        onSocialLoginClick = { provider -> viewModel.onSocialLoginSubmit(provider, onLoginClick) },
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
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onSocialLoginClick: (SocialAuthProvider) -> Unit,
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
    var pendingSocialProvider by remember { mutableStateOf<SocialAuthProvider?>(null) }

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
                    text = strings.login,
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

                Spacer(modifier = Modifier.height(10.dp))

                LoginOptionsRow(
                    rememberMe = uiState.rememberMe,
                    onRememberMeChange = onRememberMeChange,
                    onForgotPasswordClick = onForgotPasswordClick
                )

                uiState.errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(10.dp))
                    EcoFormError(message = message)
                }

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
                        text = if (uiState.isLoading) strings.signingIn else strings.login,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(if (compactHeight) 16.dp else 22.dp))

                OrDivider()

                Spacer(modifier = Modifier.height(socialGap))

                SocialLoginRow(
                    onSocialLoginClick = { provider ->
                        pendingSocialProvider = provider
                    }
                )

                Spacer(modifier = Modifier.height(bottomGap))

                RegisterPrompt(
                    onRegisterClick = onRegisterClick,
                    modifier = Modifier.padding(bottom = if (compactHeight) 12.dp else 34.dp)
                )
            }
        }

        pendingSocialProvider?.let { provider ->
            SocialAuthDemoDialog(
                provider = provider,
                onDismiss = { pendingSocialProvider = null },
                onContinue = {
                    pendingSocialProvider = null
                    onSocialLoginClick(provider)
                }
            )
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
private fun LoginOptionsRow(
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onRememberMeChange(!rememberMe) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = onRememberMeChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = EcoGreen,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )
            Text(
                text = LocalEcoStrings.current.rememberMe,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Text(
            text = LocalEcoStrings.current.forgotPassword,
            modifier = Modifier.clickable(onClick = onForgotPasswordClick),
            color = EcoGreen,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun LoginErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
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
private fun SocialLoginRow(
    onSocialLoginClick: (SocialAuthProvider) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SocialSignInButton(
            label = "Google",
            leadingContent = {
                Text(
                    text = "G",
                    color = Color(0xFF4285F4),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            onClick = { onSocialLoginClick(SocialAuthProvider.Google) },
            modifier = Modifier.weight(1f)
        )
        SocialSignInButton(
            label = "Apple",
            leadingContent = {
                SocialProviderLogo(
                    provider = SocialAuthProvider.Apple,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = { onSocialLoginClick(SocialAuthProvider.Apple) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SocialSignInButton(
    label: String,
    leadingContent: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent()
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SocialAuthDemoDialog(
    provider: SocialAuthProvider,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    val strings = LocalEcoStrings.current
    val providerName = when (provider) {
        SocialAuthProvider.Google -> "Google"
        SocialAuthProvider.Apple -> "Apple"
    }
    val providerEmail = when (provider) {
        SocialAuthProvider.Google -> "google.user@ecodala.com"
        SocialAuthProvider.Apple -> "apple.user@ecodala.com"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            SocialProviderLogo(provider = provider, modifier = Modifier.size(34.dp))
        },
        title = {
            Text(
                text = if (provider == SocialAuthProvider.Google) strings.continueWithGoogle else "${strings.continueAction} $providerName",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "EcoDala will use this demo $providerName account for sign in.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = providerEmail,
                    color = EcoGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onContinue) {
                Text(text = strings.continueAction, color = EcoGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = strings.cancel, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun SocialProviderLogo(
    provider: SocialAuthProvider,
    modifier: Modifier = Modifier
) {
    when (provider) {
        SocialAuthProvider.Google -> {
            Text(
                text = "G",
                color = Color(0xFF4285F4),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier
            )
        }
        SocialAuthProvider.Apple -> {
            Icon(
                painter = painterResource(id = R.drawable.ic_apple_logo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = modifier
            )
        }
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
            onRememberMeChange = {},
            onLoginClick = {},
            onSocialLoginClick = {},
            onRegisterClick = {},
            onForgotPasswordClick = {}
        )
    }
}

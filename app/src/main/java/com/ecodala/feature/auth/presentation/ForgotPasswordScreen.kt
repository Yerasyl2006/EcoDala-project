package com.ecodala.feature.auth.presentation

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.adaptive.horizontalScreenPadding
import com.ecodala.core.ui.adaptive.isCompactHeight
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun ForgotPasswordRoute(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ForgotPasswordScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onSendClick = viewModel::sendResetLink,
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current
    val compactHeight = isCompactHeight()
    val horizontalPadding = horizontalScreenPadding(regular = 22.dp, compact = 18.dp)
    val topPadding = if (compactHeight) 18.dp else 34.dp
    val contentGap = if (compactHeight) 18.dp else 24.dp
    val formTopGap = if (compactHeight) 22.dp else 30.dp

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
        ) {
            ForgotPasswordTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding)
                    .padding(top = topPadding, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResetIcon(
                    emailSent = uiState.emailSent,
                    compactHeight = compactHeight
                )

                Spacer(modifier = Modifier.height(contentGap))

                Text(
                    text = if (uiState.emailSent) strings.checkYourEmail else strings.forgotPassword,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (uiState.emailSent) {
                        strings.resetInstructions(uiState.email)
                    } else {
                        strings.forgotPasswordSubtitle
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(formTopGap))

                if (uiState.emailSent) {
                    SuccessCard(email = uiState.email)
                } else {
                    ForgotPasswordForm(
                        uiState = uiState,
                        onEmailChange = onEmailChange,
                        onSendClick = onSendClick
                    )
                }

                Spacer(modifier = Modifier.height(contentGap))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                            append(strings.rememberedPassword)
                        }
                        withStyle(SpanStyle(color = EcoGreen, fontWeight = FontWeight.Bold)) {
                            append(strings.login)
                        }
                    },
                    modifier = Modifier.clickable(onClick = onLoginClick),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ForgotPasswordTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = EcoGreen
            )
        }
    }
}

@Composable
private fun ResetIcon(
    emailSent: Boolean,
    compactHeight: Boolean
) {
    val outerSize = if (compactHeight) 88.dp else 104.dp
    val innerSize = if (compactHeight) 60.dp else 70.dp
    val iconSize = if (compactHeight) 32.dp else 36.dp

    Box(
        modifier = Modifier
            .size(outerSize)
            .background(EcoGreen.copy(alpha = 0.13f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(RoundedCornerShape(20.dp))
                .background(EcoGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (emailSent) Icons.Filled.MarkEmailRead else Icons.Filled.LockReset,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun ForgotPasswordForm(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                placeholder = {
                    Text(LocalEcoStrings.current.emailAddress, color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errorMessage != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedLeadingIconColor = EcoGreen,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
                )
            )

            uiState.errorMessage?.let { message ->
                Text(
                    text = if (message == "invalid_email") LocalEcoStrings.current.invalidEmail else message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onSendClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
            ) {
                Text(LocalEcoStrings.current.sendResetLink, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SuccessCard(email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGreen.copy(alpha = 0.13f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = EcoGreen,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(
                    text = LocalEcoStrings.current.resetLinkSent,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ForgotPasswordScreenPreview() {
    EcoDalaTheme {
        ForgotPasswordScreen(
            uiState = ForgotPasswordUiState(),
            onEmailChange = {},
            onSendClick = {},
            onBackClick = {},
            onLoginClick = {}
        )
    }
}

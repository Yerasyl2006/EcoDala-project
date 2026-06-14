package com.ecodala.feature.auth.presentation

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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun RegisterRoute(
    onBackClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RegisterScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onAcceptedTermsChange = viewModel::onAcceptedTermsChange,
        onBackClick = onBackClick,
        onCreateAccountClick = onCreateAccountClick,
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onAcceptedTermsChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current
    val compactHeight = isCompactHeight()
    val extraCompactHeight = isExtraCompactHeight()
    val horizontalPadding = horizontalScreenPadding(regular = 22.dp, compact = 18.dp)
    val contentTopPadding = if (compactHeight) 10.dp else 22.dp
    val fieldGap = if (compactHeight) 8.dp else 14.dp
    val sectionGap = if (compactHeight) 14.dp else 22.dp
    val largeGap = if (compactHeight) 16.dp else 28.dp
    val buttonHeight = if (compactHeight) 52.dp else 58.dp

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
            RegisterTopBar(
                compactHeight = compactHeight,
                extraCompactHeight = extraCompactHeight,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .padding(top = contentTopPadding, bottom = 20.dp)
            ) {
                Text(
                    text = strings.createAccount,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.joinCommunity,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(sectionGap))

                RegisterFieldLabel(strings.fullName)
                RegisterTextField(
                    value = uiState.fullName,
                    onValueChange = onFullNameChange,
                    placeholder = "John Doe",
                    leadingIcon = Icons.Filled.Person
                )

                Spacer(modifier = Modifier.height(fieldGap))

                RegisterFieldLabel(strings.emailAddress)
                RegisterTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    placeholder = "hello@ecodala.com",
                    leadingIcon = Icons.Filled.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(fieldGap))

                RegisterFieldLabel(strings.password)
                RegisterPasswordField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    leadingIcon = Icons.Filled.Lock
                )

                Spacer(modifier = Modifier.height(if (compactHeight) 6.dp else 10.dp))
                PasswordStrengthIndicator(password = uiState.password)

                Spacer(modifier = Modifier.height(if (compactHeight) 10.dp else 18.dp))

                RegisterFieldLabel(strings.confirmPassword)
                RegisterPasswordField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    leadingIcon = Icons.Filled.Shield,
                    showVisibilityToggle = false
                )

                Spacer(modifier = Modifier.height(if (compactHeight) 10.dp else 18.dp))

                TermsRow(
                    checked = uiState.acceptedTerms,
                    onCheckedChange = onAcceptedTermsChange
                )

                Spacer(modifier = Modifier.height(largeGap))

                Button(
                    onClick = onCreateAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D8B39),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = strings.createAccount,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(largeGap))

                OrSignUpWith()

                Spacer(modifier = Modifier.height(if (compactHeight) 12.dp else 22.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    SocialButton(
                        label = "Google",
                        leadingContent = {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        label = "Apple",
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Apps,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(if (compactHeight) 8.dp else 10.dp))

                LoginPrompt(
                    onLoginClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RegisterTopBar(
    compactHeight: Boolean,
    extraCompactHeight: Boolean,
    onBackClick: () -> Unit
) {
    val height = when {
        extraCompactHeight -> 48.dp
        compactHeight -> 54.dp
        else -> 78.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp),
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
private fun RegisterFieldLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
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
            .height(54.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(11.dp),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedLeadingIconColor = EcoGreen,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTrailingIconColor = EcoGreen,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun RegisterPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    showVisibilityToggle: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val trailingIcon: @Composable (() -> Unit)? = if (showVisibilityToggle) {
        {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        }
    } else {
        null
    }

    RegisterTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = "••••••••",
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = trailingIcon
    )
}

@Composable
private fun PasswordStrengthIndicator(password: String) {
    val strength = when {
        password.length >= 10 -> 1f
        password.length >= 6 -> 0.55f
        password.isNotEmpty() -> 0.25f
        else -> 0.55f
    }

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            repeat(4) { index ->
                LinearProgressIndicator(
                    progress = { if (index < (strength * 4).toInt()) 1f else 0f },
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EcoGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = LocalEcoStrings.current.fairlyStrongPassword,
            color = EcoGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TermsRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val strings = LocalEcoStrings.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = EcoGreen,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(strings.termsPrefix)
                }
                withStyle(SpanStyle(color = EcoGreen, fontWeight = FontWeight.SemiBold)) {
                    append(strings.terms)
                }
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(strings.termsAnd)
                }
                withStyle(SpanStyle(color = EcoGreen, fontWeight = FontWeight.SemiBold)) {
                    append(strings.privacyPolicy)
                }
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun OrSignUpWith() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            text = LocalEcoStrings.current.orSignUpWith,
            modifier = Modifier.padding(horizontal = 18.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun SocialButton(
    label: String,
    leadingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { },
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
private fun LoginPrompt(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                append(strings.alreadyHaveAccount)
            }
            withStyle(SpanStyle(color = EcoGreen, fontWeight = FontWeight.Bold)) {
                append(strings.login)
            }
        },
        modifier = modifier.clickable(onClick = onLoginClick),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RegisterScreenPreview() {
    EcoDalaTheme {
        RegisterScreen(
            uiState = AuthUiState(
                fullName = "John Doe",
                email = "hello@ecodala.com",
                password = "password",
                confirmPassword = "password"
            ),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onAcceptedTermsChange = {},
            onBackClick = {},
            onCreateAccountClick = {},
            onLoginClick = {}
        )
    }
}

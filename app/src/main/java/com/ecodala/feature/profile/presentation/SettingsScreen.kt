package com.ecodala.feature.profile.presentation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.settings.AppSettings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPushNotificationsChange = viewModel::setPushNotificationsEnabled,
        onLocationChange = viewModel::setLocationEnabled,
        onAiTipsChange = viewModel::setAiTipsEnabled,
        onPublicProfileChange = viewModel::setPublicProfileEnabled,
        onDarkModeChange = viewModel::setDarkModeEnabled,
        onPreferredUnitChange = viewModel::setPreferredUnit,
        onAppLanguageChange = viewModel::setAppLanguage,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    uiState: AppSettings,
    onBackClick: () -> Unit,
    onPushNotificationsChange: (Boolean) -> Unit,
    onLocationChange: (Boolean) -> Unit,
    onAiTipsChange: (Boolean) -> Unit,
    onPublicProfileChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onPreferredUnitChange: (String) -> Unit,
    onAppLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalEcoStrings.current
    val pageColor = if (uiState.darkModeEnabled) Color(0xFF0B120C) else MaterialTheme.colorScheme.background
    val cardColor = if (uiState.darkModeEnabled) Color(0xFF172018) else Color.White
    val primaryText = if (uiState.darkModeEnabled) Color(0xFFE4EDE2) else MaterialTheme.colorScheme.onSurface
    val secondaryText = if (uiState.darkModeEnabled) Color(0xFFA9B7AA) else MaterialTheme.colorScheme.onSurfaceVariant
    val iconBackground = if (uiState.darkModeEnabled) Color(0xFF203522) else Color(0xFFE5F6EA)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = pageColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SettingsTopBar(
                onBackClick = onBackClick,
                pageColor = pageColor
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                SettingsSection(
                    title = strings.settingsPreferences,
                    cardColor = cardColor,
                    primaryText = primaryText
                ) {
                    SwitchSettingRow(
                        icon = Icons.Filled.Notifications,
                        title = strings.settingsPushNotifications,
                        subtitle = strings.settingsPushNotificationsSubtitle,
                        checked = uiState.pushNotificationsEnabled,
                        onCheckedChange = onPushNotificationsChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground
                    )
                    SwitchSettingRow(
                        icon = Icons.Filled.LocationOn,
                        title = strings.settingsLocation,
                        subtitle = strings.settingsLocationSubtitle,
                        checked = uiState.locationEnabled,
                        onCheckedChange = onLocationChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground
                    )
                    SwitchSettingRow(
                        icon = Icons.Filled.Psychology,
                        title = strings.settingsAiTips,
                        subtitle = strings.settingsAiTipsSubtitle,
                        checked = uiState.aiTipsEnabled,
                        onCheckedChange = onAiTipsChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                SettingsSection(
                    title = strings.settingsDisplayUnits,
                    cardColor = cardColor,
                    primaryText = primaryText
                ) {
                    SwitchSettingRow(
                        icon = Icons.Filled.DarkMode,
                        title = strings.settingsDarkMode,
                        subtitle = strings.settingsDarkModeSubtitle,
                        checked = uiState.darkModeEnabled,
                        onCheckedChange = onDarkModeChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground
                    )
                    UnitSettingRow(
                        selectedUnit = uiState.preferredUnit,
                        onUnitSelected = onPreferredUnitChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground,
                        darkMode = uiState.darkModeEnabled
                    )
                    LanguageSettingRow(
                        selectedLanguageTag = uiState.appLanguageTag,
                        onLanguageSelected = onAppLanguageChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground,
                        darkMode = uiState.darkModeEnabled
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                SettingsSection(
                    title = strings.settingsPrivacy,
                    cardColor = cardColor,
                    primaryText = primaryText
                ) {
                    SwitchSettingRow(
                        icon = Icons.Filled.Public,
                        title = strings.settingsPublicProfile,
                        subtitle = strings.settingsPublicProfileSubtitle,
                        checked = uiState.publicProfileEnabled,
                        onCheckedChange = onPublicProfileChange,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground
                    )
                    StaticSettingRow(
                        icon = Icons.Filled.Security,
                        title = strings.settingsDataSecurity,
                        subtitle = strings.settingsDataSecuritySubtitle,
                        primaryText = primaryText,
                        secondaryText = secondaryText,
                        iconBackground = iconBackground
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                InfoCard(darkMode = uiState.darkModeEnabled)
            }
        }
    }
}

@Composable
private fun SettingsTopBar(
    onBackClick: () -> Unit,
    pageColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(pageColor),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = EcoGreen)
        }

        Text(
            text = LocalEcoStrings.current.settings,
            color = EcoGreen,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .size(22.dp)
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    cardColor: Color,
    primaryText: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            color = primaryText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SwitchSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryText: Color,
    secondaryText: Color,
    iconBackground: Color
) {
    SettingRowBase(
        icon = icon,
        title = title,
        subtitle = subtitle,
        primaryText = primaryText,
        secondaryText = secondaryText,
        iconBackground = iconBackground
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EcoGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBFC8BE)
            )
        )
    }
}

@Composable
private fun StaticSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    primaryText: Color,
    secondaryText: Color,
    iconBackground: Color
) {
    SettingRowBase(
        icon = icon,
        title = title,
        subtitle = subtitle,
        primaryText = primaryText,
        secondaryText = secondaryText,
        iconBackground = iconBackground
    ) {
        Text(
            text = LocalEcoStrings.current.open,
            color = EcoGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UnitSettingRow(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    primaryText: Color,
    secondaryText: Color,
    iconBackground: Color,
    darkMode: Boolean
) {
    SettingRowBase(
        icon = Icons.Filled.Scale,
        title = LocalEcoStrings.current.settingsPreferredUnit,
        subtitle = LocalEcoStrings.current.settingsPreferredUnitSubtitle,
        primaryText = primaryText,
        secondaryText = secondaryText,
        iconBackground = iconBackground
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("kg", "pcs").forEach { unit ->
                Text(
                    text = unit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selectedUnit == unit) EcoGreen
                            else if (darkMode) Color(0xFF203522)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onUnitSelected(unit) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    color = if (selectedUnit == unit) Color.White else EcoGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LanguageSettingRow(
    selectedLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    primaryText: Color,
    secondaryText: Color,
    iconBackground: Color,
    darkMode: Boolean
) {
    val options = listOf(
        "en" to "EN",
        "ru" to "RU",
        "kk" to "KZ"
    )

    SettingRowBase(
        icon = Icons.Filled.Public,
        title = LocalEcoStrings.current.settingsLanguage,
        subtitle = LocalEcoStrings.current.settingsLanguageSubtitle,
        primaryText = primaryText,
        secondaryText = secondaryText,
        iconBackground = iconBackground
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            options.forEach { (tag, label) ->
                Text(
                    text = label,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selectedLanguageTag == tag) EcoGreen
                            else if (darkMode) Color(0xFF203522)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onLanguageSelected(tag) }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    color = if (selectedLanguageTag == tag) Color.White else EcoGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SettingRowBase(
    icon: ImageVector,
    title: String,
    subtitle: String,
    primaryText: Color,
    secondaryText: Color,
    iconBackground: Color,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EcoGreen,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = primaryText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = secondaryText,
                style = MaterialTheme.typography.bodySmall
            )
        }
        trailing()
    }
}

@Composable
private fun InfoCard(darkMode: Boolean) {
    val background = if (darkMode) Color(0xFF203522) else Color(0xFFE5F6EA)
    val textColor = if (darkMode) Color(0xFFB8D8BC) else Color(0xFF466D4B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(21.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = LocalEcoStrings.current.settingsInfo,
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SettingsScreenPreview() {
    EcoDalaTheme {
        SettingsScreen(
            uiState = AppSettings(),
            onBackClick = {},
            onPushNotificationsChange = {},
            onLocationChange = {},
            onAiTipsChange = {},
            onPublicProfileChange = {},
            onDarkModeChange = {},
            onPreferredUnitChange = {},
            onAppLanguageChange = {}
        )
    }
}

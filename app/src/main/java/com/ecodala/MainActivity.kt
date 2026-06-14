package com.ecodala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import com.ecodala.core.localization.EcoLocalization
import com.ecodala.core.navigation.EcoDalaApp
import com.ecodala.core.settings.AppSettingsStore
import com.ecodala.core.ui.theme.EcoDalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppSettingsStore.initialize(
            context = applicationContext,
            scope = lifecycleScope
        )

        setContent {
            val settings by AppSettingsStore.settings.collectAsState()

            LaunchedEffect(settings.appLanguageTag) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(settings.appLanguageTag)
                )
            }

            EcoLocalization(languageTag = settings.appLanguageTag) {
                EcoDalaTheme(darkTheme = settings.darkModeEnabled) {
                    val systemBarColor = MaterialTheme.colorScheme.background.toArgb()
                    val navigationBarColor = MaterialTheme.colorScheme.surface.toArgb()

                    SideEffect {
                        window.statusBarColor = systemBarColor
                        window.navigationBarColor = navigationBarColor
                        WindowCompat.getInsetsController(window, window.decorView).apply {
                            isAppearanceLightStatusBars = !settings.darkModeEnabled
                            isAppearanceLightNavigationBars = !settings.darkModeEnabled
                        }
                    }

                    EcoDalaApp()
                }
            }
        }
    }
}

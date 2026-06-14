package com.ecodala.feature.splash.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.navigation.EcoDalaRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel : ViewModel() {
    private val _nextRoute = MutableStateFlow(EcoDalaRoute.Login.route)
    val nextRoute: StateFlow<String> = _nextRoute
}

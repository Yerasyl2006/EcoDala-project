package com.ecodala.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.EcoUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _user = MutableStateFlow(DummyEcoData.currentUser)
    val user: StateFlow<EcoUser> = _user
}

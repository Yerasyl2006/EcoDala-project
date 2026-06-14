package com.ecodala.feature.challenges.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.Challenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChallengesViewModel : ViewModel() {
    private val _challenges = MutableStateFlow(DummyEcoData.challenges)
    val challenges: StateFlow<List<Challenge>> = _challenges
}

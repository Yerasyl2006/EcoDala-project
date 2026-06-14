package com.ecodala.feature.tree.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.VirtualTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VirtualTreeViewModel : ViewModel() {
    private val _tree = MutableStateFlow(DummyEcoData.tree)
    val tree: StateFlow<VirtualTree> = _tree
}

package com.ecodala.feature.scanner.presentation

import androidx.lifecycle.ViewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.ScannerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AiWasteScannerViewModel : ViewModel() {
    private val _scannerResult = MutableStateFlow<ScannerResult?>(DummyEcoData.scannerResult)
    val scannerResult: StateFlow<ScannerResult?> = _scannerResult
}

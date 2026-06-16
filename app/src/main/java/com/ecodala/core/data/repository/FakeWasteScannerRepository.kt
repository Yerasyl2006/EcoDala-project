package com.ecodala.core.data.repository

import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.repository.WasteScannerRepository
import kotlinx.coroutines.delay

class FakeWasteScannerRepository : WasteScannerRepository {
    override suspend fun scanWastePhoto(photoSource: String): Result<ScannerResult> {
        delay(900)
        return Result.success(DummyEcoData.scannerResult)
    }
}

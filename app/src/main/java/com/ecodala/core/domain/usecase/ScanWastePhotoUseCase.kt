package com.ecodala.core.domain.usecase

import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.repository.WasteScannerRepository

class ScanWastePhotoUseCase(
    private val repository: WasteScannerRepository
) {
    suspend operator fun invoke(photoSource: String): Result<ScannerResult> {
        return repository.scanWastePhoto(photoSource)
    }
}

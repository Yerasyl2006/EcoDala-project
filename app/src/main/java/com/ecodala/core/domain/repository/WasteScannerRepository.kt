package com.ecodala.core.domain.repository

import com.ecodala.core.domain.model.ScannerResult

interface WasteScannerRepository {
    suspend fun scanWastePhoto(photoSource: String): Result<ScannerResult>
}

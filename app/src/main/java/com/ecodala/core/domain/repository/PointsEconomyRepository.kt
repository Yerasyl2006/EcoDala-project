package com.ecodala.core.domain.repository

import com.ecodala.core.domain.model.PointsEvent
import com.ecodala.core.domain.model.WasteSubmission

interface PointsEconomyRepository {
    suspend fun getPointEvents(userId: String): Result<List<PointsEvent>>
    suspend fun getWasteSubmissions(userId: String): Result<List<WasteSubmission>>
}

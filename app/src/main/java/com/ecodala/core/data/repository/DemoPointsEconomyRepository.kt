package com.ecodala.core.data.repository

import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.PointsEvent
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.repository.PointsEconomyRepository

class DemoPointsEconomyRepository : PointsEconomyRepository {
    override suspend fun getPointEvents(userId: String): Result<List<PointsEvent>> {
        return Result.success(DummyEcoData.pointsLedger.filter { it.userId == userId })
    }

    override suspend fun getWasteSubmissions(userId: String): Result<List<WasteSubmission>> {
        return Result.success(DummyEcoData.wasteSubmissions.filter { it.userId == userId })
    }
}

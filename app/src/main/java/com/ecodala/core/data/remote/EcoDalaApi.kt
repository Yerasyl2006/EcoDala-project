package com.ecodala.core.data.remote

import com.ecodala.core.domain.model.RecyclingPoint
import retrofit2.http.GET

interface EcoDalaApi {
    @GET("recycling-points")
    suspend fun getRecyclingPoints(): List<RecyclingPoint>
}

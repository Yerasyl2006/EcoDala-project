package com.ecodala.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WaterStation
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.reflect.Type

private val Context.ecoCacheDataStore by preferencesDataStore(name = "eco_local_cache")

object EcoLocalCache {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private var appContext: Context? = null

    private val mapPointsKey = stringPreferencesKey("map_recycling_points")
    private val mapBiotoiletsKey = stringPreferencesKey("map_biotoilets")
    private val mapWaterStationsKey = stringPreferencesKey("map_water_stations")
    private val mapEcoReportsKey = stringPreferencesKey("map_eco_reports")
    private val historyKey = stringPreferencesKey("recycling_history")
    private val profileKey = stringPreferencesKey("profile_user")

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun saveMapData(
        points: List<RecyclingPoint>,
        biotoilets: List<Biotoilet>,
        waterStations: List<WaterStation>,
        ecoReports: List<EcoReport>
    ) {
        val context = appContext ?: return
        context.ecoCacheDataStore.edit { preferences ->
            preferences[mapPointsKey] = toJson(points, listType<RecyclingPoint>())
            preferences[mapBiotoiletsKey] = toJson(biotoilets, listType<Biotoilet>())
            preferences[mapWaterStationsKey] = toJson(waterStations, listType<WaterStation>())
            preferences[mapEcoReportsKey] = toJson(ecoReports, listType<EcoReport>())
        }
    }

    suspend fun getMapData(): CachedMapData? {
        val context = appContext ?: return null
        val preferences = context.safeCachePreferences()
        val points = fromJson<List<RecyclingPoint>>(preferences[mapPointsKey], listType<RecyclingPoint>())
        val biotoilets = fromJson<List<Biotoilet>>(preferences[mapBiotoiletsKey], listType<Biotoilet>())
        val waterStations = fromJson<List<WaterStation>>(preferences[mapWaterStationsKey], listType<WaterStation>())
        val ecoReports = fromJson<List<EcoReport>>(preferences[mapEcoReportsKey], listType<EcoReport>())

        return if (points != null || biotoilets != null || waterStations != null || ecoReports != null) {
            CachedMapData(
                points = points.orEmpty(),
                biotoilets = biotoilets.orEmpty(),
                waterStations = waterStations.orEmpty(),
                ecoReports = ecoReports.orEmpty()
            )
        } else {
            null
        }
    }

    suspend fun saveHistory(submissions: List<WasteSubmission>) {
        val context = appContext ?: return
        context.ecoCacheDataStore.edit { preferences ->
            preferences[historyKey] = toJson(submissions, listType<WasteSubmission>())
        }
    }

    suspend fun getHistory(): List<WasteSubmission>? {
        val context = appContext ?: return null
        return fromJson(context.safeCachePreferences()[historyKey], listType<WasteSubmission>())
    }

    suspend fun saveProfile(user: EcoUser) {
        val context = appContext ?: return
        context.ecoCacheDataStore.edit { preferences ->
            preferences[profileKey] = toJson(user, EcoUser::class.java)
        }
    }

    suspend fun getProfile(): EcoUser? {
        val context = appContext ?: return null
        return fromJson(context.safeCachePreferences()[profileKey], EcoUser::class.java)
    }

    private suspend fun Context.safeCachePreferences() = ecoCacheDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .first()

    private inline fun <reified T> listType(): Type = Types.newParameterizedType(List::class.java, T::class.java)

    private fun <T> toJson(value: T, type: Type): String = moshi.adapter<T>(type).toJson(value)

    private fun <T> fromJson(value: String?, type: Type): T? {
        return value?.let { runCatching { moshi.adapter<T>(type).fromJson(it) }.getOrNull() }
    }
}

data class CachedMapData(
    val points: List<RecyclingPoint>,
    val biotoilets: List<Biotoilet>,
    val waterStations: List<WaterStation>,
    val ecoReports: List<EcoReport>
)

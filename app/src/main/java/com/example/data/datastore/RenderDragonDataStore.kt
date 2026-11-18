package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "render_dragon_tuning")

data class RenderDragonSettings(
    val shaderQuality: Float = 90f,        // 0..100
    val distanceFog: Float = 0f,           // 0..100 (0 = Zero Fog for max FPS)
    val rayTracingIntensity: Float = 25f,  // 0..100
    val bloomIntensity: Float = 45f,       // 0..100
    val volumetricLighting: Boolean = false
)

class RenderDragonDataStore(private val context: Context) {
    companion object {
        val KEY_SHADER_QUALITY = floatPreferencesKey("shader_quality")
        val KEY_DISTANCE_FOG = floatPreferencesKey("distance_fog")
        val KEY_RAY_TRACING = floatPreferencesKey("ray_tracing_intensity")
        val KEY_BLOOM = floatPreferencesKey("bloom_intensity")
        val KEY_VOLUMETRIC = booleanPreferencesKey("volumetric_lighting")
    }

    val settingsFlow: Flow<RenderDragonSettings> = context.dataStore.data.map { prefs ->
        RenderDragonSettings(
            shaderQuality = prefs[KEY_SHADER_QUALITY] ?: 90f,
            distanceFog = prefs[KEY_DISTANCE_FOG] ?: 0f,
            rayTracingIntensity = prefs[KEY_RAY_TRACING] ?: 25f,
            bloomIntensity = prefs[KEY_BLOOM] ?: 45f,
            volumetricLighting = prefs[KEY_VOLUMETRIC] ?: false
        )
    }

    suspend fun setShaderQuality(value: Float) {
        context.dataStore.edit { it[KEY_SHADER_QUALITY] = value }
    }

    suspend fun setDistanceFog(value: Float) {
        context.dataStore.edit { it[KEY_DISTANCE_FOG] = value }
    }

    suspend fun setRayTracingIntensity(value: Float) {
        context.dataStore.edit { it[KEY_RAY_TRACING] = value }
    }

    suspend fun setBloomIntensity(value: Float) {
        context.dataStore.edit { it[KEY_BLOOM] = value }
    }

    suspend fun setVolumetricLighting(value: Boolean) {
        context.dataStore.edit { it[KEY_VOLUMETRIC] = value }
    }

    suspend fun applyPreset(preset: String) {
        context.dataStore.edit { prefs ->
            when (preset) {
                "2M_SPEED" -> {
                    prefs[KEY_SHADER_QUALITY] = 10f
                    prefs[KEY_DISTANCE_FOG] = 0f
                    prefs[KEY_RAY_TRACING] = 0f
                    prefs[KEY_BLOOM] = 0f
                    prefs[KEY_VOLUMETRIC] = false
                }
                "BALANCED" -> {
                    prefs[KEY_SHADER_QUALITY] = 60f
                    prefs[KEY_DISTANCE_FOG] = 20f
                    prefs[KEY_RAY_TRACING] = 25f
                    prefs[KEY_BLOOM] = 40f
                    prefs[KEY_VOLUMETRIC] = false
                }
                "RTX_ULTRA" -> {
                    prefs[KEY_SHADER_QUALITY] = 100f
                    prefs[KEY_DISTANCE_FOG] = 50f
                    prefs[KEY_RAY_TRACING] = 100f
                    prefs[KEY_BLOOM] = 85f
                    prefs[KEY_VOLUMETRIC] = true
                }
            }
        }
    }
}

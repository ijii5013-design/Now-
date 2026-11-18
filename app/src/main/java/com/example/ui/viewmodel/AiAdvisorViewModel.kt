package com.example.ui.viewmodel

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.RenderDragonDataStore
import com.example.data.datastore.RenderDragonSettings
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiAdvisorViewModel"

data class DeviceSpecs(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val hardware: String,
    val totalRamGb: Float,
    val availableRamGb: Float
) {
    fun summary(): String {
        return "Device: $manufacturer $model | SoC/Hardware: $hardware | Android $androidVersion (API $sdkInt) | RAM: ${String.format("%.1f", availableRamGb)}GB free of ${String.format("%.1f", totalRamGb)}GB total"
    }
}

data class AiTuningRecommendation(
    val shaderQuality: Float,
    val distanceFog: Float,
    val rayTracingIntensity: Float,
    val recommendedRamGb: Int,
    val recommendedFpsCap: Long,
    val antiAliasingMode: String,
    val profileName: String,
    val technicalAnalysis: String,
    val expectedFramerateBoost: String
)

data class AiGeneratedSeed(
    val seedNumber: String,
    val title: String,
    val biome: String,
    val spawnHighlights: String,
    val coordinates: String,
    val fpsAdvantageReason: String
)

sealed interface AiTuningUiState {
    data object Idle : AiTuningUiState
    data object Loading : AiTuningUiState
    data class Success(val recommendation: AiTuningRecommendation) : AiTuningUiState
    data class Error(val message: String) : AiTuningUiState
}

sealed interface AiSeedUiState {
    data object Idle : AiSeedUiState
    data object Loading : AiSeedUiState
    data class Success(val seeds: List<AiGeneratedSeed>) : AiSeedUiState
    data class Error(val message: String) : AiSeedUiState
}

sealed interface AiChatUiState {
    data object Idle : AiChatUiState
    data object Loading : AiChatUiState
    data class Streaming(val partialResponse: String) : AiChatUiState
    data class Success(val fullResponse: String) : AiChatUiState
    data class Error(val message: String) : AiChatUiState
}

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiAdvisorState(
    val specs: DeviceSpecs,
    val tuningState: AiTuningUiState = AiTuningUiState.Idle,
    val seedState: AiSeedUiState = AiSeedUiState.Idle,
    val chatState: AiChatUiState = AiChatUiState.Idle,
    val chatHistory: List<ChatMessage> = emptyList(),
    val appliedSuccessMessage: String? = null
)

class AiAdvisorViewModel(application: Application) : AndroidViewModel(application) {
    private val renderDragonDataStore = RenderDragonDataStore(application)

    private val _state = MutableStateFlow(
        AiAdvisorState(specs = collectDeviceSpecs(application))
    )
    val state: StateFlow<AiAdvisorState> = _state.asStateFlow()

    // 1. Structured Gemini Model for RenderDragon Hardware Optimization
    private val tuningSchema = Schema.obj(
        properties = mapOf(
            "profileName" to Schema.string(description = "Short futuristic gamer preset name e.g. Quantum 2M Voxel Turbo"),
            "shaderQuality" to Schema.double(description = "Float value between 0.0 (fastest/lowest) and 1.0 (extreme)"),
            "distanceFog" to Schema.double(description = "Float value between 0.0 (disabled/max distance view) and 1.0 (thick fog)"),
            "rayTracingIntensity" to Schema.double(description = "Float value between 0.0 (off) and 1.0 (full DXR/PBR path tracing)"),
            "recommendedRamGb" to Schema.integer(description = "Allocated memory between 4 and 16 GB"),
            "recommendedFpsCap" to Schema.integer(description = "Target FPS cap e.g. 2000000 or 1000000"),
            "antiAliasingMode" to Schema.string(description = "Recommended AA mode e.g. FXAA Zero Cost, MSAA 2x, or Disabled for 2M FPS"),
            "technicalAnalysis" to Schema.string(description = "Technical explanation of mobile GPU fillrate, RenderDragon shader pipeline, chunk meshing, and thermal culling for this hardware"),
            "expectedFramerateBoost" to Schema.string(description = "Estimated FPS improvement e.g. +340% FPS Stability")
        ),
        optionalProperties = emptyList()
    )

    private val tuningModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = tuningSchema
            }
        )
    }

    // 2. Structured Gemini Model for 2M FPS Speedrun Seed Generator
    private val seedSchema = Schema.obj(
        properties = mapOf(
            "seeds" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "seedNumber" to Schema.string(description = "Minecraft Bedrock seed numerical string e.g. -48291049281"),
                        "title" to Schema.string(description = "Seed title e.g. Quad Bastion Nether Fortress Overdrive"),
                        "biome" to Schema.string(description = "Spawn biome"),
                        "spawnHighlights" to Schema.string(description = "Structures within 200 blocks of spawn"),
                        "coordinates" to Schema.string(description = "Key coordinate landmarks e.g. Fortress: /tp 120 64 -80"),
                        "fpsAdvantageReason" to Schema.string(description = "Why this seed generates ultra high framerates (e.g. low cave air chunks, minimal mob entities, flat terrain meshing)")
                    ),
                    optionalProperties = emptyList()
                ),
                description = "List of curated high-framerate Minecraft Bedrock seeds"
            )
        )
    )

    private val seedModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = seedSchema
            }
        )
    }

    // 3. Conversational / Streaming Gemini Model for Bedrock Engineering Chat
    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    /**
     * Analyzes current mobile hardware and requests an optimal RenderDragon tuning config
     * from Gemini 3.8 Flash via Firebase AI Logic.
     */
    fun analyzeHardwareAndRecommendTuning(currentSettings: RenderDragonSettings) {
        viewModelScope.launch {
            _state.update { it.copy(tuningState = AiTuningUiState.Loading, appliedSuccessMessage = null) }
            try {
                val specs = _state.value.specs
                val prompt = """
                    You are the Lead Graphics Architect for Minecraft Bedrock Edition's RenderDragon Engine and 2,000,000 FPS Extreme Launcher.
                    Analyze this Android device hardware telemetry:
                    ${specs.summary()}
                    Current RenderDragon settings:
                    - Shader Quality: ${currentSettings.shaderQuality}
                    - Distance Fog: ${currentSettings.distanceFog}
                    - Ray-Tracing Intensity: ${currentSettings.rayTracingIntensity}
                    
                    Generate the optimal RenderDragon configuration to maximize frame rates, eliminate input latency, optimize tile-based deferred mobile GPU rasterization, and ensure peak performance up to 2,000,000 FPS.
                    Ensure shaderQuality, distanceFog, and rayTracingIntensity are clamped within 0.0 to 1.0.
                """.trimIndent()

                val response = withTimeout(60_000L) {
                    tuningModel.generateContent(prompt)
                }

                val jsonText = response.text
                if (!jsonText.isNullOrBlank()) {
                    val root = JSONObject(jsonText)
                    val rec = AiTuningRecommendation(
                        shaderQuality = root.getDouble("shaderQuality").toFloat().coerceIn(0f, 1f),
                        distanceFog = root.getDouble("distanceFog").toFloat().coerceIn(0f, 1f),
                        rayTracingIntensity = root.getDouble("rayTracingIntensity").toFloat().coerceIn(0f, 1f),
                        recommendedRamGb = root.getInt("recommendedRamGb").coerceIn(4, 16),
                        recommendedFpsCap = root.getLong("recommendedFpsCap"),
                        antiAliasingMode = root.getString("antiAliasingMode"),
                        profileName = root.getString("profileName"),
                        technicalAnalysis = root.getString("technicalAnalysis"),
                        expectedFramerateBoost = root.getString("expectedFramerateBoost")
                    )
                    _state.update { it.copy(tuningState = AiTuningUiState.Success(rec)) }
                } else {
                    _state.update { it.copy(tuningState = AiTuningUiState.Error("Gemini returned an empty response.")) }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Hardware tuning analysis timed out", e)
                _state.update { it.copy(tuningState = AiTuningUiState.Error("Analysis timed out after 60 seconds. Please check your network connection and retry.")) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Hardware tuning analysis failed", e)
                _state.update { it.copy(tuningState = AiTuningUiState.Error(e.localizedMessage ?: "AI Tuning generation failed.")) }
            }
        }
    }

    /**
     * Applies the Gemini recommendation directly to the DataStore.
     */
    fun applyRecommendation(recommendation: AiTuningRecommendation) {
        viewModelScope.launch {
            renderDragonDataStore.setShaderQuality(recommendation.shaderQuality)
            renderDragonDataStore.setDistanceFog(recommendation.distanceFog)
            renderDragonDataStore.setRayTracingIntensity(recommendation.rayTracingIntensity)
            _state.update {
                it.copy(
                    appliedSuccessMessage = "Applied '${recommendation.profileName}' directly to RenderDragon Engine!"
                )
            }
        }
    }

    fun dismissSuccessMessage() {
        _state.update { it.copy(appliedSuccessMessage = null) }
    }

    /**
     * Generates custom high-FPS Minecraft Bedrock speedrun seeds using Gemini.
     */
    fun generateHighFpsSeeds(userPreference: String = "Balanced Nether Speedrun") {
        viewModelScope.launch {
            _state.update { it.copy(seedState = AiSeedUiState.Loading) }
            try {
                val prompt = """
                    You are a world generation algorithm expert for Minecraft Bedrock (RenderDragon 1.21+).
                    Generate 3 unique, real or statistically optimal Bedrock world seeds tailored for ultra-high framerates (minimal chunk meshing overhead, low subterranean entity counts, open ocean or flat plains) with preference: '$userPreference'.
                    Provide coordinates for key structures (Nether Fortress, Stronghold, Trial Chamber, Village) and why the seed renders smoothly at extreme framerates.
                """.trimIndent()

                val response = withTimeout(60_000L) {
                    seedModel.generateContent(prompt)
                }

                val jsonText = response.text
                if (!jsonText.isNullOrBlank()) {
                    val root = JSONObject(jsonText)
                    val array = root.getJSONArray("seeds")
                    val list = mutableListOf<AiGeneratedSeed>()
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)
                        list.add(
                            AiGeneratedSeed(
                                seedNumber = item.getString("seedNumber"),
                                title = item.getString("title"),
                                biome = item.getString("biome"),
                                spawnHighlights = item.getString("spawnHighlights"),
                                coordinates = item.getString("coordinates"),
                                fpsAdvantageReason = item.getString("fpsAdvantageReason")
                            )
                        )
                    }
                    _state.update { it.copy(seedState = AiSeedUiState.Success(list)) }
                } else {
                    _state.update { it.copy(seedState = AiSeedUiState.Error("Gemini returned an empty seed list.")) }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Seed generation timed out", e)
                _state.update { it.copy(seedState = AiSeedUiState.Error("Seed generation timed out. Please retry.")) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Seed generation failed", e)
                _state.update { it.copy(seedState = AiSeedUiState.Error(e.localizedMessage ?: "Seed generation failed.")) }
            }
        }
    }

    /**
     * Ask the AI Bedrock Architect questions with streaming responses.
     */
    fun sendChatMessage(query: String) {
        if (query.isBlank()) return
        val userMsg = ChatMessage(role = "user", content = query.trim())
        val updatedHistory = _state.value.chatHistory + userMsg

        _state.update {
            it.copy(
                chatHistory = updatedHistory,
                chatState = AiChatUiState.Loading
            )
        }

        viewModelScope.launch {
            val specs = _state.value.specs
            val systemContext = "You are the Bedrock 2,000,000 FPS AI Architect & RenderDragon Tuning Specialist. Answer accurately, concisely, and with technical gaming depth. User device: ${specs.summary()}.\nUser query: $query"
            val accumulated = StringBuilder()

            try {
                withTimeout(60_000L) {
                    chatModel.generateContentStream(systemContext).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulated.append(chunkText)
                        _state.update {
                            it.copy(chatState = AiChatUiState.Streaming(accumulated.toString()))
                        }
                    }
                }

                val finalOutput = accumulated.toString()
                if (finalOutput.isNotBlank()) {
                    val assistantMsg = ChatMessage(role = "assistant", content = finalOutput)
                    _state.update {
                        it.copy(
                            chatHistory = it.chatHistory + assistantMsg,
                            chatState = AiChatUiState.Success(finalOutput)
                        )
                    }
                } else {
                    _state.update {
                        it.copy(chatState = AiChatUiState.Error("No response received from AI model."))
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chat streaming timed out", e)
                _state.update { it.copy(chatState = AiChatUiState.Error("Response timed out after 60 seconds.")) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chat streaming failed", e)
                _state.update { it.copy(chatState = AiChatUiState.Error(e.localizedMessage ?: "AI response failed.")) }
            }
        }
    }

    private companion object {
        fun collectDeviceSpecs(context: Context): DeviceSpecs {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)

            val totalGb = memInfo.totalMem / (1024f * 1024f * 1024f)
            val availGb = memInfo.availMem / (1024f * 1024f * 1024f)

            return DeviceSpecs(
                manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
                model = Build.MODEL,
                androidVersion = Build.VERSION.RELEASE,
                sdkInt = Build.VERSION.SDK_INT,
                hardware = Build.HARDWARE,
                totalRamGb = totalGb,
                availableRamGb = availGb
            )
        }
    }
}

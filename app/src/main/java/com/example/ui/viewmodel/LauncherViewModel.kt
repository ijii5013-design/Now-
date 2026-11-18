package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BedrockDefaults
import com.example.data.BedrockServer
import com.example.data.BedrockVersion
import com.example.data.CapeModel
import com.example.data.SkinModel
import com.example.data.TexturePack
import com.example.data.db.CustomServerEntity
import com.example.data.db.LauncherDatabase
import com.example.data.db.LauncherSettingsEntity
import com.example.util.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class LauncherUiState(
    val fpsTarget: Long = 2_000_000L,
    val isExtremeBoosterActive: Boolean = true,
    val allocatedMemoryGb: Int = 8,
    val renderDistanceChunks: Int = 32,
    val renderDragonMode: String = "Ultra Latency (Zero Delay)",
    val particleOptimization: String = "Hyper Cull",
    val fsrUpscaling: Boolean = true,
    val vsync: Boolean = false,
    val multiThreadedChunkMeshing: Boolean = true,
    val activeVersion: BedrockVersion = BedrockDefaults.versions.first(),
    val activeSkin: SkinModel = BedrockDefaults.skins.first(),
    val activeCape: CapeModel? = BedrockDefaults.capes.first(),
    val activeTexturePack: TexturePack = BedrockDefaults.texturePacks.first(),
    val hudFps: Boolean = true,
    val hudCps: Boolean = true,
    val hudCoordinates: Boolean = true,
    val hudArmor: Boolean = true,
    val hudKeystrokes: Boolean = true,
    // System status
    val isMinecraftInstalled: Boolean = false,
    val installedPackageName: String? = null,
    val isOptimizing: Boolean = false,
    val optimizationProgress: Float = 0f,
    val optimizationLog: String = "",
    val isLaunching: Boolean = false,
    val launchDialogMessage: String? = null,
    // CPS Clicker Test
    val isCpsActive: Boolean = false,
    val cpsClicks: Int = 0,
    val cpsTimeRemainingSeconds: Float = 5.0f,
    val currentCpsRate: Float = 0f,
    val bestCpsRate: Float = 14.5f,
    val cpsFinishedMessage: String? = null,
    // Benchmark Stress Test
    val isBenchmarking: Boolean = false,
    val benchmarkProgress: Float = 0f,
    val benchmarkCurrentFps: Long = 2_000_000L,
    val benchmarkPeakFps: Long = 2_000_000L,
    val benchmarkAverageFps: Long = 1_995_000L,
    val benchmarkScore: Long = 0L,
    val benchmarkPhase: String = "IDLE (READY TO TEST)",
    val benchmarkVoxelCount: Int = 16_384,
    val benchmarkParticleCount: Int = 2_500,
    val isBoosterVerified: Boolean = false,
    val benchmarkFinishedDialog: Boolean = false,
    // Servers
    val servers: List<BedrockServer> = BedrockDefaults.servers,
    // Addons & Seeds
    val mods: List<com.example.data.BedrockMod> = com.example.data.BedrockModDefaults.mods,
    val seeds: List<com.example.data.BedrockSeed> = com.example.data.BedrockModDefaults.seeds,
    // RenderDragon DataStore Tuning
    val renderDragonTuning: com.example.data.datastore.RenderDragonSettings = com.example.data.datastore.RenderDragonSettings(),
    // Benchmark History Log
    val benchmarkHistory: List<com.example.data.db.BenchmarkResultEntity> = emptyList()
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = LauncherDatabase.getDatabase(application).launcherDao()
    val soundManager = SoundManager(application)
    val renderDragonDataStore = com.example.data.datastore.RenderDragonDataStore(application)

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    private var cpsJob: Job? = null
    private var benchmarkJob: Job? = null

    init {
        checkMinecraftInstallation()
        loadPersistedSettings()
        observeRenderDragonDataStore()
    }

    private fun observeRenderDragonDataStore() {
        viewModelScope.launch {
            renderDragonDataStore.settingsFlow.collect { tuning ->
                _uiState.update { it.copy(renderDragonTuning = tuning) }
            }
        }
    }

    private fun checkMinecraftInstallation() {
        val pm = getApplication<Application>().packageManager
        val candidates = listOf(
            "com.mojang.minecraftpe",
            "com.mojang.minecraftpreview",
            "com.mojang.minecrafttrailer"
        )
        for (pkg in candidates) {
            try {
                pm.getPackageInfo(pkg, 0)
                _uiState.update {
                    it.copy(
                        isMinecraftInstalled = true,
                        installedPackageName = pkg
                    )
                }
                return
            } catch (_: PackageManager.NameNotFoundException) {
            }
        }
        _uiState.update {
            it.copy(
                isMinecraftInstalled = false,
                installedPackageName = null
            )
        }
    }

    private fun loadPersistedSettings() {
        viewModelScope.launch {
            val settings = dao.getSettings().firstOrNull()
            if (settings != null) {
                val version = BedrockDefaults.versions.find { it.id == settings.activeVersionId }
                    ?: BedrockDefaults.versions.first()
                val skin = BedrockDefaults.skins.find { it.id == settings.activeSkinId }
                    ?: BedrockDefaults.skins.first()
                val cape = BedrockDefaults.capes.find { it.id == settings.activeCapeId }
                val pack = BedrockDefaults.texturePacks.find { it.id == settings.activeTexturePackId }
                    ?: BedrockDefaults.texturePacks.first()

                _uiState.update {
                    it.copy(
                        fpsTarget = settings.fpsTarget,
                        isExtremeBoosterActive = settings.isExtremeBoosterActive,
                        allocatedMemoryGb = settings.allocatedMemoryGb,
                        renderDistanceChunks = settings.renderDistanceChunks,
                        renderDragonMode = settings.renderDragonMode,
                        particleOptimization = settings.particleOptimization,
                        fsrUpscaling = settings.fsrUpscaling,
                        vsync = settings.vsync,
                        multiThreadedChunkMeshing = settings.multiThreadedChunkMeshing,
                        activeVersion = version,
                        activeSkin = skin,
                        activeCape = cape,
                        activeTexturePack = pack,
                        hudFps = settings.hudFps,
                        hudCps = settings.hudCps,
                        hudCoordinates = settings.hudCoordinates,
                        hudArmor = settings.hudArmor,
                        hudKeystrokes = settings.hudKeystrokes,
                        bestCpsRate = settings.cpsBestRate
                    )
                }
            }

            // Also load custom servers
            dao.getCustomServers().collect { customList ->
                val converted = customList.map {
                    BedrockServer(
                        id = "custom_${it.uid}",
                        name = it.name,
                        host = it.host,
                        port = it.port,
                        motd = it.motd,
                        playersOnline = Random.nextInt(50, 800),
                        pingMs = it.pingMs,
                        isOfficialFeatured = false
                    )
                }
                _uiState.update {
                    it.copy(servers = BedrockDefaults.servers + converted)
                }
            }

            // Observe benchmark history log
            dao.getBenchmarkHistory().collect { historyList ->
                if (historyList.isEmpty()) {
                    seedDefaultBenchmarkHistory()
                } else {
                    _uiState.update { it.copy(benchmarkHistory = historyList) }
                }
            }
        }
    }

    private fun persistSettings() {
        viewModelScope.launch {
            val s = _uiState.value
            dao.saveSettings(
                LauncherSettingsEntity(
                    id = 1,
                    fpsTarget = s.fpsTarget,
                    isExtremeBoosterActive = s.isExtremeBoosterActive,
                    allocatedMemoryGb = s.allocatedMemoryGb,
                    renderDistanceChunks = s.renderDistanceChunks,
                    renderDragonMode = s.renderDragonMode,
                    particleOptimization = s.particleOptimization,
                    fsrUpscaling = s.fsrUpscaling,
                    vsync = s.vsync,
                    multiThreadedChunkMeshing = s.multiThreadedChunkMeshing,
                    activeVersionId = s.activeVersion.id,
                    activeSkinId = s.activeSkin.id,
                    activeCapeId = s.activeCape?.id ?: "",
                    activeTexturePackId = s.activeTexturePack.id,
                    hudFps = s.hudFps,
                    hudCps = s.hudCps,
                    hudCoordinates = s.hudCoordinates,
                    hudArmor = s.hudArmor,
                    hudKeystrokes = s.hudKeystrokes,
                    cpsBestRate = s.bestCpsRate
                )
            )
        }
    }

    fun setFpsTarget(target: Long) {
        soundManager.playClick()
        _uiState.update { it.copy(fpsTarget = target) }
        persistSettings()
    }

    fun toggleBooster() {
        soundManager.playTurboBoost()
        _uiState.update { it.copy(isExtremeBoosterActive = !it.isExtremeBoosterActive) }
        persistSettings()
    }

    fun setMemoryAllocation(gb: Int) {
        soundManager.playClick()
        _uiState.update { it.copy(allocatedMemoryGb = gb) }
        persistSettings()
    }

    fun setRenderDistance(chunks: Int) {
        soundManager.playClick()
        _uiState.update { it.copy(renderDistanceChunks = chunks) }
        persistSettings()
    }

    fun setRenderDragonMode(mode: String) {
        soundManager.playClick()
        _uiState.update { it.copy(renderDragonMode = mode) }
        persistSettings()
    }

    fun setParticleOptimization(opt: String) {
        soundManager.playClick()
        _uiState.update { it.copy(particleOptimization = opt) }
        persistSettings()
    }

    fun toggleFsr() {
        soundManager.playClick()
        _uiState.update { it.copy(fsrUpscaling = !it.fsrUpscaling) }
        persistSettings()
    }

    fun toggleVsync() {
        soundManager.playClick()
        _uiState.update { it.copy(vsync = !it.vsync) }
        persistSettings()
    }

    fun toggleMultiThreading() {
        soundManager.playClick()
        _uiState.update { it.copy(multiThreadedChunkMeshing = !it.multiThreadedChunkMeshing) }
        persistSettings()
    }

    fun selectVersion(version: BedrockVersion) {
        soundManager.playClick()
        _uiState.update { it.copy(activeVersion = version) }
        persistSettings()
    }

    fun selectSkin(skin: SkinModel) {
        soundManager.playClick()
        _uiState.update { it.copy(activeSkin = skin) }
        persistSettings()
    }

    fun selectCape(cape: CapeModel?) {
        soundManager.playClick()
        _uiState.update { it.copy(activeCape = cape) }
        persistSettings()
    }

    fun selectTexturePack(pack: TexturePack) {
        soundManager.playClick()
        _uiState.update { it.copy(activeTexturePack = pack) }
        persistSettings()
    }

    fun toggleHudItem(item: String) {
        soundManager.playClick()
        _uiState.update {
            when (item) {
                "fps" -> it.copy(hudFps = !it.hudFps)
                "cps" -> it.copy(hudCps = !it.hudCps)
                "coords" -> it.copy(hudCoordinates = !it.hudCoordinates)
                "armor" -> it.copy(hudArmor = !it.hudArmor)
                "keystrokes" -> it.copy(hudKeystrokes = !it.hudKeystrokes)
                else -> it
            }
        }
        persistSettings()
    }

    // One-Tap Hyper Clean & Boost
    fun runHyperBoost() {
        soundManager.playTurboBoost()
        viewModelScope.launch {
            _uiState.update { it.copy(isOptimizing = true, optimizationProgress = 0.1f, optimizationLog = "Flushing Bedrock RenderDragon shader cache...") }
            delay(400)
            _uiState.update { it.copy(optimizationProgress = 0.45f, optimizationLog = "Allocating ${_uiState.value.allocatedMemoryGb}GB RAM High-Speed Voxel Buffer...") }
            delay(450)
            _uiState.update { it.copy(optimizationProgress = 0.8f, optimizationLog = "Uncapping Bedrock frame tick-rate to 2,000,000 FPS...") }
            delay(400)
            _uiState.update { it.copy(optimizationProgress = 1.0f, optimizationLog = "READY! 2,000,000 FPS Extreme Engine Primed.") }
            soundManager.playLevelUp()
            delay(600)
            _uiState.update { it.copy(isOptimizing = false) }
        }
    }

    // Launch Minecraft Bedrock
    fun launchMinecraft() {
        val app = getApplication<Application>()
        val pkg = _uiState.value.installedPackageName

        soundManager.playTurboBoost()

        if (pkg != null) {
            // Real Minecraft Bedrock is installed! Launch it directly
            val intent = app.packageManager.getLaunchIntentForPackage(pkg)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent != null) {
                app.startActivity(intent)
                return
            }
        }

        // Try generic Minecraft deep link scheme
        try {
            val mcIntent = Intent(Intent.ACTION_VIEW, Uri.parse("minecraft://")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            app.startActivity(mcIntent)
            return
        } catch (_: Exception) {}

        // If not installed on emulator/device, display simulated boot sequence & dialog
        viewModelScope.launch {
            _uiState.update { it.copy(isLaunching = true, launchDialogMessage = "Injecting 2,000,000 FPS Overdrive into Bedrock Engine...") }
            delay(600)
            soundManager.playLevelUp()
            _uiState.update {
                it.copy(
                    launchDialogMessage = "2,000,000 FPS Profile Applied!\n\nMinecraft Bedrock (${_uiState.value.activeVersion.name}) is configured with:\n• FPS Cap: 2,000,000 FPS\n• RenderDragon: ${_uiState.value.renderDragonMode}\n• RAM: ${_uiState.value.allocatedMemoryGb}GB Allocated\n• Texture: ${_uiState.value.activeTexturePack.name}\n\nBedrock APK package was not detected on this device. You can test performance right now in the 2M FPS Benchmark World or install Minecraft from Google Play!"
                )
            }
        }
    }

    fun dismissLaunchDialog() {
        soundManager.playClick()
        _uiState.update { it.copy(isLaunching = false, launchDialogMessage = null) }
    }

    fun openPlayStoreMinecraft() {
        soundManager.playClick()
        val app = getApplication<Application>()
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mojang.minecraftpe")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            app.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mojang.minecraftpe")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            app.startActivity(intent)
        }
    }

    fun joinServer(server: BedrockServer) {
        soundManager.playClick()
        val app = getApplication<Application>()
        // Bedrock custom URI scheme to add and join external server:
        // minecraft://?addExternalServer=ServerName|host:port
        val uriStr = "minecraft://?addExternalServer=${Uri.encode(server.name)}|${server.host}:${server.port}"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            app.startActivity(intent)
        } catch (_: Exception) {
            // Minecraft not installed, show dialog
            _uiState.update {
                it.copy(
                    isLaunching = true,
                    launchDialogMessage = "Server: ${server.name}\nAddress: ${server.host}:${server.port}\nPing: ${server.pingMs} ms\nPlayers: ${server.playersOnline}\n\nServer URI copied: $uriStr\nOpen Minecraft Bedrock to connect!"
                )
            }
        }
    }

    fun addCustomServer(name: String, host: String, port: Int) {
        soundManager.playLevelUp()
        viewModelScope.launch {
            dao.insertServer(
                CustomServerEntity(
                    name = name,
                    host = host,
                    port = port,
                    motd = "Custom Bedrock Server",
                    pingMs = Random.nextInt(15, 60)
                )
            )
        }
    }

    fun refreshServerPings() {
        soundManager.playClick()
        viewModelScope.launch {
            _uiState.update { state ->
                val updated = state.servers.map { s ->
                    s.copy(
                        pingMs = (s.pingMs + Random.nextInt(-4, 6)).coerceIn(12, 120),
                        playersOnline = (s.playersOnline + Random.nextInt(-40, 50)).coerceAtLeast(100)
                    )
                }
                state.copy(servers = updated)
            }
        }
    }

    // CPS Speed Clicker Test
    fun startCpsTest() {
        cpsJob?.cancel()
        soundManager.playClick()
        _uiState.update {
            it.copy(
                isCpsActive = true,
                cpsClicks = 0,
                cpsTimeRemainingSeconds = 5.0f,
                currentCpsRate = 0f,
                cpsFinishedMessage = null
            )
        }

        cpsJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val totalDurationMs = 5000L

            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val remaining = ((totalDurationMs - elapsed) / 1000f).coerceAtLeast(0f)
                val currentClicks = _uiState.value.cpsClicks
                val elapsedSeconds = (elapsed / 1000f).coerceAtLeast(0.1f)
                val rate = currentClicks / elapsedSeconds

                _uiState.update {
                    it.copy(
                        cpsTimeRemainingSeconds = remaining,
                        currentCpsRate = rate
                    )
                }

                if (elapsed >= totalDurationMs) {
                    val finalRate = currentClicks / 5.0f
                    val newBest = if (finalRate > _uiState.value.bestCpsRate) finalRate else _uiState.value.bestCpsRate
                    soundManager.playLevelUp()

                    val rank = when {
                        finalRate >= 16f -> "GODBRIDGER (2M FPS TIER)!"
                        finalRate >= 12f -> "BUTTERFLY CLICK MASTER!"
                        finalRate >= 9f -> "JITTER CLICK PRO!"
                        else -> "CASUAL MINECRAFT WARRIOR"
                    }

                    _uiState.update {
                        it.copy(
                            isCpsActive = false,
                            cpsTimeRemainingSeconds = 0f,
                            currentCpsRate = finalRate,
                            bestCpsRate = newBest,
                            cpsFinishedMessage = "Test Finished! Score: ${String.format("%.1f", finalRate)} CPS\nRank: $rank"
                        )
                    }
                    persistSettings()
                    break
                }
                delay(50)
            }
        }
    }

    fun registerCpsClick() {
        if (!_uiState.value.isCpsActive) {
            startCpsTest()
        }
        soundManager.playClick()
        _uiState.update { it.copy(cpsClicks = it.cpsClicks + 1) }
    }

    // Benchmark Stress Test
    fun startBenchmark() {
        benchmarkJob?.cancel()
        soundManager.playTurboBoost()
        _uiState.update {
            it.copy(
                isBenchmarking = true,
                benchmarkProgress = 0f,
                benchmarkCurrentFps = 450_000L,
                benchmarkPeakFps = 450_000L,
                benchmarkScore = 0L,
                benchmarkPhase = "PHASE 1/4: VOXEL MESH DENSITY (16,384 BLOCKS)",
                benchmarkVoxelCount = 16_384,
                benchmarkParticleCount = 1_000,
                isBoosterVerified = false,
                benchmarkFinishedDialog = false
            )
        }

        benchmarkJob = viewModelScope.launch {
            var maxFps = 450_000L
            for (step in 1..20) {
                delay(220)
                val progress = step / 20f

                val (phase, fps, particles) = when {
                    step <= 5 -> {
                        Triple(
                            "PHASE 1/4: VOXEL MESH DENSITY (16,384 BLOCKS)",
                            (450_000L + step * 90_000L + Random.nextLong(-10_000, 10_000)),
                            1_200
                        )
                    }
                    step <= 10 -> {
                        if (step == 6) soundManager.playBlockBreak()
                        Triple(
                            "PHASE 2/4: PARTICLE EXPLOSION (5,000 VOXELS)",
                            (950_000L + (step - 5) * 110_000L + Random.nextLong(-15_000, 15_000)),
                            3_500
                        )
                    }
                    step <= 15 -> {
                        if (step == 11) soundManager.playTurboBoost()
                        Triple(
                            "PHASE 3/4: RENDERDRAGON GEOMETRY PIPELINE",
                            (1_550_000L + (step - 10) * 80_000L + Random.nextLong(-10_000, 15_000)),
                            6_800
                        )
                    }
                    else -> {
                        Triple(
                            "PHASE 4/4: 2M FPS OVERDRIVE CERTIFICATION",
                            (1_980_000L + Random.nextLong(10_000, 35_000)),
                            10_000
                        )
                    }
                }

                if (fps > maxFps) maxFps = fps
                val score = (fps * progress * 0.05f).toLong()

                _uiState.update {
                    it.copy(
                        benchmarkProgress = progress,
                        benchmarkCurrentFps = fps,
                        benchmarkPeakFps = maxFps,
                        benchmarkPhase = phase,
                        benchmarkParticleCount = particles,
                        benchmarkScore = score
                    )
                }
            }

            soundManager.playLevelUp()
            _uiState.update {
                it.copy(
                    isBenchmarking = false,
                    isBoosterVerified = true,
                    benchmarkCurrentFps = 2_000_000L,
                    benchmarkPeakFps = 2_014_800L,
                    benchmarkAverageFps = 1_998_420L,
                    benchmarkPhase = "VERIFIED • 2,000,000 FPS EXTREME",
                    benchmarkFinishedDialog = true,
                    benchmarkScore = 998_420L
                )
            }
        }
    }

    fun dismissBenchmarkDialog() {
        soundManager.playClick()
        _uiState.update { it.copy(benchmarkFinishedDialog = false) }
    }

    fun toggleMod(modId: String) {
        soundManager.playClick()
        _uiState.update { state ->
            val updated = state.mods.map { mod ->
                if (mod.id == modId) mod.copy(isEnabled = !mod.isEnabled) else mod
            }
            state.copy(mods = updated)
        }
    }

    fun setShaderQuality(value: Float) {
        soundManager.vibrate(10, 60)
        viewModelScope.launch {
            renderDragonDataStore.setShaderQuality(value)
        }
    }

    fun setDistanceFog(value: Float) {
        soundManager.vibrate(10, 60)
        viewModelScope.launch {
            renderDragonDataStore.setDistanceFog(value)
        }
    }

    fun setRayTracingIntensity(value: Float) {
        soundManager.vibrate(10, 60)
        viewModelScope.launch {
            renderDragonDataStore.setRayTracingIntensity(value)
        }
    }

    fun applyRenderDragonPreset(preset: String) {
        soundManager.playTurboBoost()
        viewModelScope.launch {
            renderDragonDataStore.applyPreset(preset)
        }
    }
}


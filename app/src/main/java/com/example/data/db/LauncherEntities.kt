package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher_settings")
data class LauncherSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val fpsTarget: Long = 2_000_000L,
    val isExtremeBoosterActive: Boolean = true,
    val allocatedMemoryGb: Int = 8,
    val renderDistanceChunks: Int = 32,
    val renderDragonMode: String = "Ultra Latency (Zero Delay)",
    val particleOptimization: String = "Hyper Cull",
    val fsrUpscaling: Boolean = true,
    val vsync: Boolean = false,
    val multiThreadedChunkMeshing: Boolean = true,
    val activeVersionId: String = "1.21.50",
    val activeSkinId: String = "cyber_creeper",
    val activeCapeId: String = "turbo_elytra_2m",
    val activeTexturePackId: String = "potato_2m",
    val hudFps: Boolean = true,
    val hudCps: Boolean = true,
    val hudCoordinates: Boolean = true,
    val hudArmor: Boolean = true,
    val hudKeystrokes: Boolean = true,
    val cpsBestRate: Float = 14.5f
)

@Entity(tableName = "custom_servers")
data class CustomServerEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val name: String,
    val host: String,
    val port: Int = 19132,
    val motd: String = "Custom Bedrock Server",
    val pingMs: Int = 25
)

@Entity(tableName = "benchmark_history")
data class BenchmarkResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String,
    val peakFps: Long,
    val averageFps: Long,
    val frameTimeMs: Float,
    val score: Long,
    val voxelCount: Int,
    val particleCount: Int,
    val meshType: String,
    val boosterMode: String,
    val rating: String = "ULTRA CERTIFIED"
)


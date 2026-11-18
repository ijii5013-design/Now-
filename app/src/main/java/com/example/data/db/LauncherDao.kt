package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {
    @Query("SELECT * FROM launcher_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<LauncherSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: LauncherSettingsEntity)

    @Query("SELECT * FROM custom_servers ORDER BY uid DESC")
    fun getCustomServers(): Flow<List<CustomServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: CustomServerEntity)

    @Query("DELETE FROM custom_servers WHERE uid = :uid")
    suspend fun deleteServer(uid: Long)

    @Query("SELECT * FROM benchmark_history ORDER BY timestamp DESC")
    fun getBenchmarkHistory(): Flow<List<BenchmarkResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenchmarkResult(result: BenchmarkResultEntity): Long

    @Query("DELETE FROM benchmark_history WHERE id = :id")
    suspend fun deleteBenchmarkResult(id: Long)

    @Query("DELETE FROM benchmark_history")
    suspend fun clearBenchmarkHistory()
}

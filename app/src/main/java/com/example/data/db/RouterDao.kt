package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DeviceNicknameEntity
import com.example.data.model.RouterEntity
import com.example.data.model.SpeedTestHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouterDao {
    // Routers
    @Query("SELECT * FROM saved_routers ORDER BY isPinned DESC, lastAccessedTime DESC")
    fun getAllRouters(): Flow<List<RouterEntity>>

    @Query("SELECT * FROM saved_routers WHERE isRemoteBound = 1 ORDER BY lastAccessedTime DESC")
    fun getBoundRouters(): Flow<List<RouterEntity>>

    @Query("SELECT * FROM saved_routers WHERE id = :id")
    suspend fun getRouterById(id: Long): RouterEntity?

    @Query("SELECT * FROM saved_routers WHERE ipOrHostname = :ip LIMIT 1")
    suspend fun getRouterByIp(ip: String): RouterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouter(router: RouterEntity): Long

    @Update
    suspend fun updateRouter(router: RouterEntity)

    @Delete
    suspend fun deleteRouter(router: RouterEntity)

    @Query("DELETE FROM saved_routers WHERE id = :id")
    suspend fun deleteRouterById(id: Long)

    // Devices
    @Query("SELECT * FROM device_nicknames")
    fun getAllDeviceNicknames(): Flow<List<DeviceNicknameEntity>>

    @Query("SELECT * FROM device_nicknames WHERE macAddress = :mac LIMIT 1")
    suspend fun getDeviceByMac(mac: String): DeviceNicknameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDeviceNickname(device: DeviceNicknameEntity)

    @Delete
    suspend fun deleteDeviceNickname(device: DeviceNicknameEntity)

    // Speed Test
    @Query("SELECT * FROM speed_test_history ORDER BY timestamp DESC LIMIT 20")
    fun getSpeedHistory(): Flow<List<SpeedTestHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeedTest(speed: SpeedTestHistoryEntity)

    @Query("DELETE FROM speed_test_history")
    suspend fun clearSpeedHistory()
}

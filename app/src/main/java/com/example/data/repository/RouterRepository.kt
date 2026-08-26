package com.example.data.repository

import com.example.data.db.RouterDao
import com.example.data.model.DeviceNicknameEntity
import com.example.data.model.RouterEntity
import com.example.data.model.SpeedTestHistoryEntity
import kotlinx.coroutines.flow.Flow

class RouterRepository(private val dao: RouterDao) {
    val allRouters: Flow<List<RouterEntity>> = dao.getAllRouters()
    val boundRouters: Flow<List<RouterEntity>> = dao.getBoundRouters()
    val allDeviceNicknames: Flow<List<DeviceNicknameEntity>> = dao.getAllDeviceNicknames()
    val speedHistory: Flow<List<SpeedTestHistoryEntity>> = dao.getSpeedHistory()

    suspend fun getRouterById(id: Long): RouterEntity? = dao.getRouterById(id)
    suspend fun getRouterByIp(ip: String): RouterEntity? = dao.getRouterByIp(ip)
    suspend fun saveRouter(router: RouterEntity): Long = dao.insertRouter(router)
    suspend fun updateRouter(router: RouterEntity) = dao.updateRouter(router)
    suspend fun deleteRouter(router: RouterEntity) = dao.deleteRouter(router)
    suspend fun deleteRouterById(id: Long) = dao.deleteRouterById(id)

    suspend fun getDeviceByMac(mac: String): DeviceNicknameEntity? = dao.getDeviceByMac(mac)
    suspend fun saveDeviceNickname(device: DeviceNicknameEntity) = dao.saveDeviceNickname(device)
    suspend fun deleteDeviceNickname(device: DeviceNicknameEntity) = dao.deleteDeviceNickname(device)

    suspend fun insertSpeedTest(speed: SpeedTestHistoryEntity) = dao.insertSpeedTest(speed)
    suspend fun clearSpeedHistory() = dao.clearSpeedHistory()
}

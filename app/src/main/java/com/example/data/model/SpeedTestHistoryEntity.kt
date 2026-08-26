package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speed_test_history")
data class SpeedTestHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val pingMs: Long,
    val jitterMs: Long,
    val downloadMbps: Double,
    val ssid: String,
    val gatewayIp: String
)

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_nicknames")
data class DeviceNicknameEntity(
    @PrimaryKey
    val macAddress: String,
    val ipAddress: String = "",
    val customName: String,
    val deviceType: String = "Phone", // Phone, Laptop, Desktop, TV, Camera, Console, SmartHome, Other
    val isBlocked: Boolean = false,
    val vendor: String = "",
    val notes: String = "",
    val firstSeen: Long = System.currentTimeMillis()
)

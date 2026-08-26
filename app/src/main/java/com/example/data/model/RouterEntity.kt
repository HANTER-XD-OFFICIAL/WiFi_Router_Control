package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routers")
data class RouterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String, // TP-Link, Tenda, Netgear, Xiaomi, D-Link, MikroTik, Huawei, Asus, Mercusys, ZTE, Other
    val ipOrHostname: String, // e.g. 192.168.0.1 or myhome.tplinkdns.com
    val port: Int = 80,
    val username: String = "admin",
    val password: String = "admin",
    val isRemoteBound: Boolean = false, // True if bound for remote/cloud access
    val remoteDnsUrl: String = "", // e.g. https://myrouter.ddns.net:8443 or cloud proxy
    val locationTag: String = "Home", // Home, Office, Shop, Branch, Other
    val macAddress: String = "",
    val notes: String = "",
    val lastAccessedTime: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val protocol: String = "http" // http or https
)

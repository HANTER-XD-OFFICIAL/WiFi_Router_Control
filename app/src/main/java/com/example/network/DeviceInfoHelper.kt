package com.example.network

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import java.util.Locale

data class HostDeviceInfo(
    val deviceName: String,
    val manufacturer: String,
    val model: String,
    val brand: String,
    val androidVersion: String,
    val apiLevel: Int,
    val codeName: String,
    val buildId: String,
    val hardware: String,
    val board: String,
    val cpuAbi: String,
    val totalRamGb: Double,
    val availableRamGb: Double,
    val ramUsagePercent: Int,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val securityPatch: String,
    val uptimeFormatted: String
)

object DeviceInfoHelper {

    fun getHostDeviceInfo(context: Context): HostDeviceInfo {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val model = Build.MODEL
        val brand = Build.BRAND.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val deviceName = if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else {
            "$manufacturer $model"
        }

        val androidVersion = "Android ${Build.VERSION.RELEASE}"
        val apiLevel = Build.VERSION.SDK_INT
        val codeName = getAndroidVersionCodeName(apiLevel)
        val buildId = Build.DISPLAY ?: Build.ID
        val hardware = Build.HARDWARE.uppercase(Locale.ROOT)
        val board = Build.BOARD
        val cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"
        val securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH ?: "Current"
        } else "N/A"

        // Memory info
        var totalRamGb = 0.0
        var availRamGb = 0.0
        var ramUsagePercent = 0
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            totalRamGb = memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
            availRamGb = memInfo.availMem / (1024.0 * 1024.0 * 1024.0)
            if (totalRamGb > 0) {
                ramUsagePercent = (((totalRamGb - availRamGb) / totalRamGb) * 100).toInt().coerceIn(0, 100)
            }
        } catch (_: Exception) {
            totalRamGb = 6.0
            availRamGb = 3.2
            ramUsagePercent = 48
        }

        // Battery info
        var batteryPercent = 50
        var isCharging = false
        try {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, iFilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) {
                batteryPercent = ((level / scale.toFloat()) * 100).toInt()
            }
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        } catch (_: Exception) {
            batteryPercent = 60
        }

        // Uptime
        val uptimeMillis = SystemClock.elapsedRealtime()
        val uptimeHours = (uptimeMillis / (1000 * 60 * 60))
        val uptimeMins = (uptimeMillis / (1000 * 60)) % 60
        val uptimeFormatted = "${uptimeHours}h ${uptimeMins}m"

        return HostDeviceInfo(
            deviceName = deviceName,
            manufacturer = manufacturer,
            model = model,
            brand = brand,
            androidVersion = androidVersion,
            apiLevel = apiLevel,
            codeName = codeName,
            buildId = buildId,
            hardware = hardware,
            board = board,
            cpuAbi = cpuAbi,
            totalRamGb = totalRamGb,
            availableRamGb = availRamGb,
            ramUsagePercent = ramUsagePercent,
            batteryPercent = batteryPercent.coerceIn(0, 100),
            isCharging = isCharging,
            securityPatch = securityPatch,
            uptimeFormatted = uptimeFormatted
        )
    }

    private fun getAndroidVersionCodeName(api: Int): String {
        return when (api) {
            35 -> "Vanilla Ice Cream (15)"
            34 -> "Upside Down Cake (14)"
            33 -> "Tiramisu (13)"
            32, 31 -> "Snow Cone (12)"
            30 -> "Red Velvet Cake (11)"
            29 -> "Quince Tart (10)"
            28 -> "Pie (9.0)"
            27, 26 -> "Oreo (8.x)"
            else -> "API $api"
        }
    }
}

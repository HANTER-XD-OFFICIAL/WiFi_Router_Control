package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

data class CurrentWifiState(
    val isWifiConnected: Boolean = false,
    val ssid: String = "Not Connected",
    val bssid: String = "--:--:--:--:--:--",
    val gatewayIp: String = "192.168.0.1",
    val localIp: String = "127.0.0.1",
    val subnetMask: String = "255.255.255.0",
    val dns1: String = "8.8.8.8",
    val dns2: String = "8.8.4.4",
    val rssiDbm: Int = -100,
    val signalPercentage: Int = 0,
    val linkSpeedMbps: Int = 0,
    val frequencyMhz: Int = 0,
    val is5Ghz: Boolean = false,
    val guessedBrand: String = "Unknown Router",
    val reachable: Boolean = false,
    val gatewayPingMs: Long = -1L
)

object NetworkHelper {

    fun getWifiState(context: Context): CurrentWifiState {
        try {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

            val activeNetwork = connMgr?.activeNetwork
            val caps = activeNetwork?.let { connMgr.getNetworkCapabilities(it) }
            val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

            if (!isWifi || wifiMgr == null) {
                return CurrentWifiState(isWifiConnected = false)
            }

            val wifiInfo: WifiInfo? = try { wifiMgr.connectionInfo } catch (_: Exception) { null }
            val dhcpInfo: DhcpInfo? = try { wifiMgr.dhcpInfo } catch (_: Exception) { null }

            var ssid = wifiInfo?.ssid?.replace("\"", "") ?: "Connected WiFi"
            if (ssid == "<unknown ssid>" || ssid.isBlank()) {
                ssid = "Connected WiFi"
            }

            val bssid = wifiInfo?.bssid ?: "--:--:--:--:--:--"
            val gatewayInt = dhcpInfo?.gateway ?: 0
            val gatewayIp = if (gatewayInt != 0) formatIpAddress(gatewayInt) else "192.168.0.1"

            val localIpInt = dhcpInfo?.ipAddress ?: (wifiInfo?.ipAddress ?: 0)
            val localIp = if (localIpInt != 0) formatIpAddress(localIpInt) else "192.168.0.100"

            val netmaskInt = dhcpInfo?.netmask ?: 0
            val subnetMask = if (netmaskInt != 0) formatIpAddress(netmaskInt) else "255.255.255.0"

            val dns1Int = dhcpInfo?.dns1 ?: 0
            val dns1 = if (dns1Int != 0) formatIpAddress(dns1Int) else "8.8.8.8"

            val dns2Int = dhcpInfo?.dns2 ?: 0
            val dns2 = if (dns2Int != 0) formatIpAddress(dns2Int) else "8.8.4.4"

            val rssi = wifiInfo?.rssi ?: -65
            val signalPercent = try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    wifiMgr.calculateSignalLevel(rssi) * 25 // 0-4 scale converted to 0-100
                } else {
                    @Suppress("DEPRECATION")
                    WifiManager.calculateSignalLevel(rssi, 100).coerceIn(0, 100)
                }
            } catch (_: Exception) {
                (rssi + 100).coerceIn(0, 100)
            }

            val linkSpeed = wifiInfo?.linkSpeed ?: 0
            val frequency = wifiInfo?.frequency ?: 0
            val is5G = frequency >= 4900

            val brand = RouterPresets.guessBrandByGateway(gatewayIp)

            return CurrentWifiState(
                isWifiConnected = true,
                ssid = ssid,
                bssid = bssid,
                gatewayIp = gatewayIp,
                localIp = localIp,
                subnetMask = subnetMask,
                dns1 = dns1,
                dns2 = dns2,
                rssiDbm = rssi,
                signalPercentage = signalPercent.coerceIn(0, 100),
                linkSpeedMbps = linkSpeed,
                frequencyMhz = frequency,
                is5Ghz = is5G,
                guessedBrand = brand
            )
        } catch (_: Exception) {
            return CurrentWifiState(
                isWifiConnected = true,
                ssid = "Connected WiFi",
                gatewayIp = "192.168.0.1",
                localIp = "192.168.0.100"
            )
        }
    }

    private fun formatIpAddress(ip: Int): String {
        return "${ip and 0xFF}.${(ip shr 8) and 0xFF}.${(ip shr 16) and 0xFF}.${(ip shr 24) and 0xFF}"
    }

    suspend fun testPingLatency(host: String, port: Int = 80, timeoutMs: Int = 2000): Long = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            socket.close()
            System.currentTimeMillis() - start
        } catch (_: Exception) {
            try {
                // Fallback to ICMP reachable
                val address = InetAddress.getByName(host)
                if (address.isReachable(timeoutMs)) {
                    System.currentTimeMillis() - start
                } else {
                    -1L
                }
            } catch (_: Exception) {
                -1L
            }
        }
    }

    suspend fun checkIsPortOpen(host: String, port: Int, timeoutMs: Int = 1200): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            socket.close()
            true
        } catch (_: Exception) {
            false
        }
    }
}

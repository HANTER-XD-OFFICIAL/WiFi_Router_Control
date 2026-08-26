package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

data class LanDevice(
    val ip: String,
    val mac: String = "",
    val hostname: String = "",
    val vendor: String = "Generic Device",
    val isGateway: Boolean = false,
    val isCurrentDevice: Boolean = false,
    val pingMs: Long = 0,
    val openPorts: List<Int> = emptyList(),
    val deviceType: String = "Device" // Phone, Laptop, TV, Router, IoT, Other
)

object LanScanner {

    fun scanSubnetFlow(gatewayIp: String, localIp: String): Flow<ScanProgress> = flow {
        emit(ScanProgress(status = "Initializing subnet scanner...", percentage = 0, devices = emptyList()))

        val prefix = gatewayIp.substringBeforeLast(".")
        val discoveredDevices = mutableListOf<LanDevice>()
        val arpTable = readArpTable()

        // Include Gateway immediately
        val gwMac = arpTable[gatewayIp] ?: "--:--:--:--:--:--"
        val gwDevice = LanDevice(
            ip = gatewayIp,
            mac = gwMac,
            hostname = "Router / Gateway",
            vendor = RouterPresets.guessBrandByGateway(gatewayIp),
            isGateway = true,
            deviceType = "Router"
        )
        discoveredDevices.add(gwDevice)
        emit(ScanProgress(status = "Found Gateway: $gatewayIp", percentage = 5, devices = discoveredDevices.toList()))

        // Include current device
        if (localIp != gatewayIp && localIp.isNotBlank()) {
            val thisDevice = LanDevice(
                ip = localIp,
                mac = "This Phone",
                hostname = "This Android Device",
                vendor = "Android",
                isCurrentDevice = true,
                deviceType = "Phone"
            )
            discoveredDevices.add(thisDevice)
            emit(ScanProgress(status = "Scanning active clients...", percentage = 10, devices = discoveredDevices.toList()))
        }

        // Batch scan 1 to 254 in chunks of 32
        val allIps = (1..254).map { "$prefix.$it" }.filter { it != gatewayIp && it != localIp }
        val chunkSize = 32
        val chunks = allIps.chunked(chunkSize)

        for ((index, chunk) in chunks.withIndex()) {
            val results = withContext(Dispatchers.IO) {
                chunk.map { ip ->
                    async {
                        probeIp(ip, arpTable[ip])
                    }
                }.awaitAll()
            }

            for (device in results.filterNotNull()) {
                if (discoveredDevices.none { it.ip == device.ip }) {
                    discoveredDevices.add(device)
                }
            }

            val progressPercent = 10 + ((index + 1).toFloat() / chunks.size * 90).toInt()
            emit(
                ScanProgress(
                    status = "Scanning IP block ($progressPercent%)... Found ${discoveredDevices.size} devices",
                    percentage = progressPercent,
                    devices = discoveredDevices.toList()
                )
            )
        }

        emit(
            ScanProgress(
                status = "Scan complete! ${discoveredDevices.size} devices found.",
                percentage = 100,
                devices = discoveredDevices.toList(),
                isCompleted = true
            )
        )
    }.flowOn(Dispatchers.IO)

    private suspend fun probeIp(ip: String, cachedMac: String?): LanDevice? = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        var reachable = false
        val probePorts = listOf(80, 443, 8080, 53, 22, 554, 445, 139, 8008, 9000)
        val openPorts = mutableListOf<Int>()

        for (port in probePorts) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 120)
                socket.close()
                reachable = true
                openPorts.add(port)
            } catch (_: Exception) {
            }
        }

        if (!reachable) {
            try {
                val addr = InetAddress.getByName(ip)
                if (addr.isReachable(150)) {
                    reachable = true
                }
            } catch (_: Exception) {
            }
        }

        if (reachable || cachedMac != null) {
            val pingMs = (System.currentTimeMillis() - start).coerceAtLeast(1)
            var hostname = ""
            try {
                val addr = InetAddress.getByName(ip)
                val canonical = addr.canonicalHostName
                if (canonical != ip && canonical.isNotBlank()) {
                    hostname = canonical
                }
            } catch (_: Exception) {
            }

            val mac = cachedMac ?: readArpTable()[ip] ?: "Dynamic / Hidden"
            val vendor = lookupVendor(mac, hostname)
            val type = guessDeviceType(hostname, openPorts, vendor)

            LanDevice(
                ip = ip,
                mac = mac,
                hostname = hostname.ifBlank { "Client $ip" },
                vendor = vendor,
                pingMs = pingMs,
                openPorts = openPorts,
                deviceType = type
            )
        } else {
            null
        }
    }

    private fun readArpTable(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            val reader = BufferedReader(FileReader("/proc/net/arp"))
            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val tokens = line.split("\\s+".toRegex())
                    if (tokens.size >= 4) {
                        val ip = tokens[0]
                        val mac = tokens[3]
                        if (mac != "00:00:00:00:00:00" && mac.length == 17) {
                            map[ip] = mac.uppercase()
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
        return map
    }

    private fun lookupVendor(mac: String, hostname: String): String {
        val upperMac = mac.uppercase()
        val h = hostname.lowercase()

        return when {
            h.contains("apple") || h.contains("iphone") || h.contains("ipad") || h.contains("macbook") -> "Apple"
            h.contains("samsung") || h.contains("galaxy") -> "Samsung"
            h.contains("xiaomi") || h.contains("redmi") || h.contains("mi") -> "Xiaomi"
            h.contains("tplink") || h.contains("tp-link") -> "TP-Link"
            h.contains("tenda") -> "Tenda"
            h.contains("huawei") || h.contains("honor") -> "Huawei"
            h.contains("google") || h.contains("chromecast") || h.contains("pixel") -> "Google"
            h.contains("sony") || h.contains("playstation") -> "Sony"
            h.contains("espressif") || h.contains("esp32") || h.contains("esp8266") -> "Espressif IoT"
            h.contains("raspberry") || h.contains("rpi") -> "Raspberry Pi"
            h.contains("tv") || h.contains("androidtv") || h.contains("smarttv") -> "Smart TV"
            upperMac.startsWith("00:50:56") || upperMac.startsWith("00:0C:29") -> "VMware"
            upperMac.startsWith("B8:27:EB") || upperMac.startsWith("DC:A6:32") -> "Raspberry Pi"
            upperMac.startsWith("F4:F5:DB") || upperMac.startsWith("50:C7:BF") -> "TP-Link"
            upperMac.startsWith("C8:3A:35") || upperMac.startsWith("CC:2D:21") -> "Tenda"
            upperMac.startsWith("00:1A:2B") || upperMac.startsWith("78:11:DC") -> "Xiaomi"
            upperMac.startsWith("AC:BC:32") || upperMac.startsWith("F0:18:98") -> "Apple"
            upperMac.startsWith("94:35:0A") || upperMac.startsWith("40:4E:36") -> "Samsung"
            else -> "Network Device"
        }
    }

    private fun guessDeviceType(hostname: String, openPorts: List<Int>, vendor: String): String {
        val h = hostname.lowercase()
        return when {
            h.contains("phone") || h.contains("mobile") || h.contains("android") || h.contains("iphone") -> "Phone"
            h.contains("laptop") || h.contains("macbook") || h.contains("desktop") || h.contains("pc") || h.contains("windows") -> "Laptop"
            h.contains("tv") || h.contains("cast") || h.contains("roku") || h.contains("firestick") || openPorts.contains(8008) -> "TV"
            h.contains("camera") || h.contains("dvr") || h.contains("nvr") || openPorts.contains(554) -> "Camera"
            h.contains("print") || openPorts.contains(9100) || openPorts.contains(631) -> "Printer"
            h.contains("router") || openPorts.contains(80) && openPorts.contains(53) -> "Router"
            vendor == "Espressif IoT" || vendor == "Raspberry Pi" -> "Smart Home / IoT"
            else -> "Device"
        }
    }
}

data class ScanProgress(
    val status: String,
    val percentage: Int,
    val devices: List<LanDevice>,
    val isCompleted: Boolean = false
)

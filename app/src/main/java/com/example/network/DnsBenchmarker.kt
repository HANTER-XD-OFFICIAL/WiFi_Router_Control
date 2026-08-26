package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

data class DnsServerItem(
    val name: String,
    val primaryIp: String,
    val secondaryIp: String,
    val provider: String,
    val features: String,
    val latencyMs: Long = -1L,
    val isBest: Boolean = false
)

object DnsBenchmarker {

    val POPULAR_DNS_PROVIDERS = listOf(
        DnsServerItem("Cloudflare DNS", "1.1.1.1", "1.0.0.1", "Cloudflare", "Ultra-fast, Privacy Focused, No Logs"),
        DnsServerItem("Google Public DNS", "8.8.8.8", "8.8.4.4", "Google", "High Reliability, Worldwide Anycast"),
        DnsServerItem("OpenDNS Home", "208.67.222.222", "208.67.220.220", "Cisco", "Phishing Protection, Parental Controls"),
        DnsServerItem("Quad9 Secure", "9.9.9.9", "149.112.112.112", "Quad9", "Malware & Threat Blocking"),
        DnsServerItem("AdGuard DNS", "94.140.14.14", "94.140.15.15", "AdGuard", "Built-in Ads & Tracker Blocker"),
        DnsServerItem("Control D", "76.76.2.0", "76.76.10.0", "Control D", "Fast Anycast, Clean DNS")
    )

    fun benchmarkDnsList(): Flow<List<DnsServerItem>> = flow {
        val measuredList = withContext(Dispatchers.IO) {
            POPULAR_DNS_PROVIDERS.map { item ->
                async {
                    val lat = measureDnsLatency(item.primaryIp)
                    item.copy(latencyMs = lat)
                }
            }.awaitAll()
        }

        val minLat = measuredList.filter { it.latencyMs > 0 }.minOfOrNull { it.latencyMs } ?: 0L
        val formatted = measuredList.map {
            if (it.latencyMs > 0 && it.latencyMs == minLat) it.copy(isBest = true) else it
        }.sortedBy { if (it.latencyMs > 0) it.latencyMs else 9999L }

        emit(formatted)
    }.flowOn(Dispatchers.IO)

    private fun measureDnsLatency(ip: String): Long {
        val start = System.currentTimeMillis()
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, 53), 1000)
            socket.close()
            System.currentTimeMillis() - start
        } catch (_: Exception) {
            try {
                val addr = InetAddress.getByName(ip)
                if (addr.isReachable(1000)) System.currentTimeMillis() - start else -1L
            } catch (_: Exception) {
                -1L
            }
        }
    }
}

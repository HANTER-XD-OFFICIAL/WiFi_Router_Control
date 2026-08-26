package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

data class PortInfo(
    val port: Int,
    val serviceName: String,
    val description: String,
    val isOpen: Boolean = false,
    val latencyMs: Long = -1L
)

object PortScannerEngine {

    val COMMON_ROUTER_PORTS = listOf(
        PortInfo(80, "HTTP", "Standard Web Admin Console"),
        PortInfo(443, "HTTPS", "Secure Web Admin Console (SSL)"),
        PortInfo(8080, "HTTP-Alt", "Alternate Web Admin / Remote Management"),
        PortInfo(8443, "HTTPS-Alt", "Secure Remote Management Port"),
        PortInfo(53, "DNS", "Local Router DNS Proxy Service"),
        PortInfo(22, "SSH", "Secure Shell Terminal Access (OpenWrt/MikroTik)"),
        PortInfo(23, "Telnet", "Unencrypted Router Command Line (Legacy)"),
        PortInfo(21, "FTP", "Router USB File Sharing Server"),
        PortInfo(445, "SMB", "Samba Windows Network File Sharing"),
        PortInfo(554, "RTSP", "Real-Time Streaming / IP Camera Gateway"),
        PortInfo(8291, "Winbox", "MikroTik RouterOS Native Winbox Port"),
        PortInfo(1900, "UPnP", "Universal Plug and Play Discovery"),
        PortInfo(5000, "UPnP/Web", "Synology / Smart Router Web Port")
    )

    fun scanRouterPorts(targetHost: String): Flow<List<PortInfo>> = flow {
        val results = mutableListOf<PortInfo>()

        val chunks = COMMON_ROUTER_PORTS.chunked(4)
        for (chunk in chunks) {
            val evaluated = withContext(Dispatchers.IO) {
                chunk.map { portItem ->
                    async {
                        val start = System.currentTimeMillis()
                        try {
                            val socket = Socket()
                            socket.connect(InetSocketAddress(targetHost, portItem.port), 800)
                            socket.close()
                            val lat = System.currentTimeMillis() - start
                            portItem.copy(isOpen = true, latencyMs = lat)
                        } catch (_: Exception) {
                            portItem.copy(isOpen = false, latencyMs = -1L)
                        }
                    }
                }.awaitAll()
            }
            results.addAll(evaluated)
            emit(results.toList())
        }
    }.flowOn(Dispatchers.IO)
}

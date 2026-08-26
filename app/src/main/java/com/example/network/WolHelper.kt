package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object WolHelper {

    suspend fun sendWakeOnLan(macAddress: String, broadcastIp: String = "255.255.255.255", port: Int = 9): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cleanMac = macAddress.replace(":", "").replace("-", "")
            if (cleanMac.length != 12) {
                return@withContext Result.failure(IllegalArgumentException("Invalid MAC address length"))
            }

            val macBytes = ByteArray(6)
            for (i in 0 until 6) {
                macBytes[i] = cleanMac.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }

            val magicBytes = ByteArray(102)
            for (i in 0 until 6) {
                magicBytes[i] = 0xFF.toByte()
            }
            for (i in 1..16) {
                System.arraycopy(macBytes, 0, magicBytes, i * 6, 6)
            }

            val address = InetAddress.getByName(broadcastIp)
            val packet = DatagramPacket(magicBytes, magicBytes.size, address, port)
            val socket = DatagramSocket()
            socket.broadcast = true
            socket.send(packet)
            socket.close()

            Result.success("Magic WOL Packet sent to $macAddress via $broadcastIp:$port")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

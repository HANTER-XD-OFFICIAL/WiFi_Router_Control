package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceNicknameEntity
import com.example.network.LanDevice
import com.example.network.ScanProgress
import com.example.ui.components.StatusPill
import com.example.ui.components.TechCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SurfaceCardNavy
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ConnectedDevicesScreen(
    scanProgress: ScanProgress,
    savedNicknames: List<DeviceNicknameEntity>,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onSaveNickname: (mac: String, ip: String, name: String, type: String, isBlocked: Boolean) -> Unit,
    onSendWol: (mac: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var editingDevice by remember { mutableStateOf<LanDevice?>(null) }
    var blockingMacRule by remember { mutableStateOf<String?>(null) }

    val nicknameMap = remember(savedNicknames) {
        savedNicknames.associateBy { it.macAddress.uppercase() }
    }

    val categories = listOf("All", "Phone", "Laptop", "TV", "Router", "Smart Home / IoT")

    val filteredDevices = remember(scanProgress.devices, searchQuery, selectedCategory, nicknameMap) {
        scanProgress.devices.filter { dev ->
            val custom = nicknameMap[dev.mac.uppercase()]
            val name = custom?.customName ?: dev.hostname
            val type = custom?.deviceType ?: dev.deviceType

            val matchesQuery = searchQuery.isBlank() ||
                name.contains(searchQuery, ignoreCase = true) ||
                dev.ip.contains(searchQuery) ||
                dev.mac.contains(searchQuery, ignoreCase = true) ||
                dev.vendor.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == "All" || type.equals(selectedCategory, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Scanner Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WiFi Connected Devices Scanner",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Discover active clients connected to your subnet",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }

                        if (!scanProgress.isCompleted && scanProgress.percentage in 1..99) {
                            IconButton(
                                onClick = onStopScan,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AccentRed.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = AccentRed)
                            }
                        } else {
                            Button(
                                onClick = onStartScan,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B)),
                                modifier = Modifier.testTag("start_scan_btn")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Scan", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Scan",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    if (scanProgress.percentage > 0 && !scanProgress.isCompleted) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { scanProgress.percentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryCyan,
                            trackColor = SurfaceElevated
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = scanProgress.status,
                            style = MaterialTheme.typography.labelSmall.copy(color = PrimaryCyan, fontSize = 11.sp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${scanProgress.status} • Found ${scanProgress.devices.size} devices",
                            style = MaterialTheme.typography.labelSmall.copy(color = AccentGreen, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // Search and Category Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
                placeholder = { Text("Search by IP, MAC, or Name...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = SurfaceCardNavy,
                    unfocusedContainerColor = SurfaceCardNavy
                )
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else SurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryCyan else BorderSubtle),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) PrimaryCyan else TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Device Items List
        if (filteredDevices.isEmpty()) {
            item {
                TechCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Devices, contentDescription = "No Devices", tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Devices Found",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Text(
                            text = "Tap 'Scan' above to discover devices on your subnet",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        } else {
            items(filteredDevices) { dev ->
                val custom = nicknameMap[dev.mac.uppercase()]
                DeviceCard(
                    device = dev,
                    customNickname = custom?.customName,
                    customType = custom?.deviceType,
                    isBlocked = custom?.isBlocked == true,
                    onEdit = { editingDevice = dev },
                    onBlock = {
                        blockingMacRule = dev.mac
                    },
                    onSendWol = { onSendWol(dev.mac) },
                    onCopy = { copyText(context, "${dev.hostname} (${dev.ip}) - MAC: ${dev.mac}") }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Edit Nickname Dialog
    if (editingDevice != null) {
        val dev = editingDevice!!
        val currentCustom = nicknameMap[dev.mac.uppercase()]
        EditDeviceDialog(
            device = dev,
            currentName = currentCustom?.customName ?: dev.hostname,
            currentType = currentCustom?.deviceType ?: dev.deviceType,
            currentBlocked = currentCustom?.isBlocked ?: false,
            onDismiss = { editingDevice = null },
            onSave = { name, type, blocked ->
                onSaveNickname(dev.mac, dev.ip, name, type, blocked)
                editingDevice = null
            }
        )
    }

    // Block Rule Helper Dialog
    if (blockingMacRule != null) {
        val mac = blockingMacRule!!
        AlertDialog(
            onDismissRequest = { blockingMacRule = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Block, contentDescription = "Block", tint = AccentRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Router MAC Filter Rule",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "To block this device, copy this MAC Address and add it to your Router's MAC Filter / Access Control Blacklist:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mac,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            )
                            IconButton(onClick = { copyText(context, mac) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        copyText(context, mac)
                        blockingMacRule = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                ) {
                    Text("Copy MAC", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { blockingMacRule = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceNavy
        )
    }
}

@Composable
fun DeviceCard(
    device: LanDevice,
    customNickname: String?,
    customType: String?,
    isBlocked: Boolean,
    onEdit: () -> Unit,
    onBlock: () -> Unit,
    onSendWol: () -> Unit,
    onCopy: () -> Unit
) {
    val displayName = customNickname?.ifBlank { null } ?: device.hostname.ifBlank { "Device ${device.ip}" }
    val displayType = customType ?: device.deviceType
    val icon = getDeviceIcon(displayType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (device.isGateway) PrimaryCyan.copy(alpha = 0.5f) else if (isBlocked) AccentRed.copy(alpha = 0.5f) else BorderSubtle,
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardNavy)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (device.isGateway) PrimaryCyan.copy(alpha = 0.15f)
                                else if (isBlocked) AccentRed.copy(alpha = 0.15f)
                                else PrimaryBlue.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = displayType,
                            tint = if (device.isGateway) PrimaryCyan else if (isBlocked) AccentRed else PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isBlocked) AccentRed else TextPrimary
                            ),
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${device.ip} • ${device.vendor}",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                }

                if (device.isGateway) {
                    StatusPill(text = "Gateway", color = PrimaryCyan)
                } else if (device.isCurrentDevice) {
                    StatusPill(text = "This Phone", color = AccentGreen)
                } else if (isBlocked) {
                    StatusPill(text = "Blocked", color = AccentRed)
                } else {
                    StatusPill(text = "${device.pingMs} ms", color = AccentGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MAC: ${device.mac}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                    if (device.openPorts.isNotEmpty()) {
                        Text(
                            text = "Ports: ${device.openPorts.take(3).joinToString(",")}",
                            style = MaterialTheme.typography.labelSmall.copy(color = AccentPurple, fontSize = 10.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    if (displayType == "Laptop" || displayType == "Desktop") {
                        IconButton(onClick = onSendWol, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Wake on LAN", tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                if (!device.isGateway && !device.isCurrentDevice) {
                    OutlinedButton(
                        onClick = onBlock,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = "Block", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Block", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun EditDeviceDialog(
    device: LanDevice,
    currentName: String,
    currentType: String,
    currentBlocked: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, blocked: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedType by remember { mutableStateOf(currentType) }
    var isBlocked by remember { mutableStateOf(currentBlocked) }

    val types = listOf("Phone", "Laptop", "Desktop", "TV", "Camera", "Printer", "Smart Home / IoT", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Device Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Custom Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                Text(
                    text = "Device Type:",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(types) { t ->
                        val isSelected = selectedType == t
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else SurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryCyan else BorderSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedType = t }
                        ) {
                            Text(
                                text = t,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) PrimaryCyan else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mark as Blocked",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                    )
                    Switch(
                        checked = isBlocked,
                        onCheckedChange = { isBlocked = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentRed, checkedTrackColor = SurfaceElevated)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, selectedType, isBlocked) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceNavy
    )
}

fun getDeviceIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "phone", "mobile", "android", "iphone" -> Icons.Default.PhoneAndroid
        "laptop", "macbook", "pc", "desktop", "computer" -> Icons.Default.Computer
        "tv", "smart tv", "roku", "chromecast" -> Icons.Default.Tv
        "camera", "cctv", "dvr" -> Icons.Default.CameraAlt
        "printer" -> Icons.Default.Print
        "router" -> Icons.Default.Router
        "smart home / iot", "iot", "smart home" -> Icons.Default.Lightbulb
        else -> Icons.Default.Devices
    }
}

private fun copyText(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Device Info", text)
    clipboard?.setPrimaryClip(clip)
}

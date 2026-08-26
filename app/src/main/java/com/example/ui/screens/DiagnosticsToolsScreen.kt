package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpeedTestHistoryEntity
import com.example.network.CurrentWifiState
import com.example.network.DnsServerItem
import com.example.network.PortInfo
import com.example.network.RouterBrandPreset
import com.example.network.RouterPresets
import com.example.network.SpeedStage
import com.example.network.SpeedTestState
import com.example.ui.components.StatusPill
import com.example.ui.components.StylizedQrMatrix
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticsToolsScreen(
    initialTab: Int,
    wifiState: CurrentWifiState,
    speedState: SpeedTestState,
    speedHistory: List<SpeedTestHistoryEntity>,
    scannedPorts: List<PortInfo>,
    isPortScanning: Boolean,
    dnsList: List<DnsServerItem>,
    isDnsBenchmarking: Boolean,
    onStartSpeedTest: () -> Unit,
    onStartPortScan: (String) -> Unit,
    onStartDnsBenchmark: () -> Unit,
    onSendWol: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 4)) }

    val tabTitles = listOf(
        "Speed Test",
        "Port Scan",
        "DNS Benchmark",
        "Default Passwords",
        "WiFi QR & WOL"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
    ) {
        // Tab Header
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceNavy,
            contentColor = PrimaryCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryCyan,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) PrimaryCyan else TextSecondary
                            )
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                0 -> SpeedTestTab(
                    speedState = speedState,
                    history = speedHistory,
                    onStart = onStartSpeedTest
                )
                1 -> PortScannerTab(
                    scannedPorts = scannedPorts,
                    isScanning = isPortScanning,
                    gatewayIp = wifiState.gatewayIp,
                    onScan = onStartPortScan
                )
                2 -> DnsBenchmarkTab(
                    dnsList = dnsList,
                    isBenchmarking = isDnsBenchmarking,
                    onBenchmark = onStartDnsBenchmark
                )
                3 -> DefaultPasswordsTab()
                4 -> WifiQrAndWolTab(
                    wifiState = wifiState,
                    onSendWol = onSendWol
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. SPEED TEST TAB
// -------------------------------------------------------------
@Composable
fun SpeedTestTab(
    speedState: SpeedTestState,
    history: List<SpeedTestHistoryEntity>,
    onStart: () -> Unit
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speedState.currentSpeedMbps.toFloat(),
        animationSpec = tween(durationMillis = 300),
        label = "SpeedGauge"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Speedometer Gauge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Real-time Speed & Bandwidth Meter",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gauge Canvas
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(180.dp)) {
                            val strokeWidth = 14.dp.toPx()
                            val arcSize = size.minDimension - strokeWidth

                            // Background Arc (240 degrees)
                            drawArc(
                                color = SurfaceElevated,
                                startAngle = 150f,
                                sweepAngle = 240f,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            // Active Progress Arc
                            val progressSweep = (animatedSpeed / 100f).coerceIn(0f, 1f) * 240f
                            drawArc(
                                brush = Brush.linearGradient(listOf(PrimaryCyan, AccentGreen)),
                                startAngle = 150f,
                                sweepAngle = progressSweep,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f".format(animatedSpeed),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 38.sp
                                )
                            )
                            Text(
                                text = "Mbps",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Latency / Jitter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCardNavy)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PING", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Text(
                                text = "${speedState.pingMs} ms",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("JITTER", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Text(
                                text = "${speedState.jitterMs} ms",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("STAGE", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Text(
                                text = speedState.stage.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStart,
                        enabled = speedState.stage != SpeedStage.DOWNLOADING && speedState.stage != SpeedStage.PINGING,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_speed_test_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = "Test", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (speedState.stage == SpeedStage.DOWNLOADING || speedState.stage == SpeedStage.PINGING)
                                "Testing..."
                            else
                                "Start Speed Test",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Test History Section
        item {
            Text(
                text = "Speed Test History",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        if (history.isEmpty()) {
            item {
                TechCard {
                    Text(
                        text = "No previous tests recorded",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        } else {
            items(history) { item ->
                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
                TechCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "%.2f Mbps".format(item.downloadMbps),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            )
                            Text(
                                text = "${item.ssid} • $dateStr",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                            )
                        }

                        StatusPill(text = "${item.pingMs} ms ping", color = AccentGreen)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// -------------------------------------------------------------
// 2. PORT SCANNER TAB
// -------------------------------------------------------------
@Composable
fun PortScannerTab(
    scannedPorts: List<PortInfo>,
    isScanning: Boolean,
    gatewayIp: String,
    onScan: (String) -> Unit
) {
    var targetIp by remember { mutableStateOf(gatewayIp) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            TechCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Router Open Port Scanner",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Discover active admin, SSH, FTP, camera, or management services",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = targetIp,
                            onValueChange = { targetIp = it },
                            label = { Text("Target IP") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Button(
                            onClick = { onScan(targetIp) },
                            enabled = !isScanning,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isScanning) "Scanning..." else "Scan",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isScanning) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = PrimaryCyan,
                            trackColor = SurfaceElevated
                        )
                    }
                }
            }
        }

        if (scannedPorts.isEmpty() && !isScanning) {
            item {
                TechCard {
                    Text(
                        text = "Tap 'Scan' to analyze router ports",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        } else {
            items(scannedPorts) { port ->
                TechCard(
                    borderColor = if (port.isOpen) PrimaryCyan.copy(alpha = 0.6f) else BorderSubtle
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Port ${port.port} (${port.serviceName})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (port.isOpen) PrimaryCyan else TextPrimary
                                    )
                                )
                            }
                            Text(
                                text = port.description,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                            )
                        }

                        StatusPill(
                            text = if (port.isOpen) "OPEN (${port.latencyMs}ms)" else "CLOSED",
                            color = if (port.isOpen) AccentGreen else TextMuted
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// -------------------------------------------------------------
// 3. DNS BENCHMARK TAB
// -------------------------------------------------------------
@Composable
fun DnsBenchmarkTab(
    dnsList: List<DnsServerItem>,
    isBenchmarking: Boolean,
    onBenchmark: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            TechCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Fastest DNS Benchmark & Switcher",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Find the fastest DNS servers for your internet connection",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }

                        Button(
                            onClick = onBenchmark,
                            enabled = !isBenchmarking,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Test", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBenchmarking) "Testing..." else "Test",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    if (isBenchmarking) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = PrimaryCyan,
                            trackColor = SurfaceElevated
                        )
                    }
                }
            }
        }

        items(dnsList) { dns ->
            TechCard(
                borderColor = if (dns.isBest) AccentGreen.copy(alpha = 0.7f) else BorderSubtle
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dns.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (dns.isBest) AccentGreen else TextPrimary
                                )
                            )
                            if (dns.isBest) {
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusPill(text = "FASTEST", color = AccentGreen)
                            }
                        }
                        Text(
                            text = "${dns.primaryIp}  •  ${dns.secondaryIp}",
                            style = MaterialTheme.typography.labelSmall.copy(color = PrimaryCyan, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = dns.features,
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (dns.latencyMs > 0) {
                            StatusPill(text = "${dns.latencyMs} ms", color = if (dns.latencyMs < 40) AccentGreen else PrimaryBlue)
                        }
                        IconButton(onClick = { copyText(context, "${dns.primaryIp}, ${dns.secondaryIp}") }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// -------------------------------------------------------------
// 4. DEFAULT ROUTER PASSWORDS TAB
// -------------------------------------------------------------
@Composable
fun DefaultPasswordsTab() {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    val filteredList = remember(query) {
        RouterPresets.BRAND_LIST.filter {
            query.isBlank() ||
                it.brand.contains(query, ignoreCase = true) ||
                it.defaultGateway.contains(query) ||
                it.popularModels.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
                placeholder = { Text("Search brand or model (TP-Link, Tenda, Asus)...") },
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

        items(filteredList) { item ->
            TechCard {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.brand,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        )
                        StatusPill(text = item.defaultGateway, color = PrimaryBlue)
                    }

                    if (item.popularModels.isNotBlank()) {
                        Text(
                            text = "Models: ${item.popularModels}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "User: ${item.defaultUser} | Pass: ${item.defaultPass.ifBlank { "(empty / none)" }}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )

                            IconButton(
                                onClick = { copyText(context, "User: ${item.defaultUser}\nPass: ${item.defaultPass}") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// -------------------------------------------------------------
// 5. WIFI QR & WAKE ON LAN TAB
// -------------------------------------------------------------
@Composable
fun WifiQrAndWolTab(
    wifiState: CurrentWifiState,
    onSendWol: (String) -> Unit
) {
    val context = LocalContext.current
    var wifiPasswordInput by remember { mutableStateOf("") }
    var wolMacInput by remember { mutableStateOf("") }

    val qrString = "WIFI:S:${wifiState.ssid};T:WPA;P:$wifiPasswordInput;;"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // WiFi QR Share Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WiFi Instant QR Code Share",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Share your WiFi securely with family & guests via QR Code",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StylizedQrMatrix(contentString = qrString)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = wifiState.ssid,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = wifiPasswordInput,
                        onValueChange = { wifiPasswordInput = it },
                        label = { Text("WiFi Password (to include in QR)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                }
            }
        }

        // Wake On LAN Card
        item {
            TechCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = "WOL", tint = PrimaryCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Wake-On-LAN (WOL) Tool",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                    Text(
                        text = "Turn on sleeping PCs/servers over local or remote network via magic packet",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    OutlinedTextField(
                        value = wolMacInput,
                        onValueChange = { wolMacInput = it },
                        label = { Text("Target PC MAC Address") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Button(
                        onClick = {
                            if (wolMacInput.isNotBlank()) onSendWol(wolMacInput)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = "WOL", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Wake Packet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

private fun copyText(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Copied", text)
    clipboard?.setPrimaryClip(clip)
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouterEntity
import com.example.network.CurrentWifiState
import com.example.ui.components.DeveloperSupportQuickCard
import com.example.ui.components.SignalQualityIndicator
import com.example.ui.components.StatusPill
import com.example.ui.components.TechCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.PrimaryCyanVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SurfaceCardNavy
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    wifiState: CurrentWifiState,
    boundRouters: List<RouterEntity>,
    onOpenAdmin: (url: String, user: String, pass: String) -> Unit,
    onNavigateToBinding: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToTools: (initialTab: Int) -> Unit,
    onOpenDeveloperSupport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "DashboardPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowIntensity"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Hero Router Gateway Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                PrimaryCyan.copy(alpha = pulseGlow),
                                PrimaryBlue,
                                AccentGreen.copy(alpha = 0.5f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .testTag("hero_router_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PrimaryBlue.copy(alpha = 0.18f),
                                    SurfaceNavy
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    // Gateway Header & Status Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ACTIVE GATEWAY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PrimaryCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    copyText(context, wifiState.gatewayIp)
                                }
                            ) {
                                Text(
                                    text = wifiState.gatewayIp,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy IP",
                                    tint = PrimaryCyan.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        StatusPill(
                            text = if (wifiState.gatewayPingMs >= 0) "${wifiState.gatewayPingMs} ms" else if (wifiState.isWifiConnected) "Connected" else "Offline",
                            color = if (wifiState.gatewayPingMs in 0..80) AccentGreen else if (wifiState.isWifiConnected) AccentOrange else AccentRed
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Brand & Link Speed Telemetry Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceCardNavy)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HARDWARE VENDOR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = wifiState.guessedBrand,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                            )
                        }

                        SignalQualityIndicator(
                            rssiDbm = wifiState.rssiDbm,
                            percentage = wifiState.signalPercentage,
                            is5Ghz = wifiState.is5Ghz
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Open Router Admin Button
                    Button(
                        onClick = {
                            onOpenAdmin("http://${wifiState.gatewayIp}", "admin", "admin")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("open_admin_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryCyan,
                            contentColor = Color(0xFF001B24)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open Admin",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Launch Gateway Console (${wifiState.gatewayIp})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Direct Cudy Router (LuCI) Quick Launch
                    OutlinedButton(
                        onClick = {
                            onOpenAdmin("http://192.168.10.1/cgi-bin/luci/", "admin", "admin")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_cudy_admin_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "Cudy LuCI",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Direct Cudy LuCI (192.168.10.1/cgi-bin/luci/)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Live Vital Network Telemetry Widgets Grid (4 Quadrants)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TelemetryWidget(
                    title = "SIGNAL STRENGTH",
                    value = "${wifiState.signalPercentage}%",
                    subtitle = "${wifiState.rssiDbm} dBm",
                    icon = Icons.Default.SignalCellularAlt,
                    accentColor = if (wifiState.signalPercentage >= 50) AccentGreen else AccentOrange,
                    modifier = Modifier.weight(1f)
                )

                TelemetryWidget(
                    title = "BAND / FREQ",
                    value = if (wifiState.is5Ghz) "5.0 GHz" else "2.4 GHz",
                    subtitle = "${wifiState.linkSpeedMbps} Mbps Link",
                    icon = Icons.Default.WifiTethering,
                    accentColor = PrimaryCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TelemetryWidget(
                    title = "LATENCY PING",
                    value = if (wifiState.gatewayPingMs >= 0) "${wifiState.gatewayPingMs} ms" else "---",
                    subtitle = "Gateway RTT",
                    icon = Icons.Default.Timer,
                    accentColor = if (wifiState.gatewayPingMs in 0..60) AccentGreen else AccentOrange,
                    modifier = Modifier.weight(1f)
                )

                TelemetryWidget(
                    title = "SUBNET IP",
                    value = wifiState.localIp,
                    subtitle = "Class C /24",
                    icon = Icons.Default.Security,
                    accentColor = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bound Routers Quick Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(PrimaryCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bound Cloud & Remote Routers",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                    }

                    Text(
                        text = "Manage All →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryCyan,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToBinding() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (boundRouters.isEmpty()) {
                    TechCard(
                        modifier = Modifier
                            .clickable { onNavigateToBinding() }
                            .testTag("empty_bound_card"),
                        backgroundGradient = listOf(PrimaryBlue.copy(alpha = 0.12f), SurfaceCardNavy)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.2f))
                                        .border(1.dp, PrimaryCyan.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = "Cloud",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Add / Bind Routers for Remote Access",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Control home & office routers from anywhere globally",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(boundRouters) { router ->
                            BoundRouterMiniCard(
                                router = router,
                                onOpen = {
                                    val target = if (router.remoteDnsUrl.isNotBlank()) router.remoteDnsUrl else "${router.protocol}://${router.ipOrHostname}:${router.port}"
                                    onOpenAdmin(target, router.username, router.password)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Quick Control & Tools Grid
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AccentGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Diagnostics & Toolkit",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Subnet Scanner",
                        subtitle = "Find connected IP/MACs",
                        icon = Icons.Default.Devices,
                        tint = PrimaryCyan,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDevices
                    )

                    QuickActionCard(
                        title = "Router Binding",
                        subtitle = "DDNS & Cloud Setup",
                        icon = Icons.Default.CloudDone,
                        tint = AccentGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBinding
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Speed / Ping Test",
                        subtitle = "Bandwidth throughput",
                        icon = Icons.Default.Speed,
                        tint = AccentOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTools(0) }
                    )

                    QuickActionCard(
                        title = "Port Scanner",
                        subtitle = "Discover open services",
                        icon = Icons.Default.TravelExplore,
                        tint = AccentPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTools(1) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "DNS Benchmark",
                        subtitle = "Fastest resolver test",
                        icon = Icons.Default.Dns,
                        tint = PrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTools(2) }
                    )

                    QuickActionCard(
                        title = "Default Logins",
                        subtitle = "50+ Brands Library",
                        icon = Icons.Default.Key,
                        tint = AccentGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTools(3) }
                    )
                }
            }
        }

        // Network Specifications Details Card
        item {
            TechCard(
                borderColor = BorderSubtle,
                backgroundGradient = listOf(SurfaceNavy, SurfaceCardNavy)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Network Configuration Matrix",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        StatusPill(text = "LIVE TELEMETRY", color = PrimaryCyan)
                    }

                    HorizontalDivider(color = BorderSubtle)

                    SpecRow(label = "SSID (Network Name)", value = wifiState.ssid, onCopy = { copyText(context, wifiState.ssid) })
                    SpecRow(label = "Local IP Address", value = wifiState.localIp, onCopy = { copyText(context, wifiState.localIp) })
                    SpecRow(label = "Default Gateway IP", value = wifiState.gatewayIp, onCopy = { copyText(context, wifiState.gatewayIp) })
                    SpecRow(label = "Subnet Mask", value = wifiState.subnetMask, onCopy = { copyText(context, wifiState.subnetMask) })
                    SpecRow(label = "Primary DNS Server", value = wifiState.dns1, onCopy = { copyText(context, wifiState.dns1) })
                    SpecRow(label = "Secondary DNS Server", value = wifiState.dns2, onCopy = { copyText(context, wifiState.dns2) })
                    SpecRow(label = "Access Point BSSID", value = wifiState.bssid, onCopy = { copyText(context, wifiState.bssid) })
                    SpecRow(label = "Physical Link Speed", value = "${wifiState.linkSpeedMbps} Mbps", onCopy = null)
                }
            }
        }

        // Developer Support Card
        item {
            DeveloperSupportQuickCard(
                onOpenSupport = onOpenDeveloperSupport
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun TelemetryWidget(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
        color = SurfaceCardNavy,
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.12f),
                            SurfaceCardNavy
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        fontSize = 17.sp
                    ),
                    maxLines = 1
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .border(1.dp, tint.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
        color = SurfaceCardNavy,
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            tint.copy(alpha = 0.12f),
                            SurfaceCardNavy
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tint.copy(alpha = 0.18f))
                        .border(1.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    ),
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun BoundRouterMiniCard(
    router: RouterEntity,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onOpen() }
            .border(1.2.dp, PrimaryCyan.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
        color = SurfaceCardNavy,
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PrimaryBlue.copy(alpha = 0.15f),
                            SurfaceCardNavy
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(text = router.locationTag, color = PrimaryCyan)
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Cloud",
                        tint = AccentGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = router.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    maxLines = 1
                )
                Text(
                    text = if (router.remoteDnsUrl.isNotBlank()) router.remoteDnsUrl else "${router.ipOrHostname}:${router.port}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = router.brand,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryCyan,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "Access Console →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SpecRow(
    label: String,
    value: String,
    onCopy: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 12.sp
                )
            )
            if (onCopy != null) {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = PrimaryCyan.copy(alpha = 0.8f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

private fun copyText(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Copied Spec", text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
}

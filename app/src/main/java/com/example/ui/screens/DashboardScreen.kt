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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TravelExplore
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SurfaceCardNavy
import com.example.ui.theme.SurfaceElevated
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
                        Brush.linearGradient(listOf(PrimaryCyan, PrimaryBlue)),
                        RoundedCornerShape(24.dp)
                    )
                    .testTag("hero_router_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Router Gateway",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = wifiState.gatewayIp,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryCyan,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        StatusPill(
                            text = if (wifiState.gatewayPingMs >= 0) "${wifiState.gatewayPingMs} ms" else if (wifiState.isWifiConnected) "Active" else "Offline",
                            color = if (wifiState.gatewayPingMs in 0..100) AccentGreen else if (wifiState.isWifiConnected) AccentOrange else AccentRed
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceCardNavy)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Detected Brand",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                            Text(
                                text = wifiState.guessedBrand,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
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
                            .height(50.dp)
                            .testTag("open_admin_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryCyan,
                            contentColor = Color(0xFF00222B)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open Admin",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Current Gateway (${wifiState.gatewayIp})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "Cudy LuCI",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Launch Cudy Router (192.168.10.1/cgi-bin/luci/)",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
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
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Bound Routers",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bound Remote Routers",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Text(
                        text = "View All",
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

                Spacer(modifier = Modifier.height(8.dp))

                if (boundRouters.isEmpty()) {
                    TechCard(
                        modifier = Modifier
                            .clickable { onNavigateToBinding() }
                            .testTag("empty_bound_card")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudQueue,
                                        contentDescription = "Cloud",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "No Bound Routers Yet",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Bind your router for global access from anywhere",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Go",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
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
            Text(
                text = "Router Tools & Controls",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Connected Devices",
                        subtitle = "LAN Subnet Scanner",
                        icon = Icons.Default.Devices,
                        tint = PrimaryCyan,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDevices
                    )

                    QuickActionCard(
                        title = "Router Binding",
                        subtitle = "Global Cloud Access",
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
                        title = "Speed & Ping Test",
                        subtitle = "Real-time Throughput",
                        icon = Icons.Default.Speed,
                        tint = AccentOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTools(0) }
                    )

                    QuickActionCard(
                        title = "Port Scanner",
                        subtitle = "Open Services & Ports",
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
                        subtitle = "Fast DNS Switcher",
                        icon = Icons.Default.Dns,
                        tint = PrimaryBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToTools(2) }
                    )

                    QuickActionCard(
                        title = "Default Passwords",
                        subtitle = "50+ Brands Directory",
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
            TechCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Network Configuration Specs",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    HorizontalDivider(color = BorderSubtle)

                    SpecRow(label = "SSID", value = wifiState.ssid, onCopy = { copyText(context, wifiState.ssid) })
                    SpecRow(label = "Local IP", value = wifiState.localIp, onCopy = { copyText(context, wifiState.localIp) })
                    SpecRow(label = "Gateway IP", value = wifiState.gatewayIp, onCopy = { copyText(context, wifiState.gatewayIp) })
                    SpecRow(label = "Subnet Mask", value = wifiState.subnetMask, onCopy = { copyText(context, wifiState.subnetMask) })
                    SpecRow(label = "DNS 1", value = wifiState.dns1, onCopy = { copyText(context, wifiState.dns1) })
                    SpecRow(label = "DNS 2", value = wifiState.dns2, onCopy = { copyText(context, wifiState.dns2) })
                    SpecRow(label = "BSSID", value = wifiState.bssid, onCopy = { copyText(context, wifiState.bssid) })
                    SpecRow(label = "Link Speed", value = "${wifiState.linkSpeedMbps} Mbps", onCopy = null)
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
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
        color = SurfaceCardNavy,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
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
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpen() }
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
        color = SurfaceCardNavy,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                    modifier = Modifier.size(16.dp)
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
                    color = TextMuted,
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
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Login →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
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
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
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
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
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
}

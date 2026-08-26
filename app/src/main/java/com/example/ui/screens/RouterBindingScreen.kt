package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RouterEntity
import com.example.network.RouterPresets
import com.example.ui.components.StatusPill
import com.example.ui.components.TechCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouterBindingScreen(
    routers: List<RouterEntity>,
    onSaveRouter: (
        name: String,
        brand: String,
        ip: String,
        port: Int,
        user: String,
        pass: String,
        isRemote: Boolean,
        remoteUrl: String,
        tag: String,
        notes: String
    ) -> Unit,
    onDeleteRouter: (RouterEntity) -> Unit,
    onOpenAdmin: (url: String, user: String, pass: String) -> Unit,
    onSendWol: (mac: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var routerToConfigureRemote by remember { mutableStateOf<RouterEntity?>(null) }
    var expandedGuideBrand by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Cloud Binding",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Router Cloud Binding & Remote Control",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Manage and access your routers from anywhere in the world",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }
                    }
                }
            }

            // Routers List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved & Bound Routers (${routers.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B)),
                        modifier = Modifier.testTag("add_router_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bind New",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            if (routers.isEmpty()) {
                item {
                    TechCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "No Routers",
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Routers Saved",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Tap 'Bind New' to add local or remote cloud routers",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            } else {
                items(routers) { router ->
                    RouterItemCard(
                        router = router,
                        onOpenLocal = {
                            val target = if (router.ipOrHostname.startsWith("http://") || router.ipOrHostname.startsWith("https://")) {
                                router.ipOrHostname
                            } else {
                                "${router.protocol}://${router.ipOrHostname}:${router.port}"
                            }
                            onOpenAdmin(target, router.username, router.password)
                        },
                        onOpenRemote = {
                            val target = if (router.remoteDnsUrl.isNotBlank()) {
                                router.remoteDnsUrl
                            } else {
                                "${router.protocol}://${router.ipOrHostname}:${router.port}"
                            }
                            onOpenAdmin(target, router.username, router.password)
                        },
                        onConfigureRemote = {
                            routerToConfigureRemote = router
                        },
                        onDelete = { onDeleteRouter(router) },
                        onCopyLink = {
                            val target = if (router.isRemoteBound && router.remoteDnsUrl.isNotBlank()) router.remoteDnsUrl else "http://${router.ipOrHostname}:${router.port}"
                            copyText(context, target)
                        }
                    )
                }
            }

            // Cloud Binding Guides Section
            item {
                Text(
                    text = "Popular Router Cloud Binding Guides",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            items(RouterPresets.BRAND_LIST.take(6)) { preset ->
                val isExpanded = expandedGuideBrand == preset.brand
                TechCard(
                    modifier = Modifier.clickable {
                        expandedGuideBrand = if (isExpanded) null else preset.brand
                    }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Router,
                                        contentDescription = preset.brand,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = preset.brand,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = preset.appBindSupport,
                                        style = MaterialTheme.typography.labelSmall.copy(color = AccentGreen, fontSize = 11.sp)
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = TextSecondary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider(color = BorderSubtle)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "How to Bind Remotely:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryCyan
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.cloudBindGuide,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Default Gateway: ${preset.defaultGateway} | User: ${preset.defaultUser} | Pass: ${preset.defaultPass.ifBlank { "(None)" }}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showAddDialog) {
        AddRouterDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, brand, ip, port, user, pass, isRemote, remoteUrl, tag, notes ->
                onSaveRouter(name, brand, ip, port, user, pass, isRemote, remoteUrl, tag, notes)
                showAddDialog = false
            }
        )
    }

    routerToConfigureRemote?.let { router ->
        var remoteInput by remember(router.id) {
            mutableStateOf(if (router.remoteDnsUrl.isNotBlank()) router.remoteDnsUrl else "http://${router.ipOrHostname}/cgi-bin/luci/")
        }

        AlertDialog(
            onDismissRequest = { routerToConfigureRemote = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remote Cloud Link Setup (${router.name})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "To manage and control this router remotely from mobile data or outside your home, enter your DDNS Domain (DuckDNS / No-IP / Cudy Cloud) or Public WAN IP / ISP Router address:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 18.sp)
                    )

                    OutlinedTextField(
                        value = remoteInput,
                        onValueChange = { remoteInput = it },
                        label = { Text("Remote URL / DDNS Domain / ISP IP") },
                        placeholder = { Text("e.g. http://mycudy.duckdns.org:8080/cgi-bin/luci/") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 ISP & Remote Access Tip:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryCyan)
                            )
                            Text(
                                text = "For routers under the same ISP or WAN subnet, enter the router's WAN IP and remote management port (e.g. 10.x.x.x:8080 or public IP:port). Enable Dynamic DNS or Remote Web Management in the router settings.",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val formatted = remoteInput.trim()
                        val finalUrl = if (formatted.isNotBlank() && !formatted.startsWith("http://") && !formatted.startsWith("https://")) {
                            "http://$formatted"
                        } else {
                            formatted
                        }
                        onSaveRouter(
                            router.name,
                            router.brand,
                            router.ipOrHostname,
                            router.port,
                            router.username,
                            router.password,
                            finalUrl.isNotBlank(),
                            finalUrl,
                            router.locationTag,
                            router.notes
                        )
                        routerToConfigureRemote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { routerToConfigureRemote = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceNavy
        )
    }
}

@Composable
fun RouterItemCard(
    router: RouterEntity,
    onOpenLocal: () -> Unit,
    onOpenRemote: () -> Unit,
    onConfigureRemote: () -> Unit,
    onDelete: () -> Unit,
    onCopyLink: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (router.isRemoteBound) PrimaryCyan.copy(alpha = 0.5f) else BorderSubtle, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(if (router.isRemoteBound) PrimaryCyan.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (router.isRemoteBound) Icons.Default.CloudDone else Icons.Default.Router,
                            contentDescription = "Router",
                            tint = if (router.isRemoteBound) PrimaryCyan else PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = router.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${router.brand} • ${router.locationTag}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                StatusPill(
                    text = if (router.isRemoteBound) "Remote Ready" else "Local Only",
                    color = if (router.isRemoteBound) AccentGreen else PrimaryCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Address Details Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Local: ${router.ipOrHostname}:${router.port}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                            if (router.isRemoteBound && router.remoteDnsUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Remote: ${router.remoteDnsUrl}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = AccentGreen
                                    ),
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "User: ${router.username} | Pass: ••••••",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }

                        IconButton(onClick = onCopyLink, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Access Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }

                // Local LAN Launch
                OutlinedButton(
                    onClick = onOpenLocal,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Router, contentDescription = "Local", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Local WiFi", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // Remote Cloud / Anywhere Launch
                Button(
                    onClick = {
                        if (router.isRemoteBound && router.remoteDnsUrl.isNotBlank()) {
                            onOpenRemote()
                        } else {
                            onConfigureRemote()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (router.isRemoteBound && router.remoteDnsUrl.isNotBlank()) AccentGreen else PrimaryCyan,
                        contentColor = Color(0xFF00222B)
                    )
                ) {
                    Icon(Icons.Default.CloudDone, contentDescription = "Remote", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (router.isRemoteBound && router.remoteDnsUrl.isNotBlank()) "Anywhere (Remote)" else "Set Remote",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRouterDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        brand: String,
        ip: String,
        port: Int,
        user: String,
        pass: String,
        isRemote: Boolean,
        remoteUrl: String,
        tag: String,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("Cudy") }
    var ip by remember { mutableStateOf("192.168.10.1") }
    var port by remember { mutableStateOf("80") }
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin") }
    var isRemoteBound by remember { mutableStateOf(false) }
    var remoteUrl by remember { mutableStateOf("") }
    var locationTag by remember { mutableStateOf("Home") }
    var notes by remember { mutableStateOf("") }

    var brandMenuExpanded by remember { mutableStateOf(false) }
    val brands = remember { RouterPresets.BRAND_LIST.map { it.brand } + listOf("Custom / Other Router") }
    val tags = listOf("Home", "Office", "Shop", "Village", "Factory", "Branch")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Bind & Save Router",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Router Name (e.g. Cudy WR1300 / Home WiFi)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = brandMenuExpanded,
                        onExpandedChange = { brandMenuExpanded = !brandMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedBrand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Router Brand") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = brandMenuExpanded,
                            onDismissRequest = { brandMenuExpanded = false }
                        ) {
                            brands.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b) },
                                    onClick = {
                                        selectedBrand = b
                                        val preset = RouterPresets.findPresetByBrand(b)
                                        if (preset != null) {
                                            ip = preset.defaultGateway
                                            username = preset.defaultUser
                                            password = preset.defaultPass
                                            if (preset.brand == "Cudy") {
                                                remoteUrl = "http://192.168.10.1/cgi-bin/luci/"
                                                name = if (name.isBlank()) "Cudy Router" else name
                                            }
                                        }
                                        brandMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ip,
                            onValueChange = { ip = it },
                            label = { Text("IP / Hostname") },
                            modifier = Modifier.weight(2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        OutlinedTextField(
                            value = port,
                            onValueChange = { port = it },
                            label = { Text("Port") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Enable Remote Cloud Binding",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Access from anywhere worldwide",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                        Switch(
                            checked = isRemoteBound,
                            onCheckedChange = { isRemoteBound = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryCyan, checkedTrackColor = SurfaceElevated)
                        )
                    }
                }

                if (isRemoteBound) {
                    item {
                        OutlinedTextField(
                            value = remoteUrl,
                            onValueChange = { remoteUrl = it },
                            label = { Text("Remote DDNS / Cloud URL") },
                            placeholder = { Text("https://myrouter.tplinkdns.com:8443") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                    }
                }

                item {
                    Text(
                        text = "Location Tag:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tags.take(4).forEach { t ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (locationTag == t) PrimaryCyan.copy(alpha = 0.2f) else SurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (locationTag == t) PrimaryCyan else BorderSubtle),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { locationTag = t }
                            ) {
                                Text(
                                    text = t,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (locationTag == t) PrimaryCyan else TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val portNum = port.toIntOrNull() ?: 80
                    onConfirm(
                        name.ifBlank { "$selectedBrand Router" },
                        selectedBrand,
                        ip,
                        portNum,
                        username,
                        password,
                        isRemoteBound,
                        remoteUrl,
                        locationTag,
                        notes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
            ) {
                Text("Save & Bind", fontWeight = FontWeight.Bold)
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

private fun copyText(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Router Link", text)
    clipboard?.setPrimaryClip(clip)
}

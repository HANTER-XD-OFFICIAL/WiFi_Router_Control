package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.DeveloperSupportBottomSheet
import com.example.ui.components.TopNetworkAppBar
import com.example.ui.screens.ConnectedDevicesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticsToolsScreen
import com.example.ui.screens.RouterAdminWebScreen
import com.example.ui.screens.RouterBindingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.RouterViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: RouterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: RouterViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // App Loading / Splash Screen State
    var isAppLoading by remember { mutableStateOf(true) }
    var showDeveloperSupportSheet by remember { mutableStateOf(false) }

    // Collect States
    val wifiState by viewModel.wifiState.collectAsState()
    val allRouters by viewModel.allRouters.collectAsState()
    val boundRouters by viewModel.boundRouters.collectAsState()
    val deviceNicknames by viewModel.deviceNicknames.collectAsState()
    val speedHistory by viewModel.speedHistory.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val speedState by viewModel.speedTestState.collectAsState()
    val scannedPorts by viewModel.scannedPorts.collectAsState()
    val isPortScanning by viewModel.isPortScanning.collectAsState()
    val dnsList by viewModel.dnsList.collectAsState()
    val isDnsBenchmarking by viewModel.isDnsBenchmarking.collectAsState()
    val activeAdminUrl by viewModel.activeAdminUrl.collectAsState()
    val activeAdminUser by viewModel.activeAdminUser.collectAsState()
    val activeAdminPass by viewModel.activeAdminPass.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    // Navigation Tab Index (0: Dashboard, 1: Binding, 2: Devices, 3: Tools, 4: Admin WebView)
    var currentScreen by remember { mutableIntStateOf(0) }
    var toolsInitialTab by remember { mutableIntStateOf(0) }

    // Toast / Snackbar feedback trigger
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUiMessage()
        }
    }

    // Runtime Permission Request for WiFi / Subnet scanning
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.refreshWifiState()
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        val missing = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    Crossfade(
        targetState = isAppLoading,
        label = "AppLoadingTransition"
    ) { loading ->
        if (loading) {
            SplashScreen(
                onFinished = { isAppLoading = false }
            )
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkNavyBg),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (currentScreen != 4) { // Don't show top bar on full-screen Admin WebView
                        TopNetworkAppBar(
                            title = "WiFi Router Manager",
                            ssid = wifiState.ssid,
                            isConnected = wifiState.isWifiConnected,
                            onOpenSupport = { showDeveloperSupportSheet = true },
                            onRefresh = {
                                viewModel.refreshWifiState()
                            }
                        )
                    }
                },
                bottomBar = {
                    if (currentScreen != 4) {
                        NavigationBar(
                            containerColor = SurfaceNavy,
                            contentColor = PrimaryCyan,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == 0,
                                onClick = { currentScreen = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryCyan,
                                    selectedTextColor = PrimaryCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = PrimaryCyan.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_home")
                            )

                            NavigationBarItem(
                                selected = currentScreen == 1,
                                onClick = { currentScreen = 1 },
                                icon = { Icon(Icons.Default.CloudDone, contentDescription = "Binding") },
                                label = { Text("Binding", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryCyan,
                                    selectedTextColor = PrimaryCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = PrimaryCyan.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_binding")
                            )

                            NavigationBarItem(
                                selected = currentScreen == 2,
                                onClick = { currentScreen = 2 },
                                icon = { Icon(Icons.Default.Devices, contentDescription = "Devices") },
                                label = { Text("Devices", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryCyan,
                                    selectedTextColor = PrimaryCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = PrimaryCyan.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_devices")
                            )

                            NavigationBarItem(
                                selected = currentScreen == 3,
                                onClick = {
                                    toolsInitialTab = 0
                                    currentScreen = 3
                                },
                                icon = { Icon(Icons.Default.Build, contentDescription = "Tools") },
                                label = { Text("Tools", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryCyan,
                                    selectedTextColor = PrimaryCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = PrimaryCyan.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_tools")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(DarkNavyBg)
                ) {
                    when (currentScreen) {
                        0 -> DashboardScreen(
                            wifiState = wifiState,
                            boundRouters = boundRouters,
                            onOpenAdmin = { url, user, pass ->
                                viewModel.openRouterAdmin(url, user, pass)
                                currentScreen = 4
                            },
                            onNavigateToBinding = { currentScreen = 1 },
                            onNavigateToDevices = { currentScreen = 2 },
                            onNavigateToTools = { tabIndex ->
                                toolsInitialTab = tabIndex
                                currentScreen = 3
                            },
                            onOpenDeveloperSupport = { showDeveloperSupportSheet = true }
                        )
                        1 -> RouterBindingScreen(
                            routers = allRouters,
                            onSaveRouter = { name, brand, ip, port, user, pass, isRemote, remoteUrl, tag, notes ->
                                viewModel.saveRouter(name, brand, ip, port, user, pass, isRemote, remoteUrl, tag, notes)
                            },
                            onDeleteRouter = { router ->
                                viewModel.deleteRouter(router)
                            },
                            onOpenAdmin = { url, user, pass ->
                                viewModel.openRouterAdmin(url, user, pass)
                                currentScreen = 4
                            },
                            onSendWol = { mac ->
                                viewModel.sendWol(mac)
                            }
                        )
                        2 -> ConnectedDevicesScreen(
                            scanProgress = scanProgress,
                            savedNicknames = deviceNicknames,
                            onStartScan = { viewModel.startLanScan() },
                            onStopScan = { viewModel.stopLanScan() },
                            onSaveNickname = { mac, ip, name, type, blocked ->
                                viewModel.saveDeviceNickname(mac, ip, name, type, blocked)
                            },
                            onSendWol = { mac -> viewModel.sendWol(mac) }
                        )
                        3 -> DiagnosticsToolsScreen(
                            initialTab = toolsInitialTab,
                            wifiState = wifiState,
                            speedState = speedState,
                            speedHistory = speedHistory,
                            scannedPorts = scannedPorts,
                            isPortScanning = isPortScanning,
                            dnsList = dnsList,
                            isDnsBenchmarking = isDnsBenchmarking,
                            onStartSpeedTest = { viewModel.startSpeedTest() },
                            onStartPortScan = { target -> viewModel.startPortScan(target) },
                            onStartDnsBenchmark = { viewModel.startDnsBenchmark() },
                            onSendWol = { mac -> viewModel.sendWol(mac) }
                        )
                        4 -> RouterAdminWebScreen(
                            initialUrl = activeAdminUrl,
                            adminUser = activeAdminUser,
                            adminPass = activeAdminPass,
                            onBack = { currentScreen = 0 }
                        )
                    }
                }

                // Developer Support Bottom Sheet
                if (showDeveloperSupportSheet) {
                    DeveloperSupportBottomSheet(
                        onDismiss = { showDeveloperSupportSheet = false }
                    )
                }
            }
        }
    }
}

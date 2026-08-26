package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DeviceNicknameEntity
import com.example.data.model.RouterEntity
import com.example.data.model.SpeedTestHistoryEntity
import com.example.data.repository.RouterRepository
import com.example.network.CurrentWifiState
import com.example.network.DnsBenchmarker
import com.example.network.DnsServerItem
import com.example.network.LanDevice
import com.example.network.LanScanner
import com.example.network.NetworkHelper
import com.example.network.PortInfo
import com.example.network.PortScannerEngine
import com.example.network.RouterBrandPreset
import com.example.network.RouterPresets
import com.example.network.ScanProgress
import com.example.network.SpeedStage
import com.example.network.SpeedTestEngine
import com.example.network.SpeedTestState
import com.example.network.WolHelper
import android.content.Context
import android.content.SharedPreferences
import com.example.ui.language.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouterViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences("router_app_prefs", Context.MODE_PRIVATE)
    private val repository: RouterRepository

    private val _currentLanguage = MutableStateFlow(
        AppLanguage.fromCode(prefs.getString("app_language", AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code)
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("app_language", language.code).apply()
    }

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RouterRepository(db.routerDao())
    }

    // State: Current WiFi
    private val _wifiState = MutableStateFlow(CurrentWifiState())
    val wifiState: StateFlow<CurrentWifiState> = _wifiState.asStateFlow()

    // State: Saved & Bound Routers from Room
    val allRouters: StateFlow<List<RouterEntity>> = repository.allRouters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val boundRouters: StateFlow<List<RouterEntity>> = repository.boundRouters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deviceNicknames: StateFlow<List<DeviceNicknameEntity>> = repository.allDeviceNicknames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val speedHistory: StateFlow<List<SpeedTestHistoryEntity>> = repository.speedHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // LAN Scanner State
    private val _scanProgress = MutableStateFlow(
        ScanProgress("Ready to scan local network", 0, emptyList(), isCompleted = false)
    )
    val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()
    private var scanJob: Job? = null

    // Speed Test State
    private val _speedTestState = MutableStateFlow(SpeedTestState())
    val speedTestState: StateFlow<SpeedTestState> = _speedTestState.asStateFlow()
    private var speedJob: Job? = null

    // Port Scanner State
    private val _scannedPorts = MutableStateFlow<List<PortInfo>>(emptyList())
    val scannedPorts: StateFlow<List<PortInfo>> = _scannedPorts.asStateFlow()
    private val _isPortScanning = MutableStateFlow(false)
    val isPortScanning: StateFlow<Boolean> = _isPortScanning.asStateFlow()

    // DNS Benchmarker State
    private val _dnsList = MutableStateFlow<List<DnsServerItem>>(DnsBenchmarker.POPULAR_DNS_PROVIDERS)
    val dnsList: StateFlow<List<DnsServerItem>> = _dnsList.asStateFlow()
    private val _isDnsBenchmarking = MutableStateFlow(false)
    val isDnsBenchmarking: StateFlow<Boolean> = _isDnsBenchmarking.asStateFlow()

    // Active Admin Webview Target URL & Auth
    private val _activeAdminUrl = MutableStateFlow("http://192.168.0.1")
    val activeAdminUrl: StateFlow<String> = _activeAdminUrl.asStateFlow()
    private val _activeAdminUser = MutableStateFlow("admin")
    val activeAdminUser: StateFlow<String> = _activeAdminUser.asStateFlow()
    private val _activeAdminPass = MutableStateFlow("admin")
    val activeAdminPass: StateFlow<String> = _activeAdminPass.asStateFlow()

    // App Language: "en" (English)
    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    // Toast / Message feedback
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        refreshWifiState()
        seedDefaultRouterIfEmpty()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun refreshWifiState() {
        viewModelScope.launch {
            val state = NetworkHelper.getWifiState(getApplication())
            _wifiState.value = state
            if (state.isWifiConnected) {
                // Test ping to gateway
                val ping = NetworkHelper.testPingLatency(state.gatewayIp, 80)
                _wifiState.value = state.copy(reachable = ping >= 0, gatewayPingMs = ping)
                _activeAdminUrl.value = "http://${state.gatewayIp}"
            }
        }
    }

    private fun seedDefaultRouterIfEmpty() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Check if gateway router exists
                val currentList = NetworkHelper.getWifiState(getApplication())
                val gw = if (currentList.isWifiConnected) currentList.gatewayIp else "192.168.0.1"
                val existing = repository.getRouterByIp(gw)
                if (existing == null) {
                    val defaultRouter = RouterEntity(
                        name = "Primary Home Router",
                        brand = RouterPresets.guessBrandByGateway(gw),
                        ipOrHostname = gw,
                        port = 80,
                        username = "admin",
                        password = "admin",
                        isRemoteBound = false,
                        locationTag = "Home",
                        notes = "Auto-detected local router gateway"
                    )
                    repository.saveRouter(defaultRouter)
                }

                // Ensure Cudy Router is also seeded for instant access
                val existingCudy = repository.getRouterByIp("192.168.10.1")
                if (existingCudy == null) {
                    val cudyRouter = RouterEntity(
                        name = "Cudy Router (LuCI)",
                        brand = "Cudy",
                        ipOrHostname = "192.168.10.1/cgi-bin/luci/",
                        port = 80,
                        username = "admin",
                        password = "admin",
                        isRemoteBound = true,
                        remoteDnsUrl = "http://192.168.10.1/cgi-bin/luci/",
                        locationTag = "Home",
                        notes = "Cudy OpenWrt / LuCI Management Console (http://192.168.10.1/cgi-bin/luci/)"
                    )
                    repository.saveRouter(cudyRouter)
                }
            }
        }
    }

    // Router CRUD & Binding
    fun saveRouter(
        name: String,
        brand: String,
        ipOrHostname: String,
        port: Int,
        user: String,
        pass: String,
        isRemoteBound: Boolean,
        remoteUrl: String,
        locationTag: String,
        notes: String
    ) {
        viewModelScope.launch {
            val router = RouterEntity(
                name = name.ifBlank { "$brand Router" },
                brand = brand,
                ipOrHostname = ipOrHostname.trim(),
                port = port,
                username = user,
                password = pass,
                isRemoteBound = isRemoteBound,
                remoteDnsUrl = remoteUrl.trim(),
                locationTag = locationTag,
                notes = notes,
                lastAccessedTime = System.currentTimeMillis()
            )
            repository.saveRouter(router)
            _uiMessage.value = "Router saved & bound successfully!"
        }
    }

    fun deleteRouter(router: RouterEntity) {
        viewModelScope.launch {
            repository.deleteRouter(router)
            _uiMessage.value = "Router deleted"
        }
    }

    fun openRouterAdmin(url: String, user: String = "admin", pass: String = "admin") {
        var cleanUrl = url.trim()
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "http://$cleanUrl"
        }
        _activeAdminUrl.value = cleanUrl

        // If user didn't specify custom pass, check local database for saved password for this host
        viewModelScope.launch(Dispatchers.IO) {
            val host = extractHostFromUrl(cleanUrl)
            val prefs = getApplication<Application>().getSharedPreferences("router_credentials", android.content.Context.MODE_PRIVATE)
            val cachedPass = prefs.getString("pass_$host", null)
            val cachedUser = prefs.getString("user_$host", null)

            if (!cachedPass.isNullOrBlank()) {
                _activeAdminUser.value = cachedUser ?: user
                _activeAdminPass.value = cachedPass
            } else {
                val dbRouter = repository.getRouterByIp(host)
                if (dbRouter != null && dbRouter.password.isNotBlank()) {
                    _activeAdminUser.value = dbRouter.username
                    _activeAdminPass.value = dbRouter.password
                } else {
                    _activeAdminUser.value = user
                    _activeAdminPass.value = pass
                }
            }
        }
    }

    /**
     * Persist router credentials permanently so user never has to enter password again.
     */
    fun saveCredentialsForRouter(urlOrHost: String, user: String, pass: String) {
        if (pass.isBlank()) return
        val host = extractHostFromUrl(urlOrHost)
        val context = getApplication<Application>()

        // 1. Save synchronously to SharedPreferences for instant retrieval
        val prefs = context.getSharedPreferences("router_credentials", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putString("user_$host", user.ifBlank { "admin" })
            .putString("pass_$host", pass)
            .apply()

        _activeAdminUser.value = user.ifBlank { "admin" }
        _activeAdminPass.value = pass

        // 2. Save / Update in Room Database
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getRouterByIp(host)
            if (existing != null) {
                repository.updateRouter(
                    existing.copy(
                        username = user.ifBlank { existing.username },
                        password = pass,
                        lastAccessedTime = System.currentTimeMillis()
                    )
                )
            } else {
                val brand = RouterPresets.guessBrandByGateway(host)
                val newRouter = RouterEntity(
                    name = "$brand Router ($host)",
                    brand = brand,
                    ipOrHostname = host,
                    username = user.ifBlank { "admin" },
                    password = pass,
                    isRemoteBound = !host.startsWith("192.168.") && !host.startsWith("10.") && !host.startsWith("172."),
                    remoteDnsUrl = if (!host.startsWith("192.168.")) "http://$host" else "",
                    lastAccessedTime = System.currentTimeMillis()
                )
                repository.saveRouter(newRouter)
            }
        }
    }

    private fun extractHostFromUrl(url: String): String {
        return try {
            val clean = url.removePrefix("http://").removePrefix("https://")
            val hostPort = clean.split("/")[0]
            hostPort.split(":")[0]
        } catch (e: Exception) {
            url
        }
    }

    // Device Scanner
    fun startLanScan() {
        scanJob?.cancel()
        val gw = _wifiState.value.gatewayIp
        val local = _wifiState.value.localIp
        scanJob = viewModelScope.launch {
            LanScanner.scanSubnetFlow(gw, local).collect { progress ->
                _scanProgress.value = progress
            }
        }
    }

    fun stopLanScan() {
        scanJob?.cancel()
        _scanProgress.value = _scanProgress.value.copy(
            status = "Scan stopped",
            isCompleted = true
        )
    }

    fun saveDeviceNickname(mac: String, ip: String, name: String, type: String, isBlocked: Boolean) {
        viewModelScope.launch {
            val entity = DeviceNicknameEntity(
                macAddress = mac,
                ipAddress = ip,
                customName = name,
                deviceType = type,
                isBlocked = isBlocked
            )
            repository.saveDeviceNickname(entity)
            _uiMessage.value = "Device details updated!"
        }
    }

    // Speed Test
    fun startSpeedTest() {
        speedJob?.cancel()
        val gw = _wifiState.value.gatewayIp
        speedJob = viewModelScope.launch {
            SpeedTestEngine.runSpeedTest(gw).collect { state ->
                _speedTestState.value = state
                if (state.stage == SpeedStage.FINISHED) {
                    // Record in Room
                    val history = SpeedTestHistoryEntity(
                        pingMs = state.pingMs,
                        jitterMs = state.jitterMs,
                        downloadMbps = state.finalDownloadMbps,
                        ssid = _wifiState.value.ssid,
                        gatewayIp = _wifiState.value.gatewayIp
                    )
                    repository.insertSpeedTest(history)
                }
            }
        }
    }

    // Port Scanner
    fun startPortScan(targetIp: String = _wifiState.value.gatewayIp) {
        viewModelScope.launch {
            _isPortScanning.value = true
            _scannedPorts.value = emptyList()
            PortScannerEngine.scanRouterPorts(targetIp).collect { ports ->
                _scannedPorts.value = ports
            }
            _isPortScanning.value = false
        }
    }

    // DNS Benchmark
    fun startDnsBenchmark() {
        viewModelScope.launch {
            _isDnsBenchmarking.value = true
            DnsBenchmarker.benchmarkDnsList().collect { list ->
                _dnsList.value = list
            }
            _isDnsBenchmarking.value = false
        }
    }

    // Wake On LAN
    fun sendWol(mac: String) {
        viewModelScope.launch {
            val result = WolHelper.sendWakeOnLan(mac)
            result.onSuccess { msg ->
                _uiMessage.value = msg
            }.onFailure { err ->
                _uiMessage.value = "WOL Failed: ${err.localizedMessage}"
            }
        }
    }
}

package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * JS Interface for capturing and auto-saving router credentials permanently
 */
class RouterAuthBridge(
    private val onCredentialsCaptured: (user: String, pass: String) -> Unit
) {
    @JavascriptInterface
    fun onLoginDetected(user: String, pass: String) {
        if (pass.isNotBlank()) {
            onCredentialsCaptured(user, pass)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RouterAdminWebScreen(
    initialUrl: String,
    adminUser: String,
    adminPass: String,
    onBack: () -> Unit,
    onSaveCredentials: ((url: String, user: String, pass: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentUrl by remember { mutableStateOf(initialUrl) }
    var inputUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("Router Admin Console") }
    var isLoading by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableIntStateOf(0) }
    var isDesktopMode by remember { mutableStateOf(true) }
    var isSslSecure by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showAddressEditor by remember { mutableStateOf(false) }

    // Persistent Credentials State
    var currentUser by remember { mutableStateOf(adminUser) }
    var currentPass by remember { mutableStateOf(adminPass) }
    var showCredentialsDialog by remember { mutableStateOf(false) }

    var hasLoadError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showRemoteGuideDialog by remember { mutableStateOf(false) }

    val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    val mobileUserAgent = WebSettings.getDefaultUserAgent(context)

    // Helper to perform multi-stage JS injection so SPA forms (LuCI, TP-Link, Vue) fill automatically
    fun triggerAutoFillInjection(webView: WebView?, user: String, pass: String) {
        if (webView == null || pass.isBlank()) return
        injectRouterCredentials(webView, user, pass)
        coroutineScope.launch {
            delay(400)
            injectRouterCredentials(webView, user, pass)
            delay(1200)
            injectRouterCredentials(webView, user, pass)
            delay(2500)
            injectRouterCredentials(webView, user, pass)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
    ) {
        // Top Toolbar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = SurfaceNavy,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // URL Display Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceCardNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showAddressEditor = !showAddressEditor }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (currentUrl.startsWith("https://")) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = "Security",
                                    tint = if (currentUrl.startsWith("https://")) AccentGreen else AccentOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentUrl.removePrefix("http://").removePrefix("https://"),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Desktop Mode Toggle
                        IconButton(
                            onClick = {
                                isDesktopMode = !isDesktopMode
                                webViewInstance?.settings?.userAgentString = if (isDesktopMode) desktopUserAgent else mobileUserAgent
                                webViewInstance?.reload()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isDesktopMode) PrimaryCyan.copy(alpha = 0.2f) else SurfaceElevated)
                        ) {
                            Icon(
                                imageVector = if (isDesktopMode) Icons.Default.Computer else Icons.Default.PhoneAndroid,
                                contentDescription = "Mode",
                                tint = if (isDesktopMode) PrimaryCyan else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Auto-fill & Remember Credentials Button
                        IconButton(
                            onClick = {
                                triggerAutoFillInjection(webViewInstance, currentUser, currentPass)
                                Toast.makeText(context, "⚡ Credentials auto-filled!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryCyan.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = "Auto Fill",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Edit Credentials Button
                        IconButton(
                            onClick = { showCredentialsDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Saved Credentials",
                                tint = AccentGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Open in Chrome / External Browser
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Chrome", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Address Input Bar (Expandable)
                AnimatedVisibility(visible = showAddressEditor) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = { inputUrl = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = SurfaceCardNavy,
                                unfocusedContainerColor = SurfaceCardNavy
                            )
                        )

                        Button(
                            onClick = {
                                var formatted = inputUrl.trim()
                                if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
                                    formatted = "http://$formatted"
                                }
                                currentUrl = formatted
                                webViewInstance?.loadUrl(formatted)
                                showAddressEditor = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Go", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Persistent Auto-Login Active Banner
                if (currentPass.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Auto Login",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Auto-Login Active: Password permanently saved",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AccentGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1
                                )
                            }

                            TextButton(
                                onClick = {
                                    triggerAutoFillInjection(webViewInstance, currentUser, currentPass)
                                    Toast.makeText(context, "Injected successfully!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(
                                    text = "Auto-Fill",
                                    color = PrimaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Loading Progress Bar
                if (isLoading) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { loadingProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = PrimaryCyan,
                        trackColor = SurfaceElevated
                    )
                }
            }
        }

        // Webview Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            setSupportZoom(true)
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            allowContentAccess = true
                            allowFileAccess = true
                            userAgentString = if (isDesktopMode) desktopUserAgent else mobileUserAgent
                        }

                        // Enable persistent cookies so session stays logged in indefinitely
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)

                        // Attach JavaScript Bridge to automatically detect & remember passwords
                        addJavascriptInterface(
                            RouterAuthBridge { capturedUser, capturedPass ->
                                currentUser = capturedUser
                                currentPass = capturedPass
                                onSaveCredentials?.invoke(currentUrl, capturedUser, capturedPass)
                                cookieManager.flush()
                            },
                            "AndroidRouterBridge"
                        )

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                hasLoadError = false
                                url?.let {
                                    currentUrl = it
                                    inputUrl = it
                                    isSslSecure = it.startsWith("https://")
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                url?.let {
                                    currentUrl = it
                                    inputUrl = it
                                }
                                pageTitle = view?.title ?: "Router Admin"

                                // Multi-stage JavaScript Auto-Fill & Auto-Capture listener attachment
                                triggerAutoFillInjection(view, currentUser, currentPass)
                                attachFormAutoSaveListener(view)
                                cookieManager.flush()
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    isLoading = false
                                    hasLoadError = true
                                    errorMessage = error?.description?.toString() ?: "Connection Timed Out / Unreachable"
                                }
                            }

                            @SuppressLint("WebViewClientOnReceivedSslError")
                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                // CRITICAL: Router admin pages often use self-signed certificates
                                handler?.proceed()
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                loadingProgress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (!title.isNullOrBlank()) pageTitle = title
                            }
                        }

                        loadUrl(currentUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Connection Error & Remote Failover Assistant Overlay
            if (hasLoadError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkNavyBg.copy(alpha = 0.95f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentOrange.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(AccentOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = "Warning",
                                    tint = AccentOrange,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Unable to Connect to Router",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "You may not be connected to this router's local Wi-Fi. Local gateway addresses (e.g. 192.168.10.1 or 192.168.0.1) only work within the local network. To access other routers across your ISP subnet or over mobile data, configure the Remote DDNS / WAN IP or Cloud URL.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Quick Alternatives
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val cudyUrl = "http://192.168.10.1/cgi-bin/luci/"
                                        currentUrl = cudyUrl
                                        inputUrl = cudyUrl
                                        hasLoadError = false
                                        webViewInstance?.loadUrl(cudyUrl)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Icon(Icons.Default.Router, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Try Cudy (192.168.10.1/luci)")
                                }

                                OutlinedButton(
                                    onClick = {
                                        showRemoteGuideDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan)
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Remote & ISP Access Guide", color = PrimaryCyan)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onBack,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Go Back", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        hasLoadError = false
                                        webViewInstance?.reload()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retry Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit Saved Credentials Dialog
        if (showCredentialsDialog) {
            var tempUser by remember { mutableStateOf(currentUser) }
            var tempPass by remember { mutableStateOf(currentPass) }
            var showPassText by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showCredentialsDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Router Credentials")
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Saving your credentials securely on your device enables automatic auto-login every time without needing to type passwords again.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 16.sp)
                        )

                        OutlinedTextField(
                            value = tempUser,
                            onValueChange = { tempUser = it },
                            label = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tempPass,
                            onValueChange = { tempPass = it },
                            label = { Text("Admin Password / PIN") },
                            singleLine = true,
                            visualTransformation = if (showPassText) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassText = !showPassText }) {
                                    Icon(
                                        imageVector = if (showPassText) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Pass",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            currentUser = tempUser
                            currentPass = tempPass
                            onSaveCredentials?.invoke(currentUrl, tempUser, tempPass)
                            triggerAutoFillInjection(webViewInstance, tempUser, tempPass)
                            showCredentialsDialog = false
                            Toast.makeText(context, "🔐 Credentials saved permanently!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save & Auto-Fill", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCredentialsDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = SurfaceNavy
            )
        }

        // Remote Access Setup Guide Dialog
        if (showRemoteGuideDialog) {
            AlertDialog(
                onDismissRequest = { showRemoteGuideDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remote & ISP-Wide Router Access Guide")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Access and manage routers remotely across your ISP network or mobile data:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🌐 Cudy Router / OpenWrt LuCI & Web Portals:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1. Connect to local Wi-Fi and open http://192.168.10.1/cgi-bin/luci/.\n2. In Network > Firewall > Traffic Rules / Remote Management, enable WAN access (e.g. port 8080/8443).\n3. Under Services > Dynamic DNS (DDNS), set up DuckDNS or No-IP (e.g. router.duckdns.org).\n4. In this app, save the WAN IP / DDNS link to manage it anytime!",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 17.sp)
                                )
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🔒 ISP Subnet & VPN Access (Recommended):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AccentGreen)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Under the same ISP, routers with Remote Web Management enabled on their WAN IP (e.g. 10.x.x.x:8080) or WireGuard / Tailscale mesh VPN can be directly accessed and controlled without needing to be on their local Wi-Fi.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 17.sp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showRemoteGuideDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = SurfaceNavy
            )
        }

        // Bottom Web Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfaceNavy,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { if (webViewInstance?.canGoBack() == true) webViewInstance?.goBack() },
                        enabled = webViewInstance?.canGoBack() == true
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = if (webViewInstance?.canGoBack() == true) PrimaryCyan else TextMuted)
                    }

                    IconButton(
                        onClick = { if (webViewInstance?.canGoForward() == true) webViewInstance?.goForward() },
                        enabled = webViewInstance?.canGoForward() == true
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward", tint = if (webViewInstance?.canGoForward() == true) PrimaryCyan else TextMuted)
                    }

                    IconButton(onClick = { webViewInstance?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = TextPrimary)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Zoom controls
                    IconButton(onClick = { webViewInstance?.zoomIn() }) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = TextSecondary)
                    }
                    IconButton(onClick = { webViewInstance?.zoomOut() }) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = TextSecondary)
                    }
                    IconButton(onClick = { copyText(context, currentUrl) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = TextSecondary)
                    }
                }
            }
        }
    }
}

/**
 * JavaScript injection helper for auto-filling router username & password fields across LuCI, TP-Link, Tenda, Netgear, ZTE, Huawei
 */
private fun injectRouterCredentials(webView: WebView?, user: String, pass: String) {
    if (webView == null || pass.isBlank()) return
    val sanitizedPass = pass.replace("\\", "\\\\").replace("\"", "\\\"")
    val sanitizedUser = user.replace("\\", "\\\\").replace("\"", "\\\"")

    val script = """
        (function() {
            try {
                var u = "$sanitizedUser";
                var p = "$sanitizedPass";
                
                // Target username inputs
                var userInputs = document.querySelectorAll('input[type="text"], input[name*="user"], input[name*="name"], input[id*="user"], input[id*="login"], input[name="luci_username"]');
                // Target password inputs
                var passInputs = document.querySelectorAll('input[type="password"], input[name*="pass"], input[name*="pwd"], input[id*="pass"], input[id*="pwd"], input[name="luci_password"], #focus_password, #pc-login-password, #login-password');
                
                if (userInputs.length > 0 && u !== "") {
                    for (var i = 0; i < userInputs.length; i++) {
                        userInputs[i].value = u;
                        userInputs[i].dispatchEvent(new Event('input', { bubbles: true }));
                        userInputs[i].dispatchEvent(new Event('change', { bubbles: true }));
                    }
                }
                
                if (passInputs.length > 0 && p !== "") {
                    for (var j = 0; j < passInputs.length; j++) {
                        passInputs[j].value = p;
                        passInputs[j].dispatchEvent(new Event('input', { bubbles: true }));
                        passInputs[j].dispatchEvent(new Event('change', { bubbles: true }));
                        passInputs[j].dispatchEvent(new KeyboardEvent('keyup', { bubbles: true, key: 'Enter' }));
                    }
                }
            } catch(e) {}
        })();
    """.trimIndent()

    webView.evaluateJavascript(script, null)
}

/**
 * Attaches real-time listeners so if user enters password, the app captures it and permanently remembers it!
 */
private fun attachFormAutoSaveListener(webView: WebView?) {
    if (webView == null) return
    val script = """
        (function() {
            try {
                function reportLogin() {
                    var userInp = document.querySelector('input[type="text"], input[name*="user"], input[name*="name"], input[id*="user"], input[id*="login"], input[name="luci_username"]');
                    var passInp = document.querySelector('input[type="password"], input[name*="pass"], input[name*="pwd"], input[id*="pass"], input[id*="pwd"], input[name="luci_password"], #focus_password, #pc-login-password, #login-password');
                    var u = userInp ? userInp.value : 'admin';
                    var p = passInp ? passInp.value : '';
                    if (p && window.AndroidRouterBridge) {
                        window.AndroidRouterBridge.onLoginDetected(u, p);
                    }
                }
                
                var forms = document.querySelectorAll('form');
                forms.forEach(function(f) {
                    f.addEventListener('submit', function() { reportLogin(); });
                });
                
                var buttons = document.querySelectorAll('button, input[type="submit"], input[type="button"], .cbi-button-apply, .btn, #login-btn, #sub');
                buttons.forEach(function(b) {
                    b.addEventListener('click', function() { reportLogin(); });
                });
                
                var passInputs = document.querySelectorAll('input[type="password"]');
                passInputs.forEach(function(p) {
                    p.addEventListener('change', function() { reportLogin(); });
                    p.addEventListener('blur', function() { reportLogin(); });
                });
            } catch(e) {}
        })();
    """.trimIndent()
    webView.evaluateJavascript(script, null)
}

private fun copyText(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("URL", text)
    clipboard?.setPrimaryClip(clip)
}

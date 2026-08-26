package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.viewinterop.AndroidView
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RouterAdminWebScreen(
    initialUrl: String,
    adminUser: String,
    adminPass: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var inputUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("Router Admin Console") }
    var isLoading by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableIntStateOf(0) }
    var isDesktopMode by remember { mutableStateOf(true) }
    var isSslSecure by remember { mutableStateOf(false) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showAddressEditor by remember { mutableStateOf(false) }

    var hasLoadError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showRemoteGuideDialog by remember { mutableStateOf(false) }

    val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    val mobileUserAgent = WebSettings.getDefaultUserAgent(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
    ) {
        // Top Toolbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
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

                        // Auto-fill Credentials Button
                        IconButton(
                            onClick = {
                                injectRouterCredentials(webViewInstance, adminUser, adminPass)
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

                        // Open in Chrome / Browser
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

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

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
                                // Attempt auto-fill on page load
                                injectRouterCredentials(view, adminUser, adminPass)
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
                                text = "রাউটারে কানেক্ট করা যাচ্ছে না",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "আপনি হয়তো এই রাউটারের লোকাল ওয়াইফাইতে নেই। লোকাল আইপি (যেমন 192.168.10.1 বা 192.168.0.1) শুধুমাত্র একই ওয়াইফাই নেটওয়ার্কে কাজ করে। অন্য যেকোনো নেটওয়ার্ক বা মোবাইল ডাটা থেকে ঢুকতে Remote DDNS / Cloud Domain বা Cudy Cloud URL ব্যবহার করুন।",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action: Enter Remote URL
                            OutlinedTextField(
                                value = inputUrl,
                                onValueChange = { inputUrl = it },
                                label = { Text("Remote DDNS / Cloud URL বা WAN IP") },
                                placeholder = { Text("e.g. http://mycudy.duckdns.org:8080/cgi-bin/luci/") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryCyan,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedContainerColor = SurfaceElevated,
                                    unfocusedContainerColor = SurfaceElevated
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showRemoteGuideDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryCyan),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = "Guide", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("সেটআপ গাইড", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        var target = inputUrl.trim()
                                        if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                            target = "http://$target"
                                        }
                                        currentUrl = target
                                        hasLoadError = false
                                        webViewInstance?.loadUrl(target)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00222B))
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কানেক্ট করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Remote Access Setup Guide Dialog
        if (showRemoteGuideDialog) {
            AlertDialog(
                onDismissRequest = { showRemoteGuideDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("দূরবর্তী নেটওয়ার্ক থেকে রাউটার কন্ট্রোল সেটআপ")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "যেকোনো দেশ বা যেকোনো নেটওয়ার্ক (মোবাইল ডাটা / বাইরের ওয়াইফাই) থেকে আপনার রাউটারে এক্সেস করার ধাপসমূহ:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🌐 Cudy Router / OpenWrt LuCI এর ক্ষেত্রে:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PrimaryCyan)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "১. প্রথমে রাউটারের ওয়াইফাইতে কানেক্ট করে http://192.168.10.1/cgi-bin/luci/ তে লগইন করুন।\n২. Network > Firewall > Traffic Rules-এ গিয়ে WAN-to-Device (Port 80/443 বা 8080) Allow করুন।\n৩. Services > Dynamic DNS (DDNS)-এ গিয়ে DuckDNS বা No-IP হোস্টনেইম যোগ করুন (যেমন: yourname.duckdns.org)।\n৪. এই অ্যাপে সেই হোস্টনেইম সেভ করে যেকোনো স্থান থেকে কন্ট্রোল করুন!",
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
                                    text = "🔒 VPN / Cudy Cloud পদ্ধতি (সবচেয়ে নিরাপদ):",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AccentGreen)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "রাউটারে Tailscale, WireGuard বা Cudy Cloud অ্যাকাউন্ট চালু করে রাখলে বিশ্বব্যাপী যেকোনো স্থান থেকে রিমোট কন্ট্রোল করা যায়।",
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
                        Text("বুঝেছি (Close)", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = SurfaceNavy
            )
        }

        // Bottom Web Navigation Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
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
 * JavaScript injection helper for auto-filling router username & password fields
 */
private fun injectRouterCredentials(webView: WebView?, user: String, pass: String) {
    if (webView == null || user.isBlank()) return
    val script = """
        (function() {
            try {
                var userInputs = document.querySelectorAll('input[type="text"], input[name*="user"], input[name*="name"], input[id*="user"], input[id*="login"]');
                var passInputs = document.querySelectorAll('input[type="password"], input[name*="pass"], input[id*="pass"], input[id*="pwd"]');
                
                if (userInputs.length > 0 && "$user" !== "") {
                    userInputs[0].value = "$user";
                    userInputs[0].dispatchEvent(new Event('input', { bubbles: true }));
                    userInputs[0].dispatchEvent(new Event('change', { bubbles: true }));
                }
                
                if (passInputs.length > 0 && "$pass" !== "") {
                    passInputs[0].value = "$pass";
                    passInputs[0].dispatchEvent(new Event('input', { bubbles: true }));
                    passInputs[0].dispatchEvent(new Event('change', { bubbles: true }));
                }
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

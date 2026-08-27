# 🌐 WiFi Router Control & Network Manager (v2.5.0)

[![Release](https://img.shields.io/badge/Release-v2.5.0--Debug-blue.svg?style=for-the-badge&logo=android)](https://github.com/HANTER-XD-OFFICIAL/WiFi_Router_Control/releases/tag/v2.5.0RouterControl-Debug)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Room%20DB-brightgreen.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![License](https://img.shields.io/badge/License-MIT-orange.svg?style=for-the-badge)](LICENSE)

An enterprise-grade, high-performance Android application built with modern Kotlin and Jetpack Compose for complete local and remote WiFi router management, deep LAN hardware scanning, multi-tier speed diagnostics, port auditing, and network telemetry.

---

## 📥 Direct APK Download

Download the latest pre-compiled debug APK release directly from GitHub:

[![Download APK Button](https://img.shields.io/badge/⚡_DOWNLOAD_LATEST_APK-v2.5.0_Debug-success?style=for-the-badge&logo=android&logoColor=white)](https://github.com/HANTER-XD-OFFICIAL/WiFi_Router_Control/releases/tag/v2.5.0RouterControl-Debug)

> **Direct Release Link:** [https://github.com/HANTER-XD-OFFICIAL/WiFi_Router_Control/releases/tag/v2.5.0RouterControl-Debug](https://github.com/HANTER-XD-OFFICIAL/WiFi_Router_Control/releases/tag/v2.5.0RouterControl-Debug)

---

## 🚀 Key Features & Capabilities

### 1. 🎛️ Intelligent Router Detection & Admin Web Console
- **Automatic Gateway Discovery**: Auto-detects default gateway IP (`192.168.0.1`, `192.168.1.1`, `192.168.10.1`, `10.0.0.1`, etc.).
- **Hardware Vendor Fingerprinting**: Recognizes brands including TP-Link, Cudy, Tenda, Netgear, D-Link, MikroTik, Asus, Huawei, Xiaomi, and OpenWrt/LuCI.
- **Embedded Security-Hardened WebView**: 
  - Dedicated router login console with saved credentials auto-fill.
  - Desktop-mode toggle, cache-clearing, zoom controls, and SSL bypass options for self-signed router admin panels.
  - Quick launch buttons for Direct Cudy LuCI (`192.168.10.1/cgi-bin/luci/`), TP-Link Admin, and custom gateway portals.

### 2. ☁️ Multi-Router Cloud & Remote Binding
- **Unlimited Router Binding**: Save and manage multiple office, home, and remote routers in one synchronized vault.
- **Remote DDNS & Public IP Support**: Connect to remote router interfaces via custom ports and DDNS hostnames.
- **Persistent Room Database**: Encrypted local storage preserving custom labels, gateways, credentials, and access histories.

### 3. 🔍 Deep Subnet LAN Device Scanner & Theft Detection
- **High-Concurrency Multi-Threaded Subnet Probe**: Scans the full `/24` subnet (1–254 IPs) in seconds.
- **Vendor MAC OUI Lookup**: Automatically resolves hardware manufacturers (Apple, Samsung, Intel, Espressif, Raspberry Pi, TP-Link, Xiaomi, etc.).
- **Interactive Nicknames & Notes**: Assign personalized names to family and IoT devices to spot unauthorized WiFi intruders.
- **Ping RTT & Port Availability**: Instant latency ping check for every active IP on the network.

### 4. ⚡ 45-Second Precision Speed & Throughput Engine
- **Sequential Multi-Phase Testing**:
  - **Phase 1 (5s)**: Multi-sample ping & jitter analysis directly to the active gateway and global tier-1 backbones.
  - **Phase 2 (20s)**: High-bandwidth sustained multi-stream **Download Speed Test** with live MB/s tracking.
  - **Phase 3 (20s)**: Multi-chunk binary payload **Upload Speed Test** (20s continuous).
  - **Phase 4**: Automated Room database logging of test history (DL, UL, Ping, Jitter, Duration, Timestamp).
- **Smooth Speedometer Gauge**: Custom Jetpack Compose Canvas arc needle and live waveform graph.

### 5. 🛠️ Comprehensive Network Diagnostics Suite
- **Wake-on-LAN (WoL)**: Broadcast magic packets (`FF:FF:FF:FF:FF:FF` + 16 MAC repetitions) to wake up remote PCs and NAS servers.
- **Port Scanner & Service Audit**: Audit common networking ports (FTP 21, SSH 22, Telnet 23, HTTP 80, HTTPS 443, RTSP 554, LuCI 8080, Router Web 8000, 8888).
- **Public DNS Benchmark Tool**: Test response latency across Google DNS (`8.8.8.8`), Cloudflare (`1.1.1.1`), Quad9 (`9.9.9.9`), OpenDNS (`208.67.222.222`), AdGuard, and Control D.
- **Live Ping Diagnostic Tool**: Ping any custom hostname or IP with real-time packet loss calculation.

### 6. 📱 Host Android Device Hardware Telemetry
- **Hardware & Model Detection**: Real-time detection of host phone model, manufacturer, SoC board, and hardware revisions.
- **OS & Flavor Insights**: Displays Android version, API Level, and OS release codenames (Android 15 Vanilla Ice Cream, Android 14 Upside Down Cake, etc.).
- **Live Memory & Battery Telemetry**: Total RAM, available free RAM, percentage in use, battery percentage, charging state, and system uptime.

### 7. 🌐 Multi-Language & Modern UI/UX
- **Bilingual Interface**: Full toggle support between **English** and **বাংলা (Bengali)**.
- **Material Design 3 (M3)**: Dynamic cyberpunk dark theme with glowing neon accents (Cyan `#00E5FF`, Blue `#2979FF`, Green `#00E676`).
- **Edge-to-Edge Fluidity**: Window insets compliant with smooth layout transitions and haptic feedback.

---

## 🛠️ Technology Stack & Architecture

- **Language**: Kotlin 100%
- **UI Toolkit**: Jetpack Compose (Material 3, Canvas, Navigation Compose)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Data Layer
- **Persistence**: Room Database (SQLite with Flow Reactive Streams)
- **Asynchronous Processing**: Kotlin Coroutines & Reactive StateFlow
- **Networking**: OkHttp3, Java Sockets, DatagramSocket (WoL)
- **Dependency Management**: Gradle Kotlin DSL (`build.gradle.kts`)

---

## 📦 How to Build & Run from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/HANTER-XD-OFFICIAL/WiFi_Router_Control.git
   ```
2. Open the project in **Android Studio Ladybug (or newer)**.
3. Sync Gradle dependencies:
   ```bash
   gradle :app:assembleDebug
   ```
4. Install on your Android device or streaming emulator:
   ```bash
   gradle installDebug
   ```

---

## 👨‍💻 Developer & Author

- **Developer**: [HANTER-XD-OFFICIAL](https://github.com/HANTER-XD-OFFICIAL)
- **App Name**: WiFi Router Control & Network Manager
- **Version**: `v2.5.0-Debug`
- **Release Tag**: [v2.5.0RouterControl-Debug](https://github.com/HANTER-XD-OFFICIAL/WiFi_Router_Control/releases/tag/v2.5.0RouterControl-Debug)

---

<p align="center">
  <b>Crafted with ❤️ for Network Administrators, Power Users, and Everyday Tech Enthusiasts.</b>
</p>

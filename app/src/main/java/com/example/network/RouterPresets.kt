package com.example.network

data class RouterBrandPreset(
    val brand: String,
    val defaultGateway: String,
    val defaultUser: String,
    val defaultPass: String,
    val alternateGateways: List<String> = emptyList(),
    val webPath: String = "/",
    val appBindSupport: String = "Supported (Cloud / DDNS / OpenWrt)",
    val cloudBindGuide: String,
    val popularModels: String = ""
)

object RouterPresets {
    val BRAND_LIST = listOf(
        RouterBrandPreset(
            brand = "Cudy",
            defaultGateway = "192.168.10.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("cudy.net", "192.168.10.1", "192.168.0.1"),
            webPath = "/cgi-bin/luci/",
            appBindSupport = "Cudy Cloud / LuCI OpenWrt Web / Cudy App / DDNS",
            cloudBindGuide = "1. Open Cudy LuCI web interface at http://192.168.10.1/cgi-bin/luci/ (or cudy.net).\n2. Under Advanced Settings > Administration, enable Remote Management or Cloud Binding.\n3. Configure Dynamic DNS (DDNS) or bind with Cudy Cloud to manage from anywhere in the world.",
            popularModels = "WR1300, WR3000, WR2100, M1800, LT500, LT18, X6, AX3000 OpenWrt/LuCI"
        ),
        RouterBrandPreset(
            brand = "TP-Link",
            defaultGateway = "192.168.0.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.1.1", "tplinkwifi.net", "192.168.0.254"),
            webPath = "/",
            appBindSupport = "TP-Link ID / Tether Cloud / No-IP DDNS",
            cloudBindGuide = "1. Log into router admin panel and register with your 'TP-Link ID'.\n2. Enable 'Dynamic DNS' or 'TP-Link Cloud' to bind a remote domain (e.g. xxx.tplinkdns.com).\n3. Enable 'Remote Management' under System Tools and specify port 8080 or 8443.",
            popularModels = "Archer C6, C20, AX10, AX50, WR841N, WR845N, Deco Mesh"
        ),
        RouterBrandPreset(
            brand = "Tenda",
            defaultGateway = "192.168.0.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("tendawifi.com", "192.168.1.1"),
            webPath = "/login.html",
            appBindSupport = "Tenda WiFi Cloud / DynDNS / 88ip",
            cloudBindGuide = "1. Go to Tenda Web Admin > Administration > Remote Web Management and enable it.\n2. Bind with your Tenda Cloud account or configure Dynamic DNS (No-IP / DynDNS).\n3. Access and manage your router remotely from anywhere using your DDNS domain or WAN IP.",
            popularModels = "F3, F6, AC10, AC23, TX9 Pro, Nova Mesh"
        ),
        RouterBrandPreset(
            brand = "Xiaomi (Mi WiFi / Redmi)",
            defaultGateway = "192.168.31.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("miwifi.com"),
            webPath = "/cgi-bin/luci/web",
            appBindSupport = "Mi WiFi Cloud Account / DDNS",
            cloudBindGuide = "1. Go to Mi WiFi settings and link your Mi Account.\n2. Under Advanced Settings, configure Port Forwarding and enable Remote Web Access.\n3. Manage your router from external networks via your bound cloud domain.",
            popularModels = "Mi Router 4C, 4A Gigabit, AX3000, AX6000, Redmi AX6, AX3200"
        ),
        RouterBrandPreset(
            brand = "Netgear",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "password",
            alternateGateways = listOf("routerlogin.net", "192.168.0.1"),
            webPath = "/",
            appBindSupport = "Netgear Anywhere Access / NETGEAR DDNS",
            cloudBindGuide = "1. Go to Advanced Settings > Remote Management and enable it.\n2. Set up NETGEAR Dynamic DNS to create a free hostname and bind it in this app.",
            popularModels = "Nighthawk R7000, RAX50, Orbi Mesh, WNR614, XR1000"
        ),
        RouterBrandPreset(
            brand = "MikroTik (RouterOS)",
            defaultGateway = "192.168.88.1",
            defaultUser = "admin",
            defaultPass = "",
            alternateGateways = listOf("192.168.1.1"),
            webPath = "/webfig",
            appBindSupport = "MikroTik Cloud IP DDNS / Winbox Web",
            cloudBindGuide = "1. In WebFig or Winbox, go to IP > Cloud and check 'DDNS Enabled'.\n2. Copy the generated DNS Name (e.g. xxx.sn.mynetname.net) and save it in this app under Remote Binding.",
            popularModels = "hEX (RB750Gr3), hAP ac2, hAP ax2, CCR Series, CRS Switches"
        ),
        RouterBrandPreset(
            brand = "Asus (ASUSWRT / Merlin)",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("router.asus.com", "192.168.50.1"),
            webPath = "/",
            appBindSupport = "ASUS DDNS (*.asuscomm.com) / AiCloud 2.0",
            cloudBindGuide = "1. In WAN > DDNS, register a free ASUS DDNS hostname.\n2. In Administration > System, toggle 'Enable Web Access from WAN' and set HTTPS port (8443).\n3. Access your Asus router securely from anywhere.",
            popularModels = "RT-AC68U, RT-AX55, RT-AX86U, TUF Gaming AX5400, ROG Rapture"
        ),
        RouterBrandPreset(
            brand = "Huawei (ONT / AX / HiLink)",
            defaultGateway = "192.168.100.1",
            defaultUser = "telecomadmin",
            defaultPass = "admintelecom",
            alternateGateways = listOf("192.168.1.1", "192.168.8.1", "192.168.18.1", "192.168.3.1"),
            webPath = "/",
            appBindSupport = "Huawei HiLink / Static WAN Remote Web",
            cloudBindGuide = "1. In Security > ACL / Remote Management, permit WAN HTTP/HTTPS remote access.\n2. Bind the router's public WAN IP or DDNS hostname in this app.",
            popularModels = "HG8145V5, HG8245H, WS5200, AX3 Pro, WiFi Mesh 3"
        ),
        RouterBrandPreset(
            brand = "D-Link",
            defaultGateway = "192.168.0.1",
            defaultUser = "admin",
            defaultPass = "",
            alternateGateways = listOf("192.168.1.1", "dlinkrouter.local"),
            webPath = "/",
            appBindSupport = "mydlink Cloud / D-Link DDNS",
            cloudBindGuide = "1. In the D-Link admin panel, navigate to Management > System Admin > Remote Management.\n2. Register the router with your mydlink cloud account or configure D-Link DDNS.",
            popularModels = "DIR-615, DIR-825, DIR-841, Eagle PRO AI, EXO Series"
        ),
        RouterBrandPreset(
            brand = "Mercusys",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("mwlogin.net", "192.168.0.1"),
            webPath = "/",
            appBindSupport = "Mercusys Cloud ID / DDNS",
            cloudBindGuide = "1. Enable Remote Management under Advanced Settings > System Tools.\n2. Configure No-IP or custom DDNS to manage from any location.",
            popularModels = "MW305R, AC12G, MR50G, MR70X, Halo Mesh H50G"
        ),
        RouterBrandPreset(
            brand = "ZTE",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.0.1", "192.168.33.1"),
            webPath = "/",
            appBindSupport = "ZTE Smart Cloud / DDNS Remote Web",
            cloudBindGuide = "1. Turn on Remote Management in Administration settings.\n2. Bind via WAN IP or DDNS host to configure remotely.",
            popularModels = "F660, F670L, ZXHN H168N, AX3000 Pro"
        ),
        RouterBrandPreset(
            brand = "Totolink",
            defaultGateway = "192.168.0.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("itotolink.net", "192.168.1.1"),
            webPath = "/",
            appBindSupport = "Totolink DDNS / Remote Web Management",
            cloudBindGuide = "1. Enable Remote Management in Management settings and assign a port (e.g. 8080).\n2. Set up the DDNS server and connect using the hostname.",
            popularModels = "N300RT, A3002RU, X5000R, T10 Mesh"
        ),
        RouterBrandPreset(
            brand = "Linksys (Belkin)",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("myrouter.local", "192.168.0.1"),
            webPath = "/",
            appBindSupport = "Linksys Smart Wi-Fi Cloud / DDNS",
            cloudBindGuide = "1. In Connectivity > Administration, enable Remote Access.\n2. Register at Linksys Smart Wi-Fi cloud portal to manage Velop and Smart routers worldwide.",
            popularModels = "Velop Tri-Band, Hydra Pro 6, MR8300, EA7500, WRT3200ACM"
        ),
        RouterBrandPreset(
            brand = "Cisco / Cisco Small Business",
            defaultGateway = "192.168.1.1",
            defaultUser = "cisco",
            defaultPass = "cisco",
            alternateGateways = listOf("10.0.0.1", "192.168.0.1"),
            webPath = "/",
            appBindSupport = "Cisco Business Dashboard / Remote WAN Access",
            cloudBindGuide = "1. In System Management > Administration, enable WAN Access on HTTPS (Port 443/8443).\n2. Set up DDNS or static IP for worldwide management.",
            popularModels = "RV340, RV260, RV160, CBS350, Meraki Go"
        ),
        RouterBrandPreset(
            brand = "Ubiquiti (UniFi / EdgeRouter)",
            defaultGateway = "192.168.1.1",
            defaultUser = "ubnt",
            defaultPass = "ubnt",
            alternateGateways = listOf("unifi.ui.com", "192.168.0.1"),
            webPath = "/",
            appBindSupport = "UniFi Cloud Remote Access / UI.com Portal",
            cloudBindGuide = "1. In UniFi Console / EdgeOS Settings, enable Remote Access with your Ubiquiti account.\n2. Access via your cloud domain or EdgeOS web GUI anywhere.",
            popularModels = "Dream Machine (UDM-Pro), EdgeRouter X, Cloud Gateway Ultra, AmpliFi"
        ),
        RouterBrandPreset(
            brand = "Synology (SRM)",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "synology",
            alternateGateways = listOf("router.synology.com"),
            webPath = ":8000",
            appBindSupport = "Synology QuickConnect / Synology DDNS",
            cloudBindGuide = "1. In SRM > Network Center > Administration, enable QuickConnect or Synology DDNS.\n2. Open port 8000/8001 for SRM Web Desktop access.",
            popularModels = "RT2600ac, RT6600ax, WRX560"
        ),
        RouterBrandPreset(
            brand = "Keenetic",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "1234",
            alternateGateways = listOf("my.keenetic.net"),
            webPath = "/",
            appBindSupport = "KeenDNS (Cloud & Direct Access) / Keenetic App",
            cloudBindGuide = "1. In Keenetic Web Control > Network Rules > KeenDNS, register a free 3rd-level domain name (*.keenetic.link or *.keenetic.pro).\n2. Turn on Cloud Access mode to bypass private/NAT IP restrictions effortlessly.",
            popularModels = "Titan, Hero, Hopper, Speedster, Carrier, Extra"
        ),
        RouterBrandPreset(
            brand = "GL.iNet (Security & Travel)",
            defaultGateway = "192.168.8.1",
            defaultUser = "root",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.1.1"),
            webPath = "/cgi-bin/luci",
            appBindSupport = "GoodCloud / LuCI OpenWrt Web / WireGuard Cloud",
            cloudBindGuide = "1. Enable 'GoodCloud' service in Applications > Cloud Management.\n2. Or configure LuCI OpenWrt DDNS and WireGuard VPN server to control remotely.",
            popularModels = "Flint 2 (GL-MT6000), Beryl AX (GL-MT3000), Slate AX, Opal, Spitz 4G/5G"
        ),
        RouterBrandPreset(
            brand = "AVM FRITZ!Box",
            defaultGateway = "192.168.178.1",
            defaultUser = "admin",
            defaultPass = "",
            alternateGateways = listOf("fritz.box"),
            webPath = "/",
            appBindSupport = "MyFRITZ! Cloud Direct Access",
            cloudBindGuide = "1. In FRITZ!Box Web UI > Internet > MyFRITZ! Account, register your free MyFRITZ! address.\n2. Enable HTTPS Remote Access to securely control the router over internet.",
            popularModels = "FRITZ!Box 7590 AX, 6690 Cable, 5590 Fiber, 4060"
        ),
        RouterBrandPreset(
            brand = "DrayTek (Vigor Series)",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.2.1"),
            webPath = "/",
            appBindSupport = "DrayTek VigorACS Cloud / DrayDDNS",
            cloudBindGuide = "1. In System Maintenance > Management, check 'Allow management from the Internet'.\n2. Configure DrayDDNS for instant remote management.",
            popularModels = "Vigor 2927, Vigor 2865, Vigor 2962, Vigor 3910"
        ),
        RouterBrandPreset(
            brand = "Zyxel",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "1234",
            alternateGateways = listOf("192.168.212.1", "192.168.0.1"),
            webPath = "/",
            appBindSupport = "Nebula Cloud Center / MyZyxel DDNS",
            cloudBindGuide = "1. Register device with Zyxel Nebula Cloud or enable WAN remote management.\n2. Access via your assigned DDNS hostname.",
            popularModels = "Armor G5, NBG7510, USG FLEX, DX3301"
        ),
        RouterBrandPreset(
            brand = "FiberHome ONT",
            defaultGateway = "192.168.1.1",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.100.1"),
            webPath = "/",
            appBindSupport = "Static WAN Remote Web / DDNS",
            cloudBindGuide = "1. In Security > Remote Access Control, add HTTP/HTTPS rule for WAN.\n2. Bind public IP or DDNS host in app.",
            popularModels = "AN5506-04, HG6821M, HG6245D"
        ),
        RouterBrandPreset(
            brand = "Nokia ONT / FastMile",
            defaultGateway = "192.168.1.254",
            defaultUser = "admin",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.1.1"),
            webPath = "/",
            appBindSupport = "Nokia WiFi Cloud / Web Remote",
            cloudBindGuide = "1. In Nokia Web Console > Security > Remote Management, enable WAN access.\n2. Configure DDNS for remote access.",
            popularModels = "G-2425G-A, G-140W-ME, FastMile 5G Gateway, Beacon 1/2"
        ),
        RouterBrandPreset(
            brand = "OpenWrt / DD-WRT / Tomato",
            defaultGateway = "192.168.1.1",
            defaultUser = "root",
            defaultPass = "admin",
            alternateGateways = listOf("192.168.0.1", "192.168.10.1"),
            webPath = "/cgi-bin/luci",
            appBindSupport = "LuCI Web / SSH Remote / Dynamic DNS (ddns-scripts)",
            cloudBindGuide = "1. Configure Dynamic DNS in LuCI under Services > Dynamic DNS.\n2. In Network > Firewall, add an allow rule for incoming WAN traffic on ports 80/443.\n3. Enjoy full custom remote cloud control.",
            popularModels = "Custom Firmware OpenWrt 23.05, DD-WRT, Gargoyle, Tomato"
        )
    )

    fun findPresetByBrand(brand: String): RouterBrandPreset? {
        return BRAND_LIST.firstOrNull { it.brand.equals(brand, ignoreCase = true) || it.brand.startsWith(brand, ignoreCase = true) }
    }

    fun guessBrandByGateway(gateway: String): String {
        return when (gateway.trim()) {
            "192.168.10.1" -> "Cudy"
            "192.168.31.1" -> "Xiaomi"
            "192.168.88.1" -> "MikroTik"
            "192.168.100.1", "192.168.18.1" -> "Huawei"
            "192.168.8.1" -> "GL.iNet / Huawei"
            "192.168.178.1" -> "AVM FRITZ!Box"
            "192.168.50.1" -> "Asus"
            "192.168.1.254" -> "Nokia"
            "192.168.0.1" -> "TP-Link / Tenda / D-Link"
            "192.168.1.1" -> "Cisco / Asus / Mercusys / ZTE / OpenWrt"
            else -> "Global Router Gateway"
        }
    }
}

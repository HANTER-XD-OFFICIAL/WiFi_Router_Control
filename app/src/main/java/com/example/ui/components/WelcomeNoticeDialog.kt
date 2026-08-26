package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.DialogProperties
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

private const val DEV_NAME = "MD RASEL"
private const val DEV_FACEBOOK_URL = "https://www.facebook.com/md.rasel.7.8.2.3.4"

/**
 * Rich Welcome & Overview Dialog shown on app launch.
 * Explains capabilities, auto-login features, cloud remote access, security rules,
 * and direct links to Developer's Facebook Profile / Community.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeNoticeDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .padding(vertical = 24.dp),
        content = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceNavy,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f)),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header with Icon and Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(PrimaryCyan, PrimaryBlue)
                                        )
                                    )
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(DarkNavyBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Router,
                                    contentDescription = "Router Manager",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "WiFi Router Manager",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "স্মার্ট ওয়াইফাই ও রাউটার কন্ট্রোল",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PrimaryCyan,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Scrollable Information Content
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Welcome Banner
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceCardNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AccentGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "স্বাগতম! (Welcome to the App)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "আমাদের ওয়াইফাই ও রাউটার ম্যানেজমেন্ট সিস্টেমে আপনাকে স্বাগতম! অ্যাপটি ব্যবহারের মাধ্যমে আপনার যেকোনো ব্র্যান্ডের রাউটার পরিচালনা করা এখন আরও সহজ ও সুরক্ষিত।",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 19.sp
                                    )
                                )
                            }
                        }

                        // Developer Profile Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Developer",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "ডেভেলপার: $DEV_NAME",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "Lead Software & Network Architect",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }
                            }
                        }

                        // Section 1: অ্যাপের সেরা সুবিধাসমূহ (What can be done)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "অ্যাপের মূল সুবিধাসমূহ (Features):",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGreen
                                    )
                                )
                            }

                            FeatureBullet(
                                icon = Icons.Default.Key,
                                title = "১. পাসওয়ার্ডলেস অটো-লগইন (Auto-Login):",
                                desc = "একবার রাউটারে অ্যাডমিন পাসওয়ার্ড দিয়ে ঢুকলে অ্যাপটি তা মনে রাখবে। পরবর্তীতে পাসওয়ার্ড ছাড়াই সরাসরি অ্যাডমিন প্যানেল চালু হবে।"
                            )

                            FeatureBullet(
                                icon = Icons.Default.CloudDone,
                                title = "২. রিমোট ক্লাউড এক্সেস (Anywhere Control):",
                                desc = "ঘরের বাইরে থাকলে বা মোবাইল ডাটায় থাকলেও DDNS ডোমেইন ও ক্লাউড লিঙ্কের মাধ্যমে সরাসরি রাউটার কন্ট্রোল করা যায়।"
                            )

                            FeatureBullet(
                                icon = Icons.Default.Devices,
                                title = "৩. কানেক্টেড ডিভাইস স্ক্যানার:",
                                desc = "আপনার ওয়াইফাইতে কে কে কানেক্টেড আছে তা লাইভ দেখুন, নাম সেভ করুন এবং প্রয়োজনে ম্যানেজ করুন।"
                            )

                            FeatureBullet(
                                icon = Icons.Default.Speed,
                                title = "৪. ইন্টারনেট স্পিড ও পিং ডায়াগনস্টিকস:",
                                desc = "রিয়েল-টাইম গেটওয়ে পিং ল্যাটেন্সি ও স্পিড টেস্ট করে নেটওয়ার্কের মান যাচাই করুন।"
                            )

                            FeatureBullet(
                                icon = Icons.Default.Router,
                                title = "৫. সকল ব্র্যান্ড সাপোর্ট:",
                                desc = "Cudy (OpenWrt LuCI), TP-Link, Tenda, Netgear, Xiaomi, MikroTik, D-Link সহ সব রাউটার কনসোল নিয়ন্ত্রণের সুবিধা।"
                            )
                        }

                        // Section 2: ব্যবহারের সতর্কতা ও সীমাবদ্ধতা (Important Safety Guidelines)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = null,
                                    tint = AccentOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ব্যবহারের সতর্কতা ও নির্দেশিকা:",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentOrange
                                    )
                                )
                            }

                            SafetyBullet(
                                title = "• লোকাল আইপি এক্সেস:",
                                desc = "লোকাল গেটওয়ে (যেমন 192.168.10.1) শুধুমাত্র সংশ্লিষ্ট ওয়াইফাই নেটওয়ার্কে কানেক্টেড থাকা অবস্থায় কাজ করে।"
                            )

                            SafetyBullet(
                                title = "• রিমোট এক্সেস চালু:",
                                desc = "মোবাইল ডাটা থেকে এক্সেস করতে রাউটারে DDNS (DuckDNS/No-IP) বা WAN রিমোট পোর্ট এক্সেস অন থাকতে হবে।"
                            )

                            SafetyBullet(
                                title = "• রাউটার রিবুট ও ফার্মওয়্যার:",
                                desc = "ফার্মওয়্যার আপগ্রেড বা কনফিগ পরিবর্তনের সময় রাউটারের বিদ্যুৎ ও কানেকশন বিচ্ছিন্ন করবেন না।"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Bottom Action Buttons: Cancel and Join / Facebook Profile
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Cancel Button -> Opens Developer's Facebook Profile
                            OutlinedButton(
                                onClick = {
                                    openFacebookUrl(context)
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("welcome_btn_cancel"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            // Join / View Facebook Button
                            Button(
                                onClick = {
                                    openFacebookUrl(context)
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("welcome_btn_join"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1877F2),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Facebook",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Join / Facebook",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Continue into App
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("welcome_btn_continue"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryCyan,
                                contentColor = Color(0xFF00222B)
                            )
                        ) {
                            Text(
                                text = "অ্যাপে প্রবেশ করুন (Continue)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun FeatureBullet(icon: ImageVector, title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceCardNavy,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PrimaryCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SafetyBullet(title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DarkNavyBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}

private fun openFacebookUrl(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEV_FACEBOOK_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Opening Facebook...", Toast.LENGTH_SHORT).show()
    }
}

package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
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
private const val DEV_GMAIL = "alexraselchodhury@gmail.com"
private const val DEV_FACEBOOK_URL = "https://www.facebook.com/md.rasel.7.8.2.3.4"
private const val DEV_WHATSAPP_NUMBER = "+8801882278234"
private const val DEV_WHATSAPP_URL = "https://wa.me/8801882278234"
private const val DEV_TELEGRAM_URL = "https://t.me/HANTER_XD_OFFICIAL"

@Composable
fun DeveloperSupportScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }
    var feedbackSubject by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Hero Profile Card
        item {
            DeveloperHeroCard()
        }

        // Quick Direct Channels
        item {
            DirectContactChannelsSection(context = context)
        }

        // Quick Feedback & Support Ticket Composer
        item {
            SupportFeedbackSection(
                subject = feedbackSubject,
                onSubjectChange = { feedbackSubject = it },
                message = feedbackText,
                onMessageChange = { feedbackText = it },
                onSubmit = {
                    if (feedbackText.isBlank()) {
                        Toast.makeText(context, "Please enter your message or query first.", Toast.LENGTH_SHORT).show()
                    } else {
                        val finalSubject = if (feedbackSubject.isNotBlank()) feedbackSubject else "WiFi Router Manager Support Request"
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$DEV_GMAIL")
                            putExtra(Intent.EXTRA_SUBJECT, finalSubject)
                            putExtra(Intent.EXTRA_TEXT, feedbackText)
                        }
                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send Email Support"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email client installed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // App Information & Version Card
        item {
            AppInformationCard()
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DeveloperHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        PrimaryBlue.copy(alpha = 0.35f),
                        SurfaceCardNavy,
                        SurfaceNavy
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(PrimaryCyan, PrimaryBlue.copy(alpha = 0.5f), AccentGreen)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
            .testTag("dev_hero_card"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Online status pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AccentGreen.copy(alpha = 0.15f))
                    .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Official Developer & Support Center",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AccentGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Avatar with glowing gradient rim
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(PrimaryCyan, AccentGreen, PrimaryBlue, PrimaryCyan)
                        )
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(DarkNavyBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Developer Avatar",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Developer Name with verified badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = DEV_NAME,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.2.sp
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Developer",
                    tint = PrimaryCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lead Network Architect & Android Developer",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryCyan.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "For router setup assistance, bug reports, custom firmware integrations, or inquiries, reach out directly through any channel below.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun DirectContactChannelsSection(context: Context) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PrimaryCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Direct Contact Channels",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        // Row 1: WhatsApp & Telegram
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UniqueSocialCard(
                title = "WhatsApp",
                subtitle = "Instant Messaging",
                icon = Icons.Default.Chat,
                accentColor = Color(0xFF25D366),
                modifier = Modifier.weight(1f),
                onClick = {
                    openWhatsApp(context, DEV_WHATSAPP_NUMBER, DEV_WHATSAPP_URL)
                }
            )

            UniqueSocialCard(
                title = "Telegram",
                subtitle = "@HANTER_XD_OFFICIAL",
                icon = Icons.Default.Send,
                accentColor = Color(0xFF229ED9),
                modifier = Modifier.weight(1f),
                onClick = {
                    openUrl(context, DEV_TELEGRAM_URL)
                }
            )
        }

        // Row 2: Facebook & Gmail
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UniqueSocialCard(
                title = "Facebook",
                subtitle = "Official Profile & Group",
                icon = Icons.Default.Share,
                accentColor = Color(0xFF1877F2),
                modifier = Modifier.weight(1f),
                onClick = {
                    openUrl(context, DEV_FACEBOOK_URL)
                }
            )

            UniqueSocialCard(
                title = "Gmail",
                subtitle = "Direct Email Support",
                icon = Icons.Default.Email,
                accentColor = Color(0xFFEA4335),
                modifier = Modifier.weight(1f),
                onClick = {
                    openEmail(context, DEV_GMAIL)
                }
            )
        }
    }
}

@Composable
private fun UniqueSocialCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .testTag("dev_channel_${title.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardNavy)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.15f),
                            SurfaceCardNavy
                        )
                    )
                )
                .padding(16.dp)
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
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.dp, accentColor.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open",
                    tint = accentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SupportFeedbackSection(
    subject: String,
    onSubjectChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardNavy)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Feedback,
                        contentDescription = "Send Query",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Submit a Request / Bug Report",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Send your feedback directly to the developer's mailbox",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }

            OutlinedTextField(
                value = subject,
                onValueChange = onSubjectChange,
                label = { Text("Topic or Subject (Optional)") },
                placeholder = { Text("e.g. Cudy Router auto-login issue") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("support_input_subject"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Your Message / Query") },
                placeholder = { Text("Describe what happened or request a new feature...") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("support_input_message"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("support_btn_send_email"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCyan,
                    contentColor = Color(0xFF00222B)
                )
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Direct Message",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun AppInformationCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Application Information",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            InfoRow(label = "Application Name", value = "WiFi Router Manager")
            InfoRow(label = "Version", value = "v2.5.0 Pro Edition")
            InfoRow(label = "Architect & Developer", value = DEV_NAME)
            InfoRow(label = "Privacy & Security", value = "Encrypted Local Storage (Zero Cloud Telemetry)")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private fun openWhatsApp(context: Context, number: String, url: String) {
    try {
        val uri = Uri.parse("whatsapp://send?phone=${number.replace("+", "").replace(" ", "")}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        openUrl(context, url)
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open link.", Toast.LENGTH_SHORT).show()
    }
}

private fun openEmail(context: Context, email: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
    }
}

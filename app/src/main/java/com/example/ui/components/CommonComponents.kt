package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SurfaceCardNavy
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TopNetworkAppBar(
    title: String,
    ssid: String,
    isConnected: Boolean,
    onRefresh: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenLanguage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = DarkNavyBg,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryCyan.copy(alpha = 0.2f), PrimaryBlue.copy(alpha = 0.3f))
                            )
                        )
                        .border(1.dp, PrimaryCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Router else Icons.Default.WifiOff,
                        contentDescription = "Router Icon",
                        tint = if (isConnected) PrimaryCyan else AccentRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) AccentGreen else AccentRed)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isConnected) ssid else "No WiFi Connection",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isConnected) AccentGreen else TextMuted,
                                fontSize = 12.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onOpenLanguage,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), CircleShape)
                        .testTag("language_switch_top_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenSupport,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .border(1.dp, PrimaryCyan.copy(alpha = 0.3f), CircleShape)
                        .testTag("dev_support_top_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Developer Support",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .testTag("refresh_network_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderSubtle,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardNavy)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = color,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun SignalQualityIndicator(
    rssiDbm: Int,
    percentage: Int,
    is5Ghz: Boolean,
    modifier: Modifier = Modifier
) {
    val qualityColor = when {
        percentage >= 75 -> AccentGreen
        percentage >= 50 -> PrimaryCyan
        percentage >= 30 -> AccentOrange
        else -> AccentRed
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.SignalWifi4Bar,
            contentDescription = "Signal",
            tint = qualityColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = "$percentage% ($rssiDbm dBm)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = if (is5Ghz) "5 GHz High-Speed" else "2.4 GHz Standard",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (is5Ghz) PrimaryCyan else TextSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * Procedural stylized QR Matrix renderer in Compose Canvas
 * Encodes text into visual pattern with corner finder patterns for instant WiFi sharing
 */
@Composable
fun StylizedQrMatrix(
    contentString: String,
    modifier: Modifier = Modifier,
    matrixColor: Color = PrimaryCyan,
    bgColor: Color = Color(0xFF0F172A)
) {
    Canvas(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(2.dp, matrixColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        val sizePx = size.minDimension
        val gridSize = 21 // Standard QR Version 1 grid size
        val cellSize = sizePx / gridSize

        // Draw corner finder patterns (Top-Left, Top-Right, Bottom-Left)
        fun drawFinder(xCells: Int, yCells: Int) {
            val ox = xCells * cellSize
            val oy = yCells * cellSize
            // Outer 7x7
            drawRoundRect(
                color = matrixColor,
                topLeft = Offset(ox, oy),
                size = Size(7 * cellSize, 7 * cellSize),
                cornerRadius = CornerRadius(cellSize, cellSize)
            )
            // Inner hollow 5x5
            drawRoundRect(
                color = bgColor,
                topLeft = Offset(ox + cellSize, oy + cellSize),
                size = Size(5 * cellSize, 5 * cellSize),
                cornerRadius = CornerRadius(cellSize * 0.5f, cellSize * 0.5f)
            )
            // Center solid 3x3
            drawRoundRect(
                color = matrixColor,
                topLeft = Offset(ox + 2 * cellSize, oy + 2 * cellSize),
                size = Size(3 * cellSize, 3 * cellSize),
                cornerRadius = CornerRadius(cellSize * 0.5f, cellSize * 0.5f)
            )
        }

        drawFinder(0, 0)
        drawFinder(gridSize - 7, 0)
        drawFinder(0, gridSize - 7)

        // Deterministic pseudo-random pattern based on string hash for data cells
        val hash = contentString.hashCode()
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val inFinder1 = r < 8 && c < 8
                val inFinder2 = r < 8 && c >= gridSize - 8
                val inFinder3 = r >= gridSize - 8 && c < 8

                if (!inFinder1 && !inFinder2 && !inFinder3) {
                    val bitVal = ((hash.toLong() * (r + 1) * 31 + (c + 1) * 17) xor (r * c).toLong()) % 100
                    if (bitVal > 48 || (r == 6 || c == 6)) {
                        drawRoundRect(
                            color = matrixColor.copy(alpha = 0.9f),
                            topLeft = Offset(c * cellSize + cellSize * 0.1f, r * cellSize + cellSize * 0.1f),
                            size = Size(cellSize * 0.8f, cellSize * 0.8f),
                            cornerRadius = CornerRadius(cellSize * 0.2f, cellSize * 0.2f)
                        )
                    }
                }
            }
        }
    }
}

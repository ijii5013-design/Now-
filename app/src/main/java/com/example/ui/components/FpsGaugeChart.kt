package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BedrockBlack
import com.example.ui.theme.BedrockCardBorder
import com.example.ui.theme.BedrockDark
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.RedstoneRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboGold
import kotlin.math.cos
import kotlin.math.sin

/**
 * A custom Canvas-rendered Speedometer / Gauge Chart designed for the 2,000,000 FPS Bedrock Benchmark.
 * Visualizes frame velocity up to 2,000,000 FPS and displays the booster verification status.
 */
@Composable
fun FpsGaugeChart(
    currentFps: Long,
    maxFps: Long = 2_000_000L,
    isTesting: Boolean = false,
    isVerified: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Normalization factor: maps 0..maxFps to 0f..1f (allows slight overshoot up to 1.05f)
    val targetFraction = (currentFps.toFloat() / maxFps.toFloat()).coerceIn(0f, 1.08f)
    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "gaugeSweep"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF132218), Color(0xFF090E0B)),
                    radius = 600f
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        if (isVerified) NeonCreeper else BedrockCardBorder,
                        if (isVerified) DiamondCyan else BedrockCardBorder
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = if (isVerified) NeonCreeper else TurboGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "FPS VELOCITY GAUGE",
                    color = TextPrimary,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isVerified) Color(0x3300FF66) else Color(0x33FFB703))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isVerified) "VERIFIED" else if (isTesting) "TESTING" else "READY",
                        color = if (isVerified) NeonCreeper else TurboGold,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Gauge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(240.dp)) {
                    val strokeWidth = 18.dp.toPx()
                    val arcPadding = strokeWidth / 2f + 10f
                    val arcSize = Size(size.width - arcPadding * 2f, size.height - arcPadding * 2f)
                    val arcTopLeft = Offset(arcPadding, arcPadding)

                    val startAngle = 140f
                    val sweepTotal = 260f

                    // 1. Background Arc Track
                    drawArc(
                        color = Color(0xFF162019),
                        startAngle = startAngle,
                        sweepAngle = sweepTotal,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 2. Zone Color Bands on Track (Red -> Yellow -> Cyan -> Neon Green)
                    val zones = listOf(
                        Pair(0.12f, RedstoneRed),
                        Pair(0.28f, TurboGold),
                        Pair(0.60f, DiamondCyan),
                        Pair(1.00f, NeonCreeper)
                    )
                    var lastEndAngle = startAngle
                    for ((fraction, zoneColor) in zones) {
                        val zoneSweep = (fraction * sweepTotal) - (lastEndAngle - startAngle)
                        drawArc(
                            color = zoneColor.copy(alpha = 0.25f),
                            startAngle = lastEndAngle,
                            sweepAngle = zoneSweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth * 0.7f, cap = StrokeCap.Butt)
                        )
                        lastEndAngle = startAngle + (fraction * sweepTotal)
                    }

                    // 3. Active Value Sweep Arc with Glow Gradient
                    if (animatedFraction > 0.01f) {
                        val activeSweep = animatedFraction * sweepTotal
                        val activeBrush = Brush.sweepGradient(
                            colors = listOf(
                                RedstoneRed,
                                TurboGold,
                                DiamondCyan,
                                NeonCreeper,
                                NeonCreeper
                            ),
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                        drawArc(
                            brush = activeBrush,
                            startAngle = startAngle,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // 4. Tick Marks and Labels
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = (size.width - arcPadding * 2f) / 2f
                    val tickSteps = 8

                    for (i in 0..tickSteps) {
                        val frac = i.toFloat() / tickSteps
                        val angleDeg = startAngle + (frac * sweepTotal)
                        val angleRad = Math.toRadians(angleDeg.toDouble())

                        val outerX = center.x + (radius + 6f) * cos(angleRad).toFloat()
                        val outerY = center.y + (radius + 6f) * sin(angleRad).toFloat()
                        val innerX = center.x + (radius - 12f) * cos(angleRad).toFloat()
                        val innerY = center.y + (radius - 12f) * sin(angleRad).toFloat()

                        val isMajor = (i == 0 || i == 1 || i == 4 || i == tickSteps)
                        drawLine(
                            color = if (isMajor) TextPrimary else Color(0xFF4B6352),
                            start = Offset(innerX, innerY),
                            end = Offset(outerX, outerY),
                            strokeWidth = if (isMajor) 3f else 1.5f
                        )
                    }

                    // 5. Animated Needle
                    val needleAngleDeg = startAngle + (animatedFraction * sweepTotal)
                    val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())

                    val needleLength = radius * 0.85f
                    val needleTipX = center.x + needleLength * cos(needleAngleRad).toFloat()
                    val needleTipY = center.y + needleLength * sin(needleAngleRad).toFloat()

                    // Needle Base Orthogonal Points for Taper
                    val orthoRad = needleAngleRad + Math.PI / 2.0
                    val baseWidth = 8f
                    val baseX1 = center.x + baseWidth * cos(orthoRad).toFloat()
                    val baseY1 = center.y + baseWidth * sin(orthoRad).toFloat()
                    val baseX2 = center.x - baseWidth * cos(orthoRad).toFloat()
                    val baseY2 = center.y - baseWidth * sin(orthoRad).toFloat()

                    val needlePath = Path().apply {
                        moveTo(baseX1, baseY1)
                        lineTo(needleTipX, needleTipY)
                        lineTo(baseX2, baseY2)
                        close()
                    }

                    // Draw needle body
                    drawPath(
                        path = needlePath,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFFFFFFF),
                                if (isVerified) NeonCreeper else TurboGold
                            )
                        )
                    )

                    // Glowing tip circle
                    drawCircle(
                        color = if (isVerified) NeonCreeper else TurboGold,
                        radius = 4f,
                        center = Offset(needleTipX, needleTipY)
                    )

                    // Central Hub Pin
                    drawCircle(
                        color = Color(0xFF0F1712),
                        radius = 18f,
                        center = center
                    )
                    drawCircle(
                        color = BedrockCardBorder,
                        radius = 18f,
                        center = center,
                        style = Stroke(width = 3f)
                    )
                    drawCircle(
                        color = if (isVerified) NeonCreeper else TurboGold,
                        radius = 6f,
                        center = center
                    )
                }

                // Digital Numeric Readout in bottom center of gauge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                ) {
                    Text(
                        text = String.format("%,d", currentFps),
                        color = if (isVerified) NeonCreeper else TurboGold,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "FRAMES / SECOND",
                        color = TextSecondary,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Verification Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isVerified) Color(0xFF142B1B) else Color(0xFF162019))
                    .border(
                        1.dp,
                        if (isVerified) NeonCreeper else BedrockCardBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = if (isVerified) NeonCreeper else TurboGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVerified) "BOOSTER VERIFIED (2M FPS EXTREME)" else if (isTesting) "SIMULATION ACTIVE • STRESSING..." else "READY FOR VOXEL STRESS TEST",
                            color = if (isVerified) NeonCreeper else TextPrimary,
                            fontFamily = ChakraPetchFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = if (isVerified) "100% UNLOCKED" else "READY",
                        color = if (isVerified) NeonCreeper else TurboGold,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

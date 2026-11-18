package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datastore.RenderDragonSettings
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

@Composable
fun RenderDragonTuningPanel(
    settings: RenderDragonSettings,
    onShaderQualityChange: (Float) -> Unit,
    onDistanceFogChange: (Float) -> Unit,
    onRayTracingIntensityChange: (Float) -> Unit,
    onApplyPreset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF142219), Color(0xFF0A0F0C)),
                    radius = 700f
                )
            )
            .border(1.5.dp, DiamondCyan, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x3300E5FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = DiamondCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RENDERDRAGON TUNING",
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "DataStore Persisted Graphics Pipeline",
                            color = TextSecondary,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF122C24))
                        .border(1.dp, NeonCreeper, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "DATASTORE ON",
                        color = NeonCreeper,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 7.sp
                    )
                }
            }

            // Quick Preset Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetButton(
                    label = "2M POTATO",
                    isHighlight = settings.shaderQuality < 30f && settings.rayTracingIntensity == 0f,
                    onClick = { onApplyPreset("2M_SPEED") },
                    modifier = Modifier.weight(1f)
                )
                PresetButton(
                    label = "BALANCED",
                    isHighlight = settings.shaderQuality in 40f..75f,
                    onClick = { onApplyPreset("BALANCED") },
                    modifier = Modifier.weight(1f)
                )
                PresetButton(
                    label = "RTX ULTRA",
                    isHighlight = settings.rayTracingIntensity > 80f,
                    onClick = { onApplyPreset("RTX_ULTRA") },
                    modifier = Modifier.weight(1f)
                )
            }

            // ==========================================
            // SLIDER 1: SHADER QUALITY
            // ==========================================
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCreeper,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SHADER QUALITY",
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 9.sp
                        )
                    }
                    Text(
                        text = when {
                            settings.shaderQuality <= 20f -> "${settings.shaderQuality.toInt()}% [POTATO 2M]"
                            settings.shaderQuality <= 60f -> "${settings.shaderQuality.toInt()}% [BALANCED]"
                            else -> "${settings.shaderQuality.toInt()}% [ULTRA 2M]"
                        },
                        color = NeonCreeper,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 9.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Slider(
                    value = settings.shaderQuality,
                    onValueChange = onShaderQualityChange,
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCreeper,
                        activeTrackColor = NeonCreeper,
                        inactiveTrackColor = BedrockDark
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0% (Potato +1.8M FPS)", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 10.sp)
                    Text("100% (Ultra PBR Shaders)", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 10.sp)
                }
            }

            // ==========================================
            // SLIDER 2: DISTANCE FOG
            // ==========================================
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            tint = DiamondCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DISTANCE FOG",
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 9.sp
                        )
                    }
                    Text(
                        text = if (settings.distanceFog <= 5f) "0% [ZERO FOG BOOST]" else "${settings.distanceFog.toInt()}% [DENSE]",
                        color = if (settings.distanceFog <= 5f) NeonCreeper else DiamondCyan,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 9.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Slider(
                    value = settings.distanceFog,
                    onValueChange = onDistanceFogChange,
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = DiamondCyan,
                        activeTrackColor = DiamondCyan,
                        inactiveTrackColor = BedrockDark
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0% (Zero Fog Fullbright)", color = NeonCreeper, fontFamily = ChakraPetchFontFamily, fontSize = 10.sp)
                    Text("100% (Vanilla Atmospheric)", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 10.sp)
                }
            }

            // ==========================================
            // SLIDER 3: RAY-TRACING INTENSITY
            // ==========================================
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flare,
                            contentDescription = null,
                            tint = TurboGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RAY-TRACING INTENSITY",
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 9.sp
                        )
                    }
                    Text(
                        text = if (settings.rayTracingIntensity <= 5f) "OFF (2M FAST)" else "${settings.rayTracingIntensity.toInt()}% [PBR DXR]",
                        color = if (settings.rayTracingIntensity <= 5f) TurboGold else RedstoneRed,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 9.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Slider(
                    value = settings.rayTracingIntensity,
                    onValueChange = onRayTracingIntensityChange,
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = TurboGold,
                        activeTrackColor = TurboGold,
                        inactiveTrackColor = BedrockDark
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0% (Disabled for 2M FPS)", color = TurboGold, fontFamily = ChakraPetchFontFamily, fontSize = 10.sp)
                    Text("100% (Full Path Tracing)", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 10.sp)
                }
            }

            // Active Tuning Summary Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1712))
                    .border(1.dp, BedrockCardBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = NeonCreeper,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PIPELINE IMPACT",
                            color = TextSecondary,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = when {
                            settings.shaderQuality <= 20f && settings.rayTracingIntensity == 0f -> "+1,850,000 FPS OVERDRIVE"
                            settings.rayTracingIntensity > 60f -> "RTX ACTIVE (REQUIRES DXR)"
                            else -> "+650,000 FPS BOOSTED"
                        },
                        color = if (settings.shaderQuality <= 20f && settings.rayTracingIntensity == 0f) NeonCreeper else TurboGold,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetButton(
    label: String,
    isHighlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHighlight) Color(0xFF1E3524) else BedrockDark)
            .border(
                1.dp,
                if (isHighlight) NeonCreeper else BedrockCardBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isHighlight) NeonCreeper else TextPrimary,
            fontFamily = PressStart2PFontFamily,
            fontSize = 8.sp
        )
    }
}

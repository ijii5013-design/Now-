package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BedrockDefaults
import com.example.ui.components.BedrockHeader
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.components.RenderDragonTuningPanel
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
import com.example.ui.theme.TurboFlame
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun OptimizerScreen(
    viewModel: LauncherViewModel,
    onNavigateToAi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val fpsOptions = listOf(60L, 144L, 240L, 1_000_000L, 2_000_000L)
    val ramOptions = listOf(4, 6, 8, 12, 16)
    val dragonModes = listOf(
        "Ultra Latency (Zero Delay)",
        "Potato 2M FPS",
        "RTX Raytracing Overdrive"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BedrockHeader(
                title = "2M FPS OPTIMIZER SUITE",
                subtitle = "Hardware overrides, RenderDragon tuning & texture packs"
            )
        }

        // Master 2,000,000 FPS Overdrive Switch
        item {
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (state.isExtremeBoosterActive) NeonCreeper else BedrockCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2,000,000 FPS EXTREME MODE",
                            color = if (state.isExtremeBoosterActive) NeonCreeper else TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bypasses RenderDragon 60fps limit, locks 0.0005ms frame times, and frees heap memory",
                            color = TextSecondary,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Switch(
                        checked = state.isExtremeBoosterActive,
                        onCheckedChange = { viewModel.toggleBooster() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextDark,
                            checkedTrackColor = NeonCreeper,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = BedrockDark
                        )
                    )
                }
            }
        }

        // Gemini AI RenderDragon Advisor Quick Link
        item {
            PixelCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAi() },
                borderColor = DiamondCyan
            ) {
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0C2424))
                                .border(1.dp, DiamondCyan, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GEMINI 3.8 FLASH AUTO-TUNER",
                                color = DiamondCyan,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Auto-calculate shader & fog values for your GPU",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DiamondCyan)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AI TUNE",
                            color = TextDark,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ==========================================
        // RENDERDRAGON TUNING CONTROL PANEL (DATASTORE)
        // ==========================================
        item {
            RenderDragonTuningPanel(
                settings = state.renderDragonTuning,
                onShaderQualityChange = { viewModel.setShaderQuality(it) },
                onDistanceFogChange = { viewModel.setDistanceFog(it) },
                onRayTracingIntensityChange = { viewModel.setRayTracingIntensity(it) },
                onApplyPreset = { viewModel.applyRenderDragonPreset(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Target FPS Selector
        item {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "TARGET FRAME RATE CAP:",
                        color = TextPrimary,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(fpsOptions) { fps ->
                            val isSelected = state.fpsTarget == fps
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF1E3524) else BedrockDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCreeper else BedrockCardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setFpsTarget(fps) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (fps >= 1_000_000) "${fps / 1_000_000}M FPS" else "$fps FPS",
                                    color = if (isSelected) NeonCreeper else TextPrimary,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // RenderDragon Engine Pipeline
        item {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "RENDERDRAGON PIPELINE:",
                        color = TextPrimary,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    dragonModes.forEach { mode ->
                        val isSelected = state.renderDragonMode == mode
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF192C20) else BedrockDark)
                                .border(
                                    1.dp,
                                    if (isSelected) TurboGold else BedrockCardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { viewModel.setRenderDragonMode(mode) }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = mode,
                                color = if (isSelected) TurboGold else TextPrimary,
                                fontFamily = ChakraPetchFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Memory Buffer Allocation
        item {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "RAM ALLOCATION:",
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${state.allocatedMemoryGb} GB",
                            color = DiamondCyan,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ramOptions) { ram ->
                            val isSelected = state.allocatedMemoryGb == ram
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF142E35) else BedrockDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) DiamondCyan else BedrockCardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.setMemoryAllocation(ram) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$ram GB",
                                    color = if (isSelected) DiamondCyan else TextPrimary,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Render Distance Slider
        item {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "RENDER DISTANCE:",
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${state.renderDistanceChunks} CHUNKS",
                            color = NeonCreeper,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = state.renderDistanceChunks.toFloat(),
                        onValueChange = { viewModel.setRenderDistance(it.toInt()) },
                        valueRange = 8f..64f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCreeper,
                            activeTrackColor = NeonCreeper,
                            inactiveTrackColor = BedrockDark
                        )
                    )
                }
            }
        }

        // Advanced Toggles (FSR, VSync, Multi-Threading)
        item {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ADVANCED HARDWARE FLAGS:",
                        color = TextPrimary,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 10.sp
                    )

                    // FSR Upscaling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("FSR 3.0 FRAME GENERATION", color = TextPrimary, fontFamily = ChakraPetchFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Simulates sub-pixel frame interpolation", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                        }
                        Switch(
                            checked = state.fsrUpscaling,
                            onCheckedChange = { viewModel.toggleFsr() }
                        )
                    }

                    // VSync
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VSYNC (ZERO LATENCY TEAR-FREE)", color = TextPrimary, fontFamily = ChakraPetchFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Off unlocks unlimited FPS without display refresh throttle", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                        }
                        Switch(
                            checked = state.vsync,
                            onCheckedChange = { viewModel.toggleVsync() }
                        )
                    }

                    // Multi-threading
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("MULTI-THREAD CHUNK MESHER", color = TextPrimary, fontFamily = ChakraPetchFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Distributes Bedrock chunk geometry to all 16 CPU cores", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                        }
                        Switch(
                            checked = state.multiThreadedChunkMeshing,
                            onCheckedChange = { viewModel.toggleMultiThreading() }
                        )
                    }
                }
            }
        }

        // Texture Packs for 2M FPS
        item {
            Column {
                BedrockHeader(
                    title = "TEXTURE PACKS",
                    subtitle = "Optimized texture resolutions for maximum 2,000,000 FPS"
                )
                Spacer(modifier = Modifier.height(10.dp))
                BedrockDefaults.texturePacks.forEach { pack ->
                    val isSelected = pack.id == state.activeTexturePack.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF1E3524) else BedrockDark)
                            .border(
                                1.5.dp,
                                if (isSelected) NeonCreeper else BedrockCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectTexturePack(pack) }
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pack.name,
                                    color = if (isSelected) NeonCreeper else TextPrimary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = pack.fpsGain,
                                    color = TurboGold,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 9.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Resolution: ${pack.resolution} • ${pack.description}",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

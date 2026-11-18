package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BedrockDefaults
import com.example.ui.components.BedrockHeader
import com.example.ui.components.MetricPill
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.BedrockBlack
import com.example.ui.theme.BedrockCard
import com.example.ui.theme.BedrockCardBorder
import com.example.ui.theme.BedrockDark
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboFlame
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.LauncherViewModel
import kotlin.random.Random

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    onNavigateToBenchmark: () -> Unit,
    onNavigateToOptimizer: () -> Unit,
    onNavigateToAddons: () -> Unit,
    onNavigateToServers: () -> Unit,
    onNavigateToWardrobe: () -> Unit,
    onNavigateToHud: () -> Unit,
    onNavigateToAi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showVersionDialog by remember { mutableStateOf(false) }

    // Pulsing glowing animation for the FPS Booster status
    val infiniteTransition = rememberInfiniteTransition(label = "boosterPulse")
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlowAlpha"
    )

    val launchInteractionSource = remember { MutableInteractionSource() }
    val isLaunchPressed by launchInteractionSource.collectIsPressedAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section with Cinematic Bedrock Artwork & Version Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_bedrock),
                    contentDescription = "Minecraft Bedrock 2M FPS Landscape",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High-fidelity dark gradient overlay for contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x66000000),
                                    Color(0xAA090D0A),
                                    BedrockBlack
                                )
                            )
                        )
                )

                // Top Floating Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile & App branding chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xD90E1611))
                            .border(1.dp, Color(0xFF223528), RoundedCornerShape(20.dp))
                            .clickable { onNavigateToWardrobe() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (state.isExtremeBoosterActive) NeonCreeper else TurboGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.activeSkin.name.uppercase(),
                            color = TextPrimary,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 9.sp
                        )
                    }

                    // Version Picker Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xD90E1611))
                            .border(1.dp, DiamondCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.soundManager.playClick()
                                showVersionDialog = true
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = state.activeVersion.name,
                            color = DiamondCyan,
                            fontFamily = ChakraPetchFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = DiamondCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Bottom Title & Subtitle inside Hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "BEDROCK EDITION",
                        color = TextSecondary,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "2,000,000 FPS LAUNCHER",
                        color = TextPrimary,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // ==========================================
        // PROMINENT FPS BOOSTER STATUS DISPLAY
        // ==========================================
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF142419),
                                Color(0xFF0C130E)
                            ),
                            radius = 650f
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = if (state.isExtremeBoosterActive) {
                                listOf(
                                    NeonCreeper.copy(alpha = pulseGlowAlpha),
                                    DiamondCyan.copy(alpha = pulseGlowAlpha * 0.7f),
                                    NeonCreeper.copy(alpha = pulseGlowAlpha)
                                )
                            } else {
                                listOf(BedrockCardBorder, BedrockCardBorder)
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(18.dp)
            ) {
                Column {
                    // Header of Booster Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (state.isExtremeBoosterActive) Color(0x3300FF66) else Color(0x22FFFFFF)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = if (state.isExtremeBoosterActive) NeonCreeper else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "FPS BOOSTER ENGINE",
                                    color = TextSecondary,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (state.isExtremeBoosterActive) NeonCreeper else Color.Gray
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (state.isExtremeBoosterActive) "OVERCLOCKED • ACTIVE" else "STANDARD 60 FPS",
                                        color = if (state.isExtremeBoosterActive) NeonCreeper else TextSecondary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Toggle switch
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prominent Large FPS Readout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xD9080E0A))
                            .border(1.dp, Color(0xFF1D3023), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CURRENT TARGET VELOCITY",
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = String.format("%,d FPS", state.fpsTarget),
                                    color = if (state.isExtremeBoosterActive) NeonCreeper else TurboGold,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 18.sp
                                )
                            }

                            // Dynamic Frame Time Pill
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "FRAME TIME",
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (state.isExtremeBoosterActive) "0.0005 ms" else "16.6 ms",
                                    color = DiamondCyan,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Telemetry Grid (Latency, RAM, RenderDragon, Pack)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            label = "INPUT DELAY",
                            value = "0.00ms",
                            valueColor = NeonCreeper,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "RAM ALLOCATED",
                            value = "${state.allocatedMemoryGb} GB",
                            valueColor = TurboGold,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "RENDER CHUNKS",
                            value = "${state.renderDistanceChunks}",
                            valueColor = DiamondCyan,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            label = "PIPELINE",
                            value = "VULKAN 2M",
                            valueColor = Color(0xFFA855F7),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // One-Tap Hyper Clean & Boost Action inside Booster Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF16251A))
                            .border(1.dp, Color(0xFF284430), RoundedCornerShape(8.dp))
                            .clickable { viewModel.runHyperBoost() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = TurboGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PURGE CACHE & HYPER BOOST",
                                    color = TextPrimary,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 9.sp
                                )
                            }
                            if (state.isOptimizing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = TurboGold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "OPTIMIZE",
                                    color = TurboGold,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    // Progress bar when optimizing
                    AnimatedVisibility(visible = state.isOptimizing) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            LinearProgressIndicator(
                                progress = { state.optimizationProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonCreeper,
                                trackColor = Color(0xFF26382C)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.optimizationLog,
                                color = NeonCreeper,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // PRIMARY 'LAUNCH GAME' BUTTON
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glow & Bounce Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .scale(if (isLaunchPressed) 0.97f else 1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF00FF66),
                                    Color(0xFF059669),
                                    Color(0xFF047857)
                                )
                            )
                        )
                        .border(
                            2.dp,
                            Brush.verticalGradient(listOf(Color(0xFF86EFAC), Color(0xFF10B981))),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            interactionSource = launchInteractionSource,
                            indication = null
                        ) {
                            viewModel.launchMinecraft()
                        }
                        .testTag("launch_game_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color(0xFF062812),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "LAUNCH GAME",
                                color = Color(0xFF062812),
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "2,000,000 FPS • BEDROCK ${state.activeVersion.id}",
                                color = Color(0xFF0B4220),
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Installation Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (state.isMinecraftInstalled) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonCreeper,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Minecraft Bedrock Installed (${state.installedPackageName})",
                            color = NeonCreeper,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 12.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = TurboGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Direct Launch & Built-In 2M FPS Benchmark Ready",
                            color = TurboGold,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ==========================================
        // TEXTURE PACKS (HORIZONTAL PRESETS LIST)
        // ==========================================
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BedrockHeader(
                        title = "TEXTURE PACK PRESETS",
                        subtitle = "Toggle active visual profiles & FPS boosts"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF142419))
                            .border(1.dp, DiamondCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .clickable { onNavigateToOptimizer() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = DiamondCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ALL PACKS",
                            color = DiamondCyan,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(BedrockDefaults.texturePacks, key = { it.id }) { pack ->
                        val isSelected = pack.id == state.activeTexturePack.id
                        Box(
                            modifier = Modifier
                                .width(230.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF1D3524), Color(0xFF102015))
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF161F19), Color(0xFF0F1511))
                                        )
                                    }
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) NeonCreeper else BedrockCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.selectTexturePack(pack)
                                }
                                .padding(14.dp)
                        ) {
                            Column {
                                // Top row: Resolution Tag & Active Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isSelected) Color(0x3300FF66) else Color(0x22FFFFFF)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = pack.resolution,
                                            color = if (isSelected) NeonCreeper else TextSecondary,
                                            fontFamily = PressStart2PFontFamily,
                                            fontSize = 7.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonCreeper)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = TextDark,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "ACTIVE",
                                                color = TextDark,
                                                fontFamily = PressStart2PFontFamily,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Pack Title
                                Text(
                                    text = pack.name,
                                    color = if (isSelected) NeonCreeper else TextPrimary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // FPS Gain Badge
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = TurboGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = pack.fpsGain,
                                        color = TurboGold,
                                        fontFamily = PressStart2PFontFamily,
                                        fontSize = 8.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Description
                                Text(
                                    text = pack.description,
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // LAUNCHER MODULES GRID
        // ==========================================
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                BedrockHeader(
                    title = "LAUNCHER MODULES",
                    subtitle = "Engine tuning, voxel benchmark & multiplayer suite"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // AI Architect Banner Card
                PixelCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAi() },
                    borderColor = NeonCreeper
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
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF142419))
                                    .border(1.dp, NeonCreeper, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCreeper,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AI DRAGON ARCHITECT",
                                    color = NeonCreeper,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Gemini 3.8 Flash GPU tuning, seed generator & live chat",
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonCreeper)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OPEN",
                                color = TextDark,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Benchmark & RenderDragon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PixelCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToBenchmark() },
                        borderColor = NeonCreeper
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = NeonCreeper,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "2M FPS BENCHMARK",
                                color = TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "3D Voxel Engine & TNT stress test",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }

                    PixelCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToOptimizer() },
                        borderColor = DiamondCyan
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "RENDERDRAGON",
                                color = TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Uncap FPS & potato texture packs",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Addons & Servers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PixelCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToAddons() },
                        borderColor = TurboGold
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = null,
                                tint = TurboGold,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ADDONS & SEEDS",
                                color = TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Fullbright, No Fog & Trial Chambers",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }

                    PixelCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToServers() },
                        borderColor = Color(0xFFA855F7)
                    ) {
                        Column {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "BEDROCK SERVERS",
                                color = TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hive, CubeCraft & Fast Direct Join",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: HUD & CPS Speed Trainer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PixelCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToHud() },
                        borderColor = NeonCreeper
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = NeonCreeper,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "HUD & CPS SPEED TRAINER",
                                        color = TextPrimary,
                                        fontFamily = PressStart2PFontFamily,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Keystrokes, XYZ coords & 5s jitter click test",
                                        color = TextSecondary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            PixelButton(
                                text = "OPEN",
                                onClick = { onNavigateToHud() },
                                modifier = Modifier.height(34.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Version Picker Dialog
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = {
                Text(
                    text = "SELECT BEDROCK VERSION",
                    color = TextPrimary,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 12.sp
                )
            },
            text = {
                Column {
                    BedrockDefaults.versions.forEach { ver ->
                        val isSelected = ver.id == state.activeVersion.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF1F3526) else BedrockDark)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCreeper else BedrockCardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    viewModel.selectVersion(ver)
                                    showVersionDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = ver.name,
                                        color = if (isSelected) NeonCreeper else TextPrimary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (ver.isLatest) {
                                        Text(
                                            text = "LATEST",
                                            color = TurboGold,
                                            fontFamily = PressStart2PFontFamily,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                Text(
                                    text = ver.features.joinToString(" • "),
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) {
                    Text("CLOSE", color = NeonCreeper, fontFamily = PressStart2PFontFamily, fontSize = 11.sp)
                }
            },
            containerColor = BedrockCard
        )
    }

    // Launch Confirmation / Guidance Dialog
    if (state.isLaunching && state.launchDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissLaunchDialog() },
            title = {
                Text(
                    text = "2,000,000 FPS BEDROCK LAUNCHER",
                    color = NeonCreeper,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 12.sp
                )
            },
            text = {
                Text(
                    text = state.launchDialogMessage ?: "",
                    color = TextPrimary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { viewModel.openPlayStoreMinecraft() }) {
                        Text("GOOGLE PLAY", color = TurboGold, fontFamily = PressStart2PFontFamily, fontSize = 10.sp)
                    }
                    TextButton(onClick = { viewModel.dismissLaunchDialog() }) {
                        Text("OK", color = NeonCreeper, fontFamily = PressStart2PFontFamily, fontSize = 10.sp)
                    }
                }
            },
            containerColor = BedrockCard
        )
    }
}

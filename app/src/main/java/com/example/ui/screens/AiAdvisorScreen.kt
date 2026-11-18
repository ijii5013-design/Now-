package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.components.BedrockHeader
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
import com.example.ui.theme.RedstoneRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboFlame
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.AiAdvisorState
import com.example.ui.viewmodel.AiAdvisorViewModel
import com.example.ui.viewmodel.AiChatUiState
import com.example.ui.viewmodel.AiSeedUiState
import com.example.ui.viewmodel.AiTuningRecommendation
import com.example.ui.viewmodel.AiTuningUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun AiAdvisorScreen(
    aiViewModel: AiAdvisorViewModel,
    launcherViewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val aiState by aiViewModel.state.collectAsState()
    val launcherState by launcherViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("GPU TUNING", "SEED GEN", "AI CHAT")

    LaunchedEffect(aiState.appliedSuccessMessage) {
        val msg = aiState.appliedSuccessMessage
        if (msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            launcherViewModel.soundManager.playLevelUp()
            aiViewModel.dismissSuccessMessage()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BedrockHeader(
                title = "AI DRAGON ARCHITECT",
                subtitle = "Powered by Firebase AI Logic & Gemini 3.8 Flash"
            )
        }

        // Live Hardware Telemetry Banner
        item {
            HardwareTelemetryCard(aiState)
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BedrockDark,
                contentColor = NeonCreeper,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonCreeper,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BedrockCardBorder, RoundedCornerShape(8.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            launcherViewModel.soundManager.playClick()
                            selectedTab = index
                        },
                        text = {
                            Text(
                                text = title,
                                fontFamily = ChakraPetchFontFamily,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == index) NeonCreeper else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Tab 1: AI Hardware & RenderDragon Tuning
                item {
                    AiTuningTabContent(
                        aiState = aiState,
                        onAnalyzeClicked = {
                            launcherViewModel.soundManager.playTurboBoost()
                            aiViewModel.analyzeHardwareAndRecommendTuning(launcherState.renderDragonTuning)
                        },
                        onApplyClicked = { rec ->
                            launcherViewModel.soundManager.playLevelUp()
                            aiViewModel.applyRecommendation(rec)
                            launcherViewModel.setMemoryAllocation(rec.recommendedRamGb)
                            launcherViewModel.setFpsTarget(rec.recommendedFpsCap)
                        }
                    )
                }
            }
            1 -> {
                // Tab 2: AI Seed Synthesizer
                item {
                    AiSeedTabContent(
                        aiState = aiState,
                        onGenerateSeed = { theme ->
                            launcherViewModel.soundManager.playTurboBoost()
                            aiViewModel.generateHighFpsSeeds(theme)
                        },
                        onCopySeed = { seed ->
                            launcherViewModel.soundManager.playClick()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Minecraft Seed", seed))
                            Toast.makeText(context, "Seed $seed copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            2 -> {
                // Tab 3: Bedrock Engineering Chat
                item {
                    AiChatTabContent(
                        aiState = aiState,
                        onSendMessage = { query ->
                            launcherViewModel.soundManager.playClick()
                            aiViewModel.sendChatMessage(query)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HardwareTelemetryCard(aiState: AiAdvisorState) {
    val specs = aiState.specs
    PixelCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = DiamondCyan
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = DiamondCyan,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "TARGET HARDWARE TELEMETRY",
                    color = DiamondCyan,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 11.sp
                )
            }

            Text(
                text = "${specs.manufacturer} ${specs.model} • Android ${specs.androidVersion} (API ${specs.sdkInt})",
                color = TextPrimary,
                fontFamily = ChakraPetchFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SoC: ${specs.hardware}",
                    color = TextSecondary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 12.sp
                )
                Text(
                    text = "RAM: ${String.format("%.1f", specs.availableRamGb)}GB Free / ${String.format("%.1f", specs.totalRamGb)}GB",
                    color = NeonCreeper,
                    fontFamily = ChakraPetchFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AiTuningTabContent(
    aiState: AiAdvisorState,
    onAnalyzeClicked: () -> Unit,
    onApplyClicked: (AiTuningRecommendation) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PixelCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "GEMINI RENDERDRAGON ADVISOR",
                    color = NeonCreeper,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 12.sp
                )

                Text(
                    text = "Let Gemini 3.8 Flash evaluate your device's GPU fillrate, cache size, and thread dispatch to construct an ultra-optimized RenderDragon pipeline config aimed at 2,000,000 FPS.",
                    color = TextSecondary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 13.sp
                )

                PixelButton(
                    text = if (aiState.tuningState is AiTuningUiState.Loading) "ANALYZING HARDWARE..." else "OPTIMIZE FOR 2,000,000 FPS",
                    onClick = onAnalyzeClicked,
                    enabled = aiState.tuningState !is AiTuningUiState.Loading,
                    icon = Icons.Default.AutoAwesome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_analyze_button")
                )
            }
        }

        when (val state = aiState.tuningState) {
            is AiTuningUiState.Idle -> {
                // Helpful idle guidance
                PixelCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = BedrockCardBorder
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Tap 'OPTIMIZE' to generate custom RenderDragon tuning",
                            color = TextSecondary,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            is AiTuningUiState.Loading -> {
                PixelCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = NeonCreeper,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "Consulting Gemini 3.8 Flash & RenderDragon engine...",
                            color = NeonCreeper,
                            fontFamily = ChakraPetchFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        LinearProgressIndicator(
                            color = DiamondCyan,
                            trackColor = BedrockDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                        )
                    }
                }
            }
            is AiTuningUiState.Error -> {
                PixelCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = RedstoneRed
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = RedstoneRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "AI OPTIMIZATION NOTICE",
                                color = RedstoneRed,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = state.message,
                            color = TextPrimary,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 13.sp
                        )
                        PixelButton(
                            text = "RETRY OPTIMIZATION",
                            onClick = onAnalyzeClicked,
                            icon = Icons.Default.Refresh,
                            color = RedstoneRed,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            is AiTuningUiState.Success -> {
                val rec = state.recommendation
                PixelCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCreeper
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rec.profileName.uppercase(),
                                color = NeonCreeper,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TurboFlame.copy(alpha = 0.2f))
                                    .border(1.dp, TurboFlame, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = rec.expectedFramerateBoost,
                                    color = TurboFlame,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Tuning Metrics Grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TuningMetricRow("Shader Quality", String.format("%.2f", rec.shaderQuality), DiamondCyan)
                            TuningMetricRow("Distance Fog", String.format("%.2f", rec.distanceFog), TurboGold)
                            TuningMetricRow("Ray-Tracing Intensity", String.format("%.2f", rec.rayTracingIntensity), TurboFlame)
                            TuningMetricRow("Allocated RAM", "${rec.recommendedRamGb} GB", NeonCreeper)
                            TuningMetricRow("Target FPS Cap", "${rec.recommendedFpsCap} FPS", NeonCreeper)
                            TuningMetricRow("Anti-Aliasing", rec.antiAliasingMode, TextPrimary)
                        }

                        // Technical Analysis Narrative
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(BedrockDark)
                                .border(1.dp, BedrockCardBorder, RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "GEMINI ENGINE ANALYSIS:",
                                    color = DiamondCyan,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = rec.technicalAnalysis,
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        PixelButton(
                            text = "APPLY TO RENDERDRAGON",
                            onClick = { onApplyClicked(rec) },
                            icon = Icons.Default.Check,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("apply_tuning_button")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TuningMetricRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontFamily = ChakraPetchFontFamily,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = color,
            fontFamily = ChakraPetchFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun AiSeedTabContent(
    aiState: AiAdvisorState,
    onGenerateSeed: (String) -> Unit,
    onCopySeed: (String) -> Unit
) {
    val presets = listOf(
        "Nether Fortress Speedrun",
        "Trial Chamber Near Spawn",
        "Flat Ocean Mushroom Island",
        "Desert Temple & Village"
    )
    var selectedPreset by remember { mutableStateOf(presets.first()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PixelCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "2M FPS SEED SYNTHESIZER",
                    color = NeonCreeper,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 12.sp
                )
                Text(
                    text = "Generate Bedrock world seeds optimized for high framerates with low subterranean cave air density and immediate access to speedrun structures.",
                    color = TextSecondary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 13.sp
                )

                // Presets Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets) { preset ->
                        val isSel = selectedPreset == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) NeonCreeper else BedrockDark)
                                .border(1.dp, if (isSel) NeonCreeper else BedrockCardBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedPreset = preset }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset,
                                color = if (isSel) TextDark else TextPrimary,
                                fontFamily = ChakraPetchFontFamily,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                PixelButton(
                    text = if (aiState.seedState is AiSeedUiState.Loading) "SYNTHESIZING SEEDS..." else "SYNTHESIZE SEEDS",
                    onClick = { onGenerateSeed(selectedPreset) },
                    enabled = aiState.seedState !is AiSeedUiState.Loading,
                    icon = Icons.Default.Public,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("synthesize_seeds_button")
                )
            }
        }

        when (val state = aiState.seedState) {
            is AiSeedUiState.Idle -> {
                PixelCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Pick a seed archetype above and tap 'SYNTHESIZE SEEDS' to query Gemini.",
                        color = TextSecondary,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is AiSeedUiState.Loading -> {
                PixelCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator(
                            color = NeonCreeper,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Calculating high-framerate chunk terrain with Gemini...",
                            color = NeonCreeper,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            is AiSeedUiState.Error -> {
                PixelCard(modifier = Modifier.fillMaxWidth(), borderColor = RedstoneRed) {
                    Text(text = state.message, color = RedstoneRed, fontFamily = ChakraPetchFontFamily, fontSize = 13.sp)
                }
            }
            is AiSeedUiState.Success -> {
                state.seeds.forEach { seed ->
                    PixelCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = DiamondCyan
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = seed.title,
                                    color = DiamondCyan,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onCopySeed(seed.seedNumber) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Seed",
                                        tint = NeonCreeper,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SEED: ${seed.seedNumber}",
                                    color = NeonCreeper,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = "Biome: ${seed.biome} • ${seed.spawnHighlights}",
                                color = TextPrimary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "Coordinates: ${seed.coordinates}",
                                color = TurboGold,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 12.sp
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BedrockDark)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "2M FPS Benefit: ${seed.fpsAdvantageReason}",
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiChatTabContent(
    aiState: AiAdvisorState,
    onSendMessage: (String) -> Unit
) {
    var queryText by remember { mutableStateOf("") }
    val quickQuestions = listOf(
        "How to uncap FPS on Bedrock 1.21?",
        "Best settings for low-end phone?",
        "Why does RenderDragon cause stuttering?",
        "How do chunk updates affect framerate?"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        PixelCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BEDROCK AI GRAPHICS SPECIALIST",
                    color = NeonCreeper,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 11.sp
                )
                Text(
                    text = "Ask any technical question about Bedrock graphics, RenderDragon, resource packs, or lag prevention. Responses stream live from Gemini 3.8 Flash.",
                    color = TextSecondary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 12.sp
                )
            }
        }

        // Quick question chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(quickQuestions) { q ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BedrockDark)
                        .border(1.dp, BedrockCardBorder, RoundedCornerShape(6.dp))
                        .clickable { onSendMessage(q) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = q,
                        color = DiamondCyan,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Chat History & Streaming Card
        if (aiState.chatHistory.isNotEmpty() || aiState.chatState is AiChatUiState.Streaming || aiState.chatState is AiChatUiState.Loading) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                aiState.chatHistory.forEach { msg ->
                    ChatBubble(
                        role = msg.role,
                        text = msg.content
                    )
                }

                if (aiState.chatState is AiChatUiState.Streaming) {
                    ChatBubble(
                        role = "assistant",
                        text = (aiState.chatState as AiChatUiState.Streaming).partialResponse,
                        isStreaming = true
                    )
                } else if (aiState.chatState is AiChatUiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NeonCreeper,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        if (aiState.chatState is AiChatUiState.Error) {
            PixelCard(modifier = Modifier.fillMaxWidth(), borderColor = RedstoneRed) {
                Text(
                    text = (aiState.chatState as AiChatUiState.Error).message,
                    color = RedstoneRed,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 12.sp
                )
            }
        }

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                placeholder = {
                    Text(
                        "Ask AI Architect...",
                        color = TextSecondary,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCreeper,
                    unfocusedBorderColor = BedrockCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCreeper
                ),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (queryText.isNotBlank()) {
                        onSendMessage(queryText)
                        queryText = ""
                    }
                },
                enabled = queryText.isNotBlank() && aiState.chatState !is AiChatUiState.Loading && aiState.chatState !is AiChatUiState.Streaming,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (queryText.isNotBlank()) NeonCreeper else BedrockDark)
                    .testTag("ai_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (queryText.isNotBlank()) TextDark else TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(role: String, text: String, isStreaming: Boolean = false) {
    val isUser = role == "user"
    val align = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) BedrockDark else BedrockCard
    val borderColor = if (isUser) DiamondCyan else NeonCreeper

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isUser) Icons.Default.Speed else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isUser) DiamondCyan else NeonCreeper,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isUser) "YOU" else "AI BEDROCK ARCHITECT",
                        color = if (isUser) DiamondCyan else NeonCreeper,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 9.sp
                    )
                    if (isStreaming) {
                        Text(
                            text = "• STREAMING",
                            color = TurboFlame,
                            fontFamily = ChakraPetchFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }

                Text(
                    text = text,
                    color = TextPrimary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboFlame
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun HudAndCpsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    var activeKey by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BedrockHeader(
                title = "HUD & CPS CLIENT SUITE",
                subtitle = "In-game client overlays & jitter click speed trainer"
            )
        }

        // CPS Speed Clicker Trainer
        item {
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = TurboGold
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CPS SPEED TRAINER (5 SEC)",
                            color = TurboGold,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "BEST: ${String.format("%.1f", state.bestCpsRate)} CPS",
                            color = NeonCreeper,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 9.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Click Target Zone
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (state.isCpsActive) Color(0xFF261D0C) else BedrockDark)
                            .border(
                                2.dp,
                                if (state.isCpsActive) TurboGold else BedrockCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        viewModel.registerCpsClick()
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = if (state.isCpsActive) TurboGold else TextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (state.isCpsActive) "${state.cpsClicks} CLICKS (${String.format("%.1f", state.currentCpsRate)} CPS)" else "TAP RAPIDLY TO START TEST",
                                color = if (state.isCpsActive) TurboGold else TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (state.isCpsActive) "Time: ${String.format("%.1f", state.cpsTimeRemainingSeconds)}s remaining" else "Jitter click or Butterfly click",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (state.isCpsActive) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { state.cpsTimeRemainingSeconds / 5.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TurboGold,
                            trackColor = Color(0xFF2E2413)
                        )
                    }

                    if (state.cpsFinishedMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E3524), RoundedCornerShape(6.dp))
                                .border(1.dp, NeonCreeper, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = state.cpsFinishedMessage ?: "",
                                color = NeonCreeper,
                                fontFamily = ChakraPetchFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Live In-Game HUD Preview
        item {
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonCreeper
            ) {
                Column {
                    Text(
                        text = "LIVE BEDROCK HUD PREVIEW",
                        color = NeonCreeper,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mock Screen HUD Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF090D0A))
                            .border(1.dp, Color(0xFF1F2E23), RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        // FPS Overlay (Top-Left)
                        if (state.hudFps) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(Color(0x99000000), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "2,000,000 FPS",
                                    color = NeonCreeper,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        // Coordinates Overlay (Top-Right)
                        if (state.hudCoordinates) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color(0x99000000), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "XYZ: 142.5 / 64.0 / -823.1",
                                    color = DiamondCyan,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Crosshair (Center)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(16.dp, 2.dp)
                                    .background(Color.White)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(2.dp, 16.dp)
                                    .background(Color.White)
                            )
                        }

                        // Armor HUD (Bottom-Left)
                        if (state.hudArmor) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .background(Color(0x99000000), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text("NETHERITE HELMET 100%", color = Color(0xFFA855F7), fontFamily = ChakraPetchFontFamily, fontSize = 9.sp)
                                Text("NETHERITE CHEST 98%", color = Color(0xFFA855F7), fontFamily = ChakraPetchFontFamily, fontSize = 9.sp)
                                Text("NETHERITE LEGS 95%", color = Color(0xFFA855F7), fontFamily = ChakraPetchFontFamily, fontSize = 9.sp)
                                Text("NETHERITE BOOTS 99%", color = Color(0xFFA855F7), fontFamily = ChakraPetchFontFamily, fontSize = 9.sp)
                            }
                        }

                        // Keystrokes Overlay (Bottom-Right)
                        if (state.hudKeystrokes) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color(0x99000000), RoundedCornerShape(4.dp))
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // W Key
                                KeyBox("W", activeKey == "W") { activeKey = "W"; viewModel.soundManager.playClick() }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    KeyBox("A", activeKey == "A") { activeKey = "A"; viewModel.soundManager.playClick() }
                                    KeyBox("S", activeKey == "S") { activeKey = "S"; viewModel.soundManager.playClick() }
                                    KeyBox("D", activeKey == "D") { activeKey = "D"; viewModel.soundManager.playClick() }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    KeyBox("LMB", activeKey == "LMB", width = 28) { activeKey = "LMB"; viewModel.soundManager.playClick() }
                                    KeyBox("RMB", activeKey == "RMB", width = 28) { activeKey = "RMB"; viewModel.soundManager.playClick() }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Toggles List
        item {
            PixelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "HUD OVERLAY TOGGLES:",
                        color = TextPrimary,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 10.sp
                    )

                    HudToggleRow("2,000,000 FPS Counter", "Top-left ultra-fast frame counter", state.hudFps) {
                        viewModel.toggleHudItem("fps")
                    }
                    HudToggleRow("Clicks-Per-Second (CPS)", "Real-time attack click counter", state.hudCps) {
                        viewModel.toggleHudItem("cps")
                    }
                    HudToggleRow("Coordinates & Biome (XYZ)", "Sub-block position and direction", state.hudCoordinates) {
                        viewModel.toggleHudItem("coords")
                    }
                    HudToggleRow("Armor & Durability Status", "Netherite armor durability percentages", state.hudArmor) {
                        viewModel.toggleHudItem("armor")
                    }
                    HudToggleRow("Keystrokes (WASD + LMB/RMB)", "Touch keystrokes overlay for recording", state.hudKeystrokes) {
                        viewModel.toggleHudItem("keystrokes")
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyBox(
    label: String,
    isActive: Boolean,
    width: Int = 20,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width.dp, 20.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (isActive) NeonCreeper else Color(0x66FFFFFF))
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) TextDark else TextPrimary,
            fontFamily = PressStart2PFontFamily,
            fontSize = 7.sp
        )
    }
}

@Composable
private fun HudToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = ChakraPetchFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontFamily = ChakraPetchFontFamily,
                fontSize = 11.sp
            )
        }
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(
                checkedColor = NeonCreeper,
                checkmarkColor = TextDark
            )
        )
    }
}

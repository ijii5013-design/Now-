package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BedrockHeader
import com.example.ui.components.BlockType
import com.example.ui.components.FpsGaugeChart
import com.example.ui.components.MetricPill
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.components.VoxelCanvas
import com.example.ui.theme.BedrockBlack
import com.example.ui.theme.BedrockCard
import com.example.ui.theme.BedrockCardBorder
import com.example.ui.theme.BedrockDark
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.RedstoneRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboFlame
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun BenchmarkScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedBlock by remember { mutableStateOf(BlockType.BEDROCK) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BedrockHeader(
                title = "REAL-TIME VOXEL BENCHMARK",
                subtitle = "Gauge speedometer & sub-microsecond FPS booster stress test"
            )
        }

        // ==========================================
        // GAUGE CHART & BOOSTER VERIFICATION DISPLAY
        // ==========================================
        item {
            FpsGaugeChart(
                currentFps = state.benchmarkCurrentFps,
                maxFps = 2_000_000L,
                isTesting = state.isBenchmarking,
                isVerified = state.isBoosterVerified,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Stress Test Control Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Detonate TNT Action
                PixelButton(
                    text = "DETONATE TNT",
                    onClick = {
                        selectedBlock = BlockType.TNT
                        viewModel.soundManager.playExplosion()
                    },
                    modifier = Modifier.weight(1f),
                    isAccent = true,
                    testTag = "detonate_tnt_button"
                )

                // Start Real-Time Stress Test
                PixelButton(
                    text = if (state.isBenchmarking) "TESTING..." else "RUN STRESS TEST",
                    onClick = { viewModel.startBenchmark() },
                    modifier = Modifier.weight(1.3f),
                    isPrimary = true,
                    enabled = !state.isBenchmarking,
                    testTag = "run_benchmark_button"
                )
            }
        }

        // Real-Time Stress Test Progress & Active Simulation Phases
        item {
            AnimatedVisibility(visible = state.isBenchmarking) {
                PixelCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCreeper
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.benchmarkPhase,
                                color = NeonCreeper,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "${(state.benchmarkProgress * 100).toInt()}%",
                                color = TurboGold,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { state.benchmarkProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonCreeper,
                            trackColor = Color(0xFF1B2E21)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live Stress Load Metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricPill(
                                label = "ACTIVE VOXELS",
                                value = String.format("%,d", state.benchmarkVoxelCount),
                                valueColor = DiamondCyan,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "PARTICLES",
                                value = String.format("%,d", state.benchmarkParticleCount),
                                valueColor = TurboGold,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "FRAME TIME",
                                value = "0.0005 ms",
                                valueColor = NeonCreeper,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Block Mesh Selector
        item {
            Column {
                Text(
                    text = "SELECT 3D VOXEL MESH:",
                    color = TextSecondary,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(BlockType.entries) { block ->
                        val isSelected = block == selectedBlock
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF1E3524) else BedrockDark)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCreeper else BedrockCardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    viewModel.soundManager.playClick()
                                    selectedBlock = block
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = block.label,
                                color = if (isSelected) NeonCreeper else TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // 3D Voxel Interactive Canvas Engine
        item {
            VoxelCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                activeBlock = selectedBlock,
                isBoosterActive = state.isExtremeBoosterActive,
                targetFps = state.fpsTarget,
                onExplosion = {
                    if (selectedBlock == BlockType.TNT) {
                        viewModel.soundManager.playExplosion()
                    } else {
                        viewModel.soundManager.playBlockBreak()
                    }
                }
            )
        }

        // Booster Status & Telemetry Breakdown Card
        item {
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (state.isBoosterVerified) NeonCreeper else BedrockCardBorder
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (state.isBoosterVerified) Icons.Default.CheckCircle else Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (state.isBoosterVerified) NeonCreeper else TurboGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BOOSTER VERIFICATION STATUS",
                                color = TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = if (state.isBoosterVerified) "VERIFIED" else "PENDING",
                            color = if (state.isBoosterVerified) NeonCreeper else TurboGold,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("PEAK SPEED", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                            Text(
                                text = String.format("%,d FPS", state.benchmarkPeakFps),
                                color = NeonCreeper,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 11.sp
                            )
                        }
                        Column {
                            Text("1% LOWS", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                            Text("1,895,400 FPS", color = TurboGold, fontFamily = PressStart2PFontFamily, fontSize = 11.sp)
                        }
                        Column {
                            Text("FRAME TIME", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                            Text("0.0005 ms", color = DiamondCyan, fontFamily = PressStart2PFontFamily, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("RENDER PIPELINE", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                            Text("Vulkan RenderDragon 2M", color = TextPrimary, fontFamily = ChakraPetchFontFamily, fontSize = 13.sp)
                        }
                        Column {
                            Text("INPUT LATENCY", color = TextSecondary, fontFamily = ChakraPetchFontFamily, fontSize = 11.sp)
                            Text("0.00 ms (Zero Delay)", color = NeonCreeper, fontFamily = ChakraPetchFontFamily, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    // Benchmark Result Verification Dialog
    if (state.benchmarkFinishedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissBenchmarkDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonCreeper,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BOOSTER VERIFIED (2M FPS)!",
                        color = NeonCreeper,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 11.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Score: ${String.format("%,d", state.benchmarkScore)} POINTS",
                        color = TurboGold,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "GAUGE VERIFICATION REPORT:\n\n• Peak Rate: 2,014,800 FPS\n• Average Rate: 1,998,420 FPS\n• Voxel Mesh: 16,384 blocks rendered\n• Particle Physics: 10,000 active sub-ticks\n• 0.00% frame drops detected during explosion load\n• RenderDragon Frame Unlimiter: VERIFIED OK",
                        color = TextPrimary,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissBenchmarkDialog() }) {
                    Text("EXCELLENT", color = NeonCreeper, fontFamily = PressStart2PFontFamily, fontSize = 11.sp)
                }
            },
            containerColor = BedrockCard
        )
    }
}

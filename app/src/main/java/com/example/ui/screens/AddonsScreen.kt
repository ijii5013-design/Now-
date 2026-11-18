package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
fun AddonsScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "OPTIMIZATION", "VISUAL", "PVP", "UTILITY")

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.soundManager.playLevelUp()
            Toast.makeText(context, "Bedrock Addon (.mcpack) imported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            BedrockHeader(
                title = "ADDONS & SEEDS",
                subtitle = "Active Bedrock behavioral mods & curated speedrun seeds"
            )
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BedrockDark,
                contentColor = NeonCreeper,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonCreeper
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BedrockCardBorder, RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        viewModel.soundManager.playClick()
                        selectedTabIndex = 0
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Extension, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADDONS & MODS", fontFamily = PressStart2PFontFamily, fontSize = 9.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        viewModel.soundManager.playClick()
                        selectedTabIndex = 1
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SPEEDRUN SEEDS", fontFamily = PressStart2PFontFamily, fontSize = 9.sp)
                        }
                    }
                )
            }
        }

        if (selectedTabIndex == 0) {
            // Category filter row
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
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
                                    selectedCategory = cat
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) NeonCreeper else TextPrimary,
                                fontFamily = PressStart2PFontFamily,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }

            // Import Custom .mcpack button
            item {
                PixelCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.soundManager.playClick()
                            filePickerLauncher.launch("*/*")
                        },
                    borderColor = TurboGold
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderZip,
                            contentDescription = null,
                            tint = TurboGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IMPORT .MCPACK / .MCADDON",
                            color = TurboGold,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Mods List
            val filteredMods = if (selectedCategory == "ALL") {
                state.mods
            } else {
                state.mods.filter { it.category == selectedCategory }
            }

            items(filteredMods, key = { it.id }) { mod ->
                PixelCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (mod.isEnabled) NeonCreeper else BedrockCardBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mod.name,
                                    color = if (mod.isEnabled) NeonCreeper else TextPrimary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mod.version,
                                    color = TextSecondary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "By ${mod.author} • Impact: ${mod.fpsImpact}",
                                color = TurboGold,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mod.description,
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Switch(
                            checked = mod.isEnabled,
                            onCheckedChange = { viewModel.toggleMod(mod.id) },
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
        } else {
            // Speedrun Seeds Tab
            items(state.seeds, key = { it.id }) { seed ->
                PixelCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DiamondCyan
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = seed.name,
                                    color = TextPrimary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Version: ${seed.version}",
                                    color = DiamondCyan,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontSize = 11.sp
                                )
                            }
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(seed.seedValue))
                                    Toast.makeText(context, "Seed copied: ${seed.seedValue}", Toast.LENGTH_SHORT).show()
                                    viewModel.soundManager.playClick()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TurboGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPY SEED", color = TurboGold, fontFamily = PressStart2PFontFamily, fontSize = 9.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Seed: ${seed.seedValue}",
                            color = NeonCreeper,
                            fontFamily = PressStart2PFontFamily,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = seed.description,
                            color = TextSecondary,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Spawn Coordinates: ${seed.coordinates}",
                            color = DiamondCyan,
                            fontFamily = ChakraPetchFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(seed.structures) { struct ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BedrockDark)
                                        .border(1.dp, BedrockCardBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = struct,
                                        color = TextPrimary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

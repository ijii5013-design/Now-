package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BedrockDefaults
import com.example.ui.components.BedrockHeader
import com.example.ui.components.Character3DPreview
import com.example.ui.components.PixelButton
import com.example.ui.components.PixelCard
import com.example.ui.theme.BedrockBlack
import com.example.ui.theme.BedrockCardBorder
import com.example.ui.theme.BedrockDark
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun WardrobeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.soundManager.playLevelUp()
            Toast.makeText(context, "Custom Bedrock Skin loaded!", Toast.LENGTH_SHORT).show()
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
                title = "BEDROCK WARDROBE",
                subtitle = "3D player model preview with skins and capes"
            )
        }

        // 3D Model Preview
        item {
            Character3DPreview(
                skin = state.activeSkin,
                cape = state.activeCape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
            )
        }

        // Custom Skin Import button
        item {
            PixelCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.soundManager.playClick()
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                borderColor = DiamondCyan
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = DiamondCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IMPORT CUSTOM SKIN (PNG)",
                        color = DiamondCyan,
                        fontFamily = PressStart2PFontFamily,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Skins Selector
        item {
            Column {
                Text(
                    text = "SELECT BEDROCK SKIN:",
                    color = TextPrimary,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BedrockDefaults.skins.forEach { skin ->
                    val isSelected = skin.id == state.activeSkin.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF1B3222) else BedrockDark)
                            .border(
                                1.5.dp,
                                if (isSelected) NeonCreeper else BedrockCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectSkin(skin) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(skin.primaryColorHex), RoundedCornerShape(4.dp))
                                        .border(1.dp, Color(skin.accentColorHex), RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = skin.name,
                                        color = if (isSelected) NeonCreeper else TextPrimary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = skin.description,
                                        color = TextSecondary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = NeonCreeper,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Capes Selector
        item {
            Column {
                Text(
                    text = "SELECT BEDROCK CAPE:",
                    color = TextPrimary,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                BedrockDefaults.capes.forEach { cape ->
                    val isSelected = cape.id == state.activeCape?.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF282512) else BedrockDark)
                            .border(
                                1.5.dp,
                                if (isSelected) TurboGold else BedrockCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectCape(cape) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp, 28.dp)
                                        .background(Color(cape.colorHex), RoundedCornerShape(3.dp))
                                        .border(1.dp, Color(cape.secondaryHex), RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = cape.name,
                                        color = if (isSelected) TurboGold else TextPrimary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = cape.description,
                                        color = TextSecondary,
                                        fontFamily = ChakraPetchFontFamily,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = TurboGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

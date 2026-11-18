package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun ServersScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showAddServerDialog by remember { mutableStateOf(false) }
    var newServerName by remember { mutableStateOf("") }
    var newServerHost by remember { mutableStateOf("") }
    var newServerPort by remember { mutableStateOf("19132") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BedrockBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            BedrockHeader(
                title = "BEDROCK SERVERS",
                subtitle = "Fast-connect to top minigames and custom Bedrock lobbies"
            )
        }

        // Action Buttons (Add Server & Refresh Ping)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PixelButton(
                    text = "ADD SERVER",
                    onClick = { showAddServerDialog = true },
                    modifier = Modifier.weight(1f),
                    isAccent = true,
                    testTag = "add_server_button"
                )
                PixelButton(
                    text = "PING ALL",
                    onClick = { viewModel.refreshServerPings() },
                    modifier = Modifier.weight(1f),
                    testTag = "ping_all_button"
                )
            }
        }

        // Server List
        items(state.servers, key = { it.id }) { server ->
            PixelCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (server.isOfficialFeatured) BedrockCardBorder else TurboGold
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = server.name,
                                    color = TextPrimary,
                                    fontFamily = ChakraPetchFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                if (!server.isOfficialFeatured) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CUSTOM",
                                        color = TurboGold,
                                        fontFamily = PressStart2PFontFamily,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                            Text(
                                text = "${server.host}:${server.port}",
                                color = TextSecondary,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 12.sp
                            )
                        }

                        // Ping & Players badge
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SignalCellularAlt,
                                    contentDescription = null,
                                    tint = if (server.pingMs < 30) NeonCreeper else TurboGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${server.pingMs} ms",
                                    color = if (server.pingMs < 30) NeonCreeper else TurboGold,
                                    fontFamily = PressStart2PFontFamily,
                                    fontSize = 9.sp
                                )
                            }
                            Text(
                                text = "${server.playersOnline} players",
                                color = DiamondCyan,
                                fontFamily = ChakraPetchFontFamily,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = server.motd.replace("§[0-9a-fk-or]".toRegex(), ""),
                        color = TextSecondary,
                        fontFamily = ChakraPetchFontFamily,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("${server.host}:${server.port}"))
                                Toast.makeText(context, "Copied ${server.host}:${server.port}", Toast.LENGTH_SHORT).show()
                                viewModel.soundManager.playClick()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("COPY IP", color = TextSecondary, fontFamily = PressStart2PFontFamily, fontSize = 9.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        PixelButton(
                            text = "JOIN SERVER",
                            onClick = { viewModel.joinServer(server) },
                            isPrimary = true,
                            modifier = Modifier.height(38.dp),
                            testTag = "join_server_${server.id}"
                        )
                    }
                }
            }
        }
    }

    // Add Custom Server Dialog
    if (showAddServerDialog) {
        AlertDialog(
            onDismissRequest = { showAddServerDialog = false },
            title = {
                Text(
                    text = "ADD BEDROCK SERVER",
                    color = TextPrimary,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 11.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newServerName,
                        onValueChange = { newServerName = it },
                        label = { Text("Server Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCreeper,
                            unfocusedBorderColor = BedrockCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newServerHost,
                        onValueChange = { newServerHost = it },
                        label = { Text("Server Address (IP/Host)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCreeper,
                            unfocusedBorderColor = BedrockCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newServerPort,
                        onValueChange = { newServerPort = it },
                        label = { Text("Port (Default: 19132)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCreeper,
                            unfocusedBorderColor = BedrockCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                PixelButton(
                    text = "SAVE",
                    onClick = {
                        val portInt = newServerPort.toIntOrNull() ?: 19132
                        if (newServerName.isNotBlank() && newServerHost.isNotBlank()) {
                            viewModel.addCustomServer(newServerName, newServerHost, portInt)
                            showAddServerDialog = false
                            newServerName = ""
                            newServerHost = ""
                            newServerPort = "19132"
                        }
                    },
                    isPrimary = true,
                    modifier = Modifier.height(38.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddServerDialog = false }) {
                    Text("CANCEL", color = TextSecondary, fontFamily = PressStart2PFontFamily, fontSize = 9.sp)
                }
            },
            containerColor = BedrockCard
        )
    }
}

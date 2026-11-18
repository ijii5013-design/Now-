package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AddonsScreen
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.BenchmarkScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HudAndCpsScreen
import com.example.ui.screens.OptimizerScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.WardrobeScreen
import com.example.ui.theme.BedrockBlack
import com.example.ui.theme.BedrockCard
import com.example.ui.theme.BedrockCardBorder
import com.example.ui.theme.BedrockDark
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TurboGold
import com.example.ui.viewmodel.AiAdvisorViewModel
import com.example.ui.viewmodel.LauncherViewModel
import com.example.util.configureAppCheck

enum class LauncherScreen(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    BENCHMARK("2M FPS", Icons.Default.RocketLaunch),
    OPTIMIZER("Tweaks", Icons.Default.Tune),
    AI("AI", Icons.Default.AutoAwesome),
    ADDONS("Mods", Icons.Default.Extension),
    SERVERS("Servers", Icons.Default.Gamepad),
    WARDROBE("Skins", Icons.Default.Person),
    HUD("HUD", Icons.Default.Speed)
}

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()
    private val aiViewModel: AiAdvisorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(LauncherScreen.HOME) }

                // BackHandler returns to Home when on a sub-screen
                if (currentScreen != LauncherScreen.HOME) {
                    BackHandler {
                        currentScreen = LauncherScreen.HOME
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = BedrockBlack,
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        NavigationBar(
                            containerColor = BedrockDark,
                            tonalElevation = 8.dp,
                            windowInsets = WindowInsets.navigationBars,
                            modifier = Modifier
                                .border(1.dp, BedrockCardBorder)
                                .height(68.dp)
                        ) {
                            LauncherScreen.entries.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        viewModel.soundManager.playClick()
                                        currentScreen = screen
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.label,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.label,
                                            fontFamily = ChakraPetchFontFamily,
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = TextDark,
                                        selectedTextColor = NeonCreeper,
                                        indicatorColor = NeonCreeper,
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "ScreenTransition"
                        ) { target ->
                            when (target) {
                                LauncherScreen.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToBenchmark = { currentScreen = LauncherScreen.BENCHMARK },
                                    onNavigateToOptimizer = { currentScreen = LauncherScreen.OPTIMIZER },
                                    onNavigateToAddons = { currentScreen = LauncherScreen.ADDONS },
                                    onNavigateToServers = { currentScreen = LauncherScreen.SERVERS },
                                    onNavigateToWardrobe = { currentScreen = LauncherScreen.WARDROBE },
                                    onNavigateToHud = { currentScreen = LauncherScreen.HUD },
                                    onNavigateToAi = { currentScreen = LauncherScreen.AI }
                                )
                                LauncherScreen.BENCHMARK -> BenchmarkScreen(
                                    viewModel = viewModel
                                )
                                LauncherScreen.OPTIMIZER -> OptimizerScreen(
                                    viewModel = viewModel,
                                    onNavigateToAi = { currentScreen = LauncherScreen.AI }
                                )
                                LauncherScreen.AI -> AiAdvisorScreen(
                                    aiViewModel = aiViewModel,
                                    launcherViewModel = viewModel
                                )
                                LauncherScreen.ADDONS -> AddonsScreen(
                                    viewModel = viewModel
                                )
                                LauncherScreen.SERVERS -> ServersScreen(
                                    viewModel = viewModel
                                )
                                LauncherScreen.WARDROBE -> WardrobeScreen(
                                    viewModel = viewModel
                                )
                                LauncherScreen.HUD -> HudAndCpsScreen(
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

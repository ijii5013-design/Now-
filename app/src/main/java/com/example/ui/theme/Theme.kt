package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BedrockDarkColorScheme = darkColorScheme(
    primary = NeonCreeper,
    onPrimary = TextDark,
    primaryContainer = BedrockSurfaceVariant,
    onPrimaryContainer = NeonCreeper,
    secondary = TurboGold,
    onSecondary = TextDark,
    secondaryContainer = BedrockSurfaceVariant,
    onSecondaryContainer = TurboGold,
    tertiary = DiamondCyan,
    onTertiary = TextDark,
    tertiaryContainer = BedrockSurfaceVariant,
    onTertiaryContainer = DiamondCyan,
    background = BedrockBlack,
    onBackground = TextPrimary,
    surface = BedrockDark,
    onSurface = TextPrimary,
    surfaceVariant = BedrockCard,
    onSurfaceVariant = TextSecondary,
    outline = BedrockCardBorder,
    error = RedstoneRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Keep dark gaming theme active for Minecraft Bedrock vibe
    MaterialTheme(
        colorScheme = BedrockDarkColorScheme,
        typography = Typography,
        content = content
    )
}

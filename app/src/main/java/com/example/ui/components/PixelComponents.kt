package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

@Composable
fun PixelCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BedrockCardBorder,
    glowColor: Color? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BedrockCard)
            .border(
                border = BorderStroke(
                    1.5.dp,
                    glowColor ?: borderColor
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(14.dp)
    ) {
        content()
    }
}

@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    color: Color? = null,
    isPrimary: Boolean = false,
    isAccent: Boolean = false,
    enabled: Boolean = true,
    testTag: String = "pixel_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    val bgBrush = when {
        !enabled -> Brush.verticalGradient(listOf(Color(0xFF2B332D), Color(0xFF1E2420)))
        color != null -> Brush.verticalGradient(listOf(color.copy(alpha = 0.85f), color.copy(alpha = 0.5f)))
        isPrimary -> Brush.verticalGradient(listOf(Color(0xFF00FF66), Color(0xFF059669)))
        isAccent -> Brush.verticalGradient(listOf(Color(0xFFFFB703), Color(0xFFFF5400)))
        else -> Brush.verticalGradient(listOf(Color(0xFF2E3D32), Color(0xFF1B241E)))
    }

    val textColor = when {
        !enabled -> Color(0xFF6B7280)
        color != null -> TextPrimary
        isPrimary -> Color(0xFF0A0F0C)
        isAccent -> Color(0xFF0A0F0C)
        else -> TextPrimary
    }

    val borderColor = when {
        !enabled -> Color(0xFF374151)
        color != null -> color
        isPrimary -> Color(0xFF6EE7B7)
        isAccent -> Color(0xFFFDE047)
        else -> Color(0xFF3F5445)
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .scale(if (isPressed) 0.96f else 1f)
            .clip(RoundedCornerShape(6.dp))
            .background(bgBrush)
            .border(2.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) {
                scope.launch {
                    scale.animateTo(0.95f, tween(50, easing = FastOutSlowInEasing))
                    scale.animateTo(1f, tween(100, easing = FastOutSlowInEasing))
                }
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontFamily = PressStart2PFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
    valueColor: Color = NeonCreeper,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x800A0F0C))
            .border(1.dp, BedrockCardBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = TextSecondary,
                fontFamily = ChakraPetchFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = valueColor,
                fontFamily = PressStart2PFontFamily,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun BedrockHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(NeonCreeper, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontFamily = PressStart2PFontFamily,
                fontSize = 13.sp
            )
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontFamily = ChakraPetchFontFamily,
                fontSize = 13.sp
            )
        }
    }
}

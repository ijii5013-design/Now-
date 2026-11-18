package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CapeModel
import com.example.data.SkinModel
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.TurboGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Character3DPreview(
    skin: SkinModel,
    cape: CapeModel?,
    modifier: Modifier = Modifier
) {
    var rotationY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "flutter")
    val capeFlutter by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "capeFlutter"
    )

    val armSwing by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "armSwing"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF131F17), Color(0xFF090D0A))
                )
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotationY = (rotationY + dragAmount.x * 0.8f) % 360f
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.52f
            val scale = (size.height * 0.0032f).coerceAtLeast(0.6f)

            val rad = Math.toRadians(rotationY.toDouble())
            val cosRot = cos(rad).toFloat()
            val sinRot = sin(rad).toFloat()

            // Is character facing front or back?
            val isBackFacing = cosRot < 0f

            val primaryColor = Color(skin.primaryColorHex)
            val accentColor = Color(skin.accentColorHex)
            val hairColor = Color(skin.hairColorHex)
            val eyeColor = Color(skin.eyeColorHex)

            // Draw Cape if facing away or side
            if (cape != null && isBackFacing) {
                val capeColor = Color(cape.colorHex)
                val capeSecondary = Color(cape.secondaryHex)
                val flutterOffset = capeFlutter * sinRot.coerceIn(-1f, 1f)

                val capePath = Path().apply {
                    moveTo(cx - 24f * scale * cosRot, cy - 25f * scale)
                    lineTo(cx + 24f * scale * cosRot, cy - 25f * scale)
                    lineTo(cx + 28f * scale * cosRot + flutterOffset, cy + 65f * scale)
                    lineTo(cx - 28f * scale * cosRot + flutterOffset, cy + 65f * scale)
                    close()
                }
                drawPath(capePath, capeColor)

                // Cape emblem / trim
                drawRoundRect(
                    color = capeSecondary,
                    topLeft = Offset(cx - 10f * scale * cosRot + flutterOffset * 0.5f, cy + 10f * scale),
                    size = Size(20f * scale * Math.abs(cosRot).coerceAtLeast(0.2f), 30f * scale),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }

            // Head (centered at cy - 70 * scale)
            val headWidth = 44f * scale
            val headHeight = 44f * scale
            val headTop = cy - 85f * scale

            // Head base
            drawRoundRect(
                color = Color(0xFFFFD1A4), // skin tone
                topLeft = Offset(cx - headWidth / 2f, headTop),
                size = Size(headWidth, headHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Hair / Helmet
            val hairHeight = headHeight * 0.42f
            drawRoundRect(
                color = if (skin.hasArmor) primaryColor else hairColor,
                topLeft = Offset(cx - headWidth / 2f, headTop),
                size = Size(headWidth, hairHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Eyes (only visible when facing front)
            if (!isBackFacing) {
                val eyeWidth = 8f * scale
                val eyeHeight = 6f * scale
                val eyeY = headTop + headHeight * 0.52f
                val eyeSpacing = 10f * scale * cosRot

                // Left Eye
                drawRect(
                    color = Color.White,
                    topLeft = Offset(cx - eyeSpacing - eyeWidth, eyeY),
                    size = Size(eyeWidth, eyeHeight)
                )
                drawRect(
                    color = eyeColor,
                    topLeft = Offset(cx - eyeSpacing - eyeWidth * 0.5f, eyeY),
                    size = Size(eyeWidth * 0.5f, eyeHeight)
                )

                // Right Eye
                drawRect(
                    color = Color.White,
                    topLeft = Offset(cx + eyeSpacing, eyeY),
                    size = Size(eyeWidth, eyeHeight)
                )
                drawRect(
                    color = eyeColor,
                    topLeft = Offset(cx + eyeSpacing + eyeWidth * 0.5f, eyeY),
                    size = Size(eyeWidth * 0.5f, eyeHeight)
                )
            }

            // Torso
            val torsoWidth = 48f * scale * Math.abs(cosRot).coerceAtLeast(0.35f)
            val torsoHeight = 64f * scale
            val torsoTop = cy - 38f * scale
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(cx - torsoWidth / 2f, torsoTop),
                size = Size(torsoWidth, torsoHeight),
                cornerRadius = CornerRadius(3f, 3f)
            )

            // Armor trim / details on chest
            if (skin.hasArmor) {
                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(cx - (torsoWidth * 0.7f) / 2f, torsoTop + 14f * scale),
                    size = Size(torsoWidth * 0.7f, 36f * scale),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }

            // Left Arm
            val armWidth = 18f * scale
            val armHeight = 60f * scale
            val armY = torsoTop + (armSwing * scale)
            val leftArmX = cx - (torsoWidth / 2f) - armWidth - 2f * scale
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(leftArmX, armY),
                size = Size(armWidth, armHeight),
                cornerRadius = CornerRadius(3f, 3f)
            )
            // Hand
            drawRoundRect(
                color = Color(0xFFFFD1A4),
                topLeft = Offset(leftArmX, armY + armHeight - 14f * scale),
                size = Size(armWidth, 14f * scale),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Right Arm
            val rightArmX = cx + (torsoWidth / 2f) + 2f * scale
            val rightArmY = torsoTop - (armSwing * scale)
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(rightArmX, rightArmY),
                size = Size(armWidth, armHeight),
                cornerRadius = CornerRadius(3f, 3f)
            )
            // Hand
            drawRoundRect(
                color = Color(0xFFFFD1A4),
                topLeft = Offset(rightArmX, rightArmY + armHeight - 14f * scale),
                size = Size(armWidth, 14f * scale),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Legs
            val legWidth = 22f * scale
            val legHeight = 66f * scale
            val legTop = torsoTop + torsoHeight

            // Left Leg
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(cx - legWidth - 2f * scale, legTop),
                size = Size(legWidth, legHeight),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Boots
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(cx - legWidth - 2f * scale, legTop + legHeight - 16f * scale),
                size = Size(legWidth, 16f * scale),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Right Leg
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(cx + 2f * scale, legTop),
                size = Size(legWidth, legHeight),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Boots
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(cx + 2f * scale, legTop + legHeight - 16f * scale),
                size = Size(legWidth, 16f * scale),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Draw Cape if facing forward (hanging slightly behind)
            if (cape != null && !isBackFacing) {
                // Peek from shoulders
                val capeColor = Color(cape.colorHex)
                drawRect(
                    color = capeColor,
                    topLeft = Offset(cx - 26f * scale, cy - 35f * scale),
                    size = Size(52f * scale, 8f * scale)
                )
            }

            // Shadow on ground
            drawOval(
                color = Color(0x66000000),
                topLeft = Offset(cx - 40f * scale, legTop + legHeight - 4f * scale),
                size = Size(80f * scale, 14f * scale)
            )
        }

        // Info Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .background(Color(0xCC090D0A), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${skin.name} ${if (cape != null) "• " + cape.name else ""}",
                color = NeonCreeper,
                fontFamily = ChakraPetchFontFamily,
                fontSize = 11.sp
            )
        }
    }
}

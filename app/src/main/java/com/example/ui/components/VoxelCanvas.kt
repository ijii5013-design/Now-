package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChakraPetchFontFamily
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.NeonCreeper
import com.example.ui.theme.PressStart2PFontFamily
import com.example.ui.theme.RedstoneRed
import com.example.ui.theme.TurboGold
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class BlockType(val label: String, val topColor: Color, val leftColor: Color, val rightColor: Color) {
    BEDROCK("BEDROCK", Color(0xFF4A4E51), Color(0xFF2B2E30), Color(0xFF1E2022)),
    TNT("TNT", Color(0xFFE53935), Color(0xFFC62828), Color(0xFFB71C1C)),
    DIAMOND("DIAMOND BLOCK", Color(0xFF80DEEA), Color(0xFF26C6DA), Color(0xFF00ACC1)),
    OBSIDIAN("OBSIDIAN", Color(0xFF311B92), Color(0xFF1A0A4A), Color(0xFF120533)),
    EMERALD("EMERALD BLOCK", Color(0xFF69F0AE), Color(0xFF00E676), Color(0xFF00C853))
}

data class VoxelParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    val color: Color,
    var life: Float = 1.0f,
    val decay: Float = 0.02f
)

@Composable
fun VoxelCanvas(
    modifier: Modifier = Modifier,
    activeBlock: BlockType = BlockType.BEDROCK,
    isBoosterActive: Boolean = true,
    targetFps: Long = 2_000_000L,
    onExplosion: (() -> Unit)? = null
) {
    var rotX by remember { mutableFloatStateOf(25f) }
    var rotY by remember { mutableFloatStateOf(45f) }
    var autoSpin by remember { mutableStateOf(true) }

    val particles = remember { mutableStateListOf<VoxelParticle>() }
    var measuredFps by remember { mutableLongStateOf(2_000_000L) }
    var frameTimeMs by remember { mutableFloatStateOf(0.0005f) }
    var frameCount by remember { mutableLongStateOf(0L) }

    // High speed physics & particle updater loop
    LaunchedEffect(isBoosterActive) {
        var lastNanos = System.nanoTime()
        while (true) {
            withFrameNanos { nowNanos ->
                val dt = (nowNanos - lastNanos).coerceAtLeast(1L)
                lastNanos = nowNanos
                frameCount++

                if (autoSpin) {
                    rotY = (rotY + 1.2f) % 360f
                }

                // Simulate realistic 2,000,000 FPS variance around target
                val jitter = Random.nextLong(-220L, 260L)
                val base = if (isBoosterActive) targetFps else 60L
                measuredFps = (base + jitter).coerceAtLeast(30L)
                frameTimeMs = if (isBoosterActive) {
                    0.0005f + Random.nextFloat() * 0.00008f
                } else {
                    16.66f
                }

                // Update particles
                val iter = particles.iterator()
                while (iter.hasNext()) {
                    val p = iter.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.vy += 0.4f // gravity
                    p.life -= p.decay
                    if (p.life <= 0f) {
                        iter.remove()
                    }
                }
            }
        }
    }

    fun spawnExplosion(originX: Float, originY: Float, count: Int = 180) {
        val colors = when (activeBlock) {
            BlockType.TNT -> listOf(Color(0xFFFF3333), Color(0xFFFFB703), Color(0xFFFFFFFF), Color(0xFF222222))
            BlockType.BEDROCK -> listOf(Color(0xFF6B7280), Color(0xFF374151), Color(0xFF1F2937), Color(0xFF00FF66))
            BlockType.DIAMOND -> listOf(DiamondCyan, Color(0xFFE0F7FA), Color(0xFF00BCD4), Color(0xFF80DEEA))
            BlockType.EMERALD -> listOf(NeonCreeper, Color(0xFFB9F6CA), Color(0xFF00E676), Color(0xFF004D40))
            BlockType.OBSIDIAN -> listOf(Color(0xFF7C3AED), Color(0xFF4C1D95), Color(0xFF1E1B4B), Color(0xFFC084FC))
        }

        repeat(count) {
            val speed = Random.nextFloat() * 14f + 2f
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            particles.add(
                VoxelParticle(
                    x = originX,
                    y = originY,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 4f,
                    size = Random.nextFloat() * 8f + 3f,
                    color = colors.random(),
                    decay = Random.nextFloat() * 0.03f + 0.015f
                )
            )
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF17241C), Color(0xFF0A0F0C)),
                    radius = 800f
                )
            )
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    spawnExplosion(offset.x, offset.y, count = 220)
                    onExplosion?.invoke()
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { autoSpin = false },
                    onDragEnd = { autoSpin = true }
                ) { change, dragAmount ->
                    change.consume()
                    rotY = (rotY + dragAmount.x * 0.5f) % 360f
                    rotX = (rotX - dragAmount.y * 0.3f).coerceIn(-60f, 60f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val cubeSize = (size.minDimension * 0.38f).coerceAtLeast(90f)

            // Draw isometric 3D Voxel Cube with real rotation projection
            val radY = Math.toRadians(rotY.toDouble())
            val radX = Math.toRadians(rotX.toDouble())

            val cosY = cos(radY).toFloat()
            val sinY = sin(radY).toFloat()
            val cosX = cos(radX).toFloat()
            val sinX = sin(radX).toFloat()

            // 8 Cube vertices relative to center
            val half = cubeSize / 2f
            val rawVerts = listOf(
                floatArrayOf(-half, -half, -half), // 0: Top-Back-Left
                floatArrayOf( half, -half, -half), // 1: Top-Back-Right
                floatArrayOf( half, -half,  half), // 2: Top-Front-Right
                floatArrayOf(-half, -half,  half), // 3: Top-Front-Left
                floatArrayOf(-half,  half, -half), // 4: Bottom-Back-Left
                floatArrayOf( half,  half, -half), // 5: Bottom-Back-Right
                floatArrayOf( half,  half,  half), // 6: Bottom-Front-Right
                floatArrayOf(-half,  half,  half)  // 7: Bottom-Front-Left
            )

            // Project 3D vertices to 2D
            val projected = rawVerts.map { v ->
                // Rotate around Y
                val x1 = v[0] * cosY - v[2] * sinY
                val z1 = v[0] * sinY + v[2] * cosY
                // Rotate around X
                val y1 = v[1] * cosX - z1 * sinX
                val z2 = v[1] * sinX + z1 * cosX

                // Perspective factor
                val perspective = 1f + (z2 / (cubeSize * 3.5f))
                Offset(cx + x1 * perspective, cy + y1 * perspective)
            }

            // Top Face (0, 1, 2, 3)
            val topPath = Path().apply {
                moveTo(projected[0].x, projected[0].y)
                lineTo(projected[1].x, projected[1].y)
                lineTo(projected[2].x, projected[2].y)
                lineTo(projected[3].x, projected[3].y)
                close()
            }
            drawPath(topPath, activeBlock.topColor)

            // Front/Left Face (3, 2, 6, 7)
            val frontPath = Path().apply {
                moveTo(projected[3].x, projected[3].y)
                lineTo(projected[2].x, projected[2].y)
                lineTo(projected[6].x, projected[6].y)
                lineTo(projected[7].x, projected[7].y)
                close()
            }
            drawPath(frontPath, activeBlock.leftColor)

            // Side/Right Face (2, 1, 5, 6)
            val sidePath = Path().apply {
                moveTo(projected[2].x, projected[2].y)
                lineTo(projected[1].x, projected[1].y)
                lineTo(projected[5].x, projected[5].y)
                lineTo(projected[6].x, projected[6].y)
                close()
            }
            drawPath(sidePath, activeBlock.rightColor)

            // Pixel grid texture highlights on top face
            val step = cubeSize / 4f
            for (i in 1..3) {
                // Subtle pixel details
                drawLine(
                    color = Color.White.copy(alpha = 0.12f),
                    start = projected[3],
                    end = projected[1],
                    strokeWidth = 2f
                )
            }

            // Outer cube wireframe glow
            val edges = listOf(
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 0),
                Pair(4, 5), Pair(5, 6), Pair(6, 7), Pair(7, 4),
                Pair(0, 4), Pair(1, 5), Pair(2, 6), Pair(3, 7)
            )
            for ((startIdx, endIdx) in edges) {
                drawLine(
                    color = if (isBoosterActive) NeonCreeper.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.2f),
                    start = projected[startIdx],
                    end = projected[endIdx],
                    strokeWidth = if (isBoosterActive) 2.5f else 1.5f
                )
            }

            // Draw particles
            for (p in particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.life.coerceIn(0f, 1f)),
                    radius = p.size * p.life,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // Live Overclocked 2,000,000 FPS HUD overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .background(Color(0xCC090D0A), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            androidx.compose.foundation.layout.Column {
                Text(
                    text = String.format("%,d FPS", measuredFps),
                    color = if (isBoosterActive) NeonCreeper else TurboGold,
                    fontFamily = PressStart2PFontFamily,
                    fontSize = 11.sp
                )
                Text(
                    text = String.format("FRAME: %.4f ms | 1%% LOW: %,d", frameTimeMs, (measuredFps * 0.92).toLong()),
                    color = DiamondCyan,
                    fontFamily = ChakraPetchFontFamily,
                    fontSize = 11.sp
                )
            }
        }

        // Tap to Explode hint
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .background(Color(0x99000000), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "TAP TO DETONATE | DRAG TO ROTATE",
                color = TurboGold,
                fontFamily = ChakraPetchFontFamily,
                fontSize = 10.sp
            )
        }
    }
}

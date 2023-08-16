package ca.veltus.mindfulbreathingwearos.presentation.session.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin


@Composable
fun AnimatedRingTimer(isAnimating: Boolean) {
    val animationController = rememberInfiniteTransition()

    val progress by if (isAnimating) {
        animationController.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(15000, easing = LinearEasing),  // 15 seconds for full cycle
                repeatMode = RepeatMode.Restart
            )
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    // Determining current stage: Growing, Flashing, or Shrinking
    val stage = when {
        progress < 1/3f -> "growing"
        progress < 2/3f -> "flashing"
        else -> "shrinking"
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val ringWidth = 5.dp.toPx()
        val angleOffset = -90f  // To start from the top

        val sweepAngle = when(stage) {
            "growing" -> 360 * progress * 3  // multiply by 3 to cover the whole circle in 1/3 of the time
            "flashing" -> 360f
            "shrinking" -> 360 - 360 * ((progress - 2/3f) * 3)
            else -> 360f
        }

        // Deciding color
        val currentColor = when(stage) {
            "flashing" -> {
                // Flashes once every second (oscillates between transparent and opaque)
                val alpha = abs(sin(PI * (progress - 1/3f) * 10)).toFloat()
                Color.Green.copy(alpha = alpha)
            }
            "shrinking" -> Color.Blue
            else -> Color.Green
        }

        drawArc(
            color = currentColor,
            startAngle = angleOffset,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = ringWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AnimatedRingPreview() {
    AnimatedRingTimer(isAnimating = true)
}
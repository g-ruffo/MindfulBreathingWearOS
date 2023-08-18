package ca.veltus.mindfulbreathingwearos.presentation.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.R
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate

@Composable
fun AnimatedHeart(heartRate: HeartRate?, size: DpSize) {
    val targetBpm: Float = heartRate?.value?.toFloat() ?: 0f
    var isExpanding by remember { mutableStateOf(true) }

    if (heartRate == null) {
        Icon(
            painter = painterResource(id = R.drawable.heart_broken),
            contentDescription = stringResource(R.string.broken_heart_image),
            tint = Color.Red,
            modifier = Modifier
                .size(size)
        )
    } else {
        val beatDurationMillis = 60000f / targetBpm
        val halfBeatDurationMillis = beatDurationMillis / 2
        val targetScale = if (isExpanding) 1.2f else 1f
        val animatedScale by animateFloatAsState(
            targetValue = targetScale,
            animationSpec = tween(
                durationMillis = halfBeatDurationMillis.toInt(),
                easing = if (isExpanding) LinearOutSlowInEasing else FastOutSlowInEasing
            ), label = "HeartRateAnimateScale"
        )
        LaunchedEffect(animatedScale) {
            if (animatedScale == targetScale) {
                isExpanding = !isExpanding
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.heart),
            contentDescription = stringResource(R.string.heart_image),
            modifier = Modifier
                .size(size)
                .scale(animatedScale),
            tint = Color.Red
        )
    }
}

@Composable
@WearPreviewDevices
fun PreviewAnimatedHeart() {
    AnimatedHeart(heartRate = null, size = DpSize(width = 38.dp, height = 38.dp))
}
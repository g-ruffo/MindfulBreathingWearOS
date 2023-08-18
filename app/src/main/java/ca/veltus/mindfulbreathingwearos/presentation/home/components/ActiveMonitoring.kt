package ca.veltus.mindfulbreathingwearos.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.R
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate

@Composable
fun ActiveMonitoring(
    heartRate: HeartRate?,
    navigateToSession: () -> Unit
) {
    val orangeColor = Color(0xFFDB8C02)
    val orangeOpaque = orangeColor.copy(alpha = 0.6f)

    Scaffold(timeText = {
        TimeText(
            timeTextStyle = TimeTextDefaults.timeTextStyle(
                fontSize = 12.sp
            )
        )
    }) {
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(
                text = "${heartRate?.value ?: "--"}",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(90.dp),
                fontSize = 50.sp,
                textAlign = TextAlign.Right

            )
            Column(
                modifier = Modifier
                    .padding(start = 4.dp, end = 20.dp)
            ) {
                AnimatedHeart(heartRate = heartRate, size = DpSize(width = 28.dp, height = 28.dp))
                Text(
                    text = "bpm",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 10.dp)
                )
            }
        }
        Text(
            text = stringResource(R.string.current_heart_rate),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(bottom = 18.dp)
        )
        Button(
            colors = ButtonDefaults.buttonColors(
                orangeOpaque
            ),
            modifier = Modifier
                .width(100.dp)
                .background(orangeOpaque, shape = RoundedCornerShape(50))
                .border(
                    1.dp, orangeColor,
                    shape = RoundedCornerShape(50)
                ),
            onClick = {
                navigateToSession()
            }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.stats),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun ActivityMonitoringPreview() {
    ActiveMonitoring(
        heartRate = HeartRate(value = 39),
        navigateToSession = {}
    )
}
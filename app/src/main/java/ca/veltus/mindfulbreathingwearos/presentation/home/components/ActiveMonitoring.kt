package ca.veltus.mindfulbreathingwearos.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.PreviewActivity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import com.google.accompanist.permissions.isGranted

@Composable
fun ActiveMonitoring(
    heartRate: HeartRate?,
    navigateToSession: () -> Unit
) {
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
        Text(
            text = "Current Heart Rate",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth()
        ) {
            Text(
                text = "${heartRate?.value ?: "--"}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 50.sp,
            )
            Column {
                AnimatedHeart(heartRate = heartRate, size = DpSize(width = 30.dp, height = 30.dp))
                Text(
                    text = "bpm",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 10.dp)
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            onClick = {
                navigateToSession()
            }) {
            Text("Stats")
        }
    }
}

@WearPreviewDevices
@Composable
fun ActivityMonitoringPreview() {
    ActiveMonitoring(
        heartRate = HeartRate(value = 34),
        navigateToSession = {}
    )
}
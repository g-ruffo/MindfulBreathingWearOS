package ca.veltus.mindfulbreathingwearos.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import com.google.accompanist.permissions.isGranted

@Composable
fun ActiveMonitoring(
    heartRate: HeartRate?,
    navigateToSession: () -> Unit
) {
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

        Text(
            text = "${heartRate?.value ?: "--"} bpm",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                navigateToSession()
            }) {
            Text("Start Breathing Session")
        }
    }
}
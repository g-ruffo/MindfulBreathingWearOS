package ca.veltus.mindfulbreathingwearos.presentation.session

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import ca.veltus.mindfulbreathingwearos.presentation.home.HomeScreen
import ca.veltus.mindfulbreathingwearos.presentation.home.HomeViewModel
import ca.veltus.mindfulbreathingwearos.presentation.session.components.AnimatedRingTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SessionScreen(
    viewModel: SessionViewModel = hiltViewModel()
) {
    val heartRate = viewModel.heartRate.value
    val sessionTimeRemaining = viewModel.sessionTimeRemaining.value
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Breathing Session",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Heart Rate: $heartRate bpm",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Time Remaining: $sessionTimeRemaining seconds",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
            coroutineScope.launch {
                viewModel.startSession()
                while (viewModel.sessionTimeRemaining.value > 0) {
                    delay(1000)
                    viewModel.decrementSessionTime()
                }
                viewModel.endSession()
            }
        }) {
            Text("Start Session")
        }
    }
    AnimatedRingTimer(isAnimating = true)
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun SessionScreenPreview() {
}

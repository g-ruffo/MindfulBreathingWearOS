package ca.veltus.mindfulbreathingwearos.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.veltus.mindfulbreathingwearos.presentation.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val heartRate = viewModel.heartRate.value

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
            text = "$heartRate bpm",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
            navController.navigate(Screen.SessionScreen.route)

        }) {
            Text("Start Breathing Session")
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController(), viewModel = hiltViewModel())
}


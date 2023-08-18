package ca.veltus.mindfulbreathingwearos.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.veltus.mindfulbreathingwearos.common.Constants.PERMISSION
import ca.veltus.mindfulbreathingwearos.presentation.home.HomeScreen
import ca.veltus.mindfulbreathingwearos.presentation.home.HomeViewModel
import ca.veltus.mindfulbreathingwearos.presentation.stats.StatsScreen
import ca.veltus.mindfulbreathingwearos.presentation.stats.StatsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            val viewModel = hiltViewModel<HomeViewModel>()

            val heartRate by viewModel.heartRate
            val uiState by viewModel.uiState
            val isEnabled by viewModel.enabled.collectAsState()

            // Called when user presses button on home screen
            fun navigate() { navController.navigate(Screen.StatsScreen.route) }

            // Enables the heart rate data collection upon granting permissions
            val permissionState = rememberPermissionState(
                permission = PERMISSION,
                onPermissionResult = { granted ->
                    viewModel.enableHeartRate(granted)
                }
            )
            // If permissions are already granted begin collecting heart rate data
            if (isEnabled != permissionState.status.isGranted) {
                viewModel.enableHeartRate(permissionState.status.isGranted)
            }
            HomeScreen(
                heartRate = heartRate,
                navigateToSession = { navigate() },
                permissionState = permissionState,
                state = uiState
            )
        }

        composable(route = Screen.StatsScreen.route) {
            val viewModel = hiltViewModel<StatsViewModel>()

            val uncachedStats by viewModel.uncachedStats.collectAsState()
            val cacheStats by viewModel.cacheStats.collectAsState()
            val databaseStats by viewModel.databaseStats.collectAsState()
            val isDatabaseConnected by viewModel.isDatabaseConnected.collectAsState()
            val timerTime by viewModel.timerTimeSeconds.collectAsState()

            StatsScreen(
                uncachedStats = uncachedStats,
                cacheStats = cacheStats,
                databaseStats = databaseStats,
                isDatabaseEnabled = isDatabaseConnected,
                startStopPressed = { viewModel.toggleDatabaseEnabled() },
                stepperPressed = { value -> viewModel.updateTimerTime(value) },
                time = timerTime
            )
        }
    }
}
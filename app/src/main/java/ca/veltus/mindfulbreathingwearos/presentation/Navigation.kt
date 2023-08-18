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
            val availability by viewModel.availability
            val uiState by viewModel.uiState
            val isEnabled by viewModel.enabled.collectAsState()

            fun navigate() {
                navController.navigate(Screen.StatsScreen.route)
            }

            val permissionState = rememberPermissionState(
                permission = PERMISSION,
                onPermissionResult = { granted ->
                    viewModel.enableHeartRate(granted)
                }
            )
            if (isEnabled != permissionState.status.isGranted) {
                viewModel.enableHeartRate(permissionState.status.isGranted)
            }
            HomeScreen(
                heartRate = heartRate,
                navigateToSession = { navigate() },
                permissionState = permissionState,
                state = uiState,
                availability = availability
            )
        }

        composable(route = Screen.StatsScreen.route) {
            val viewModel = hiltViewModel<StatsViewModel>()
            val uncachedStats by viewModel.uncachedStats.collectAsState()
            val cacheCount by viewModel.cacheStats.collectAsState()
            val databaseCount by viewModel.databaseStats.collectAsState()

            StatsScreen(
                uncachedStats = cacheCount,
                cacheStats = cacheCount,
                databaseStats = databaseCount,
                isDatabaseEnabled = true
            )
        }
    }
}
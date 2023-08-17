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
import ca.veltus.mindfulbreathingwearos.presentation.home.UIState
import ca.veltus.mindfulbreathingwearos.presentation.session.SessionScreen
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
            val cacheCount by viewModel.cacheItemCount.collectAsState()
            val databaseCount by viewModel.databaseItemCount.collectAsState()

            fun navigate() {
                navController.navigate(Screen.SessionScreen.route)
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
                availability = availability,
                cacheCount = cacheCount,
                databaseCount = databaseCount
            )
        }

        composable(route = Screen.SessionScreen.route) {

            SessionScreen()
        }
    }
}
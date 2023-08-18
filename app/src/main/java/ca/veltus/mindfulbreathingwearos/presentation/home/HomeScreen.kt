package ca.veltus.mindfulbreathingwearos.presentation.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.DataTypeAvailability
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.HeartRate
import ca.veltus.mindfulbreathingwearos.presentation.home.components.ActiveMonitoring
import ca.veltus.mindfulbreathingwearos.presentation.home.components.LoadingIcon
import ca.veltus.mindfulbreathingwearos.presentation.home.components.PermissionsRequired
import ca.veltus.mindfulbreathingwearos.presentation.home.components.Unavailable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    heartRate: HeartRate?,
    navigateToSession: () -> Unit,
    permissionState: PermissionState,
    state: UIState,
    availability: DataTypeAvailability
    ) {
    if (state == UIState.Startup) {
        LoadingIcon()
    } else if (state == UIState.NotSupported){
        Unavailable()
    } else if (!permissionState.status.isGranted) {
        PermissionsRequired(
            permissionState = permissionState
        )
    } else {
        ActiveMonitoring(
            heartRate = heartRate,
            navigateToSession = { navigateToSession() }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val permissionState = object : PermissionState {
        override val permission = "android.permission.ACTIVITY_RECOGNITION"
        override val status: PermissionStatus = PermissionStatus.Granted
        override fun launchPermissionRequest() {}
    }
    val heartRate = HeartRate(value = 90)
    HomeScreen(
        heartRate = heartRate,
        navigateToSession = { },
        permissionState = permissionState,
        state = UIState.Supported,
        availability = DataTypeAvailability.AVAILABLE
        )
}


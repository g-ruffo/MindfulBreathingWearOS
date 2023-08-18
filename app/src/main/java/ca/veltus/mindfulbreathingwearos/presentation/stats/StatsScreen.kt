package ca.veltus.mindfulbreathingwearos.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.presentation.stats.components.AnimatedRingTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StatsScreen(
    cacheCount: Resource<Int>,
    databaseCount: Resource<Int>
) {

}

@WearPreviewDevices
@Composable
fun SessionScreenPreview() {
    StatsScreen(
        cacheCount = Resource.Success(30),
    databaseCount = Resource.Success(30)
    )
}

package ca.veltus.mindfulbreathingwearos.presentation.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.presentation.stats.components.CellItem
import ca.veltus.mindfulbreathingwearos.presentation.stats.components.CustomStepper

@Composable
fun StatsScreen(
    uncachedStats: Resource<DatabaseStats>,
    cacheStats: Resource<DatabaseStats>,
    databaseStats: Resource<DatabaseStats>,
    isDatabaseEnabled: Boolean
) {
    var value by remember { mutableStateOf(5) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Icon(
                imageVector = if (isDatabaseEnabled) {
                    Icons.Default.Cloud
                } else {
                    Icons.Default.CloudOff
                },
                contentDescription = "Database enabled status icon",
                tint = if (isDatabaseEnabled) Color.Green else Color.Red
            )
        }
        item {
            CustomStepper(
                value = 4,
                onValueChange = {},
                valueProgression = 0..10
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("$it")
                    Text("min")
                }
            }
        }
        item {
            Button(
                onClick = {
                    // Handle button click action, like toggling the database status
                },
                colors = ButtonDefaults.buttonColors(
                    if (isDatabaseEnabled) Color.Green else Color.Red
                ),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Text(
                    text = if (isDatabaseEnabled) "Disable" else "Enable",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
            }
        }

        items(3) { index ->
            when(index) {
                0 -> CellItem(uncachedStats)
                1 -> CellItem(cacheStats)
                2 -> CellItem(databaseStats)
            }
        }
    }
}
@WearPreviewDevices
@Composable
fun SessionScreenPreview() {
    val stats = Resource.Success(DatabaseStats(count = 43563, lastAddedDate = "August 11, 2023 12:00:22"))
    StatsScreen(
        uncachedStats = stats,
        cacheStats = stats,
        databaseStats = stats,
        isDatabaseEnabled = false
    )
}

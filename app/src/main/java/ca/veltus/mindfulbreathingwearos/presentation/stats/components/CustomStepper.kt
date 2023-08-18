package ca.veltus.mindfulbreathingwearos.presentation.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.presentation.stats.StatsScreen

@Composable
fun CustomStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueProgression: IntProgression,
    modifier: Modifier = Modifier,
    valueContent: @Composable (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button on the left
        IconButton(onClick = { if (value > valueProgression.first) onValueChange(value - 1) }) {
            Icon(StepperDefaults.Decrease, "Decrease")
        }

        // Value in the middle
        valueContent(value)

        // Increase button on the right
        IconButton(onClick = { if (value < valueProgression.last) onValueChange(value + 1) }) {
            Icon(StepperDefaults.Increase, "Increase")
        }
    }
}

@WearPreviewDevices
@Composable
fun StepperPreview() {
    val stats = Resource.Success(DatabaseStats(count = 43563, lastAddedDate = "August 11, 2023 12:00:22"))
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

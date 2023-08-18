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

@Composable
fun CustomStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    isDatabaseConnected: Boolean,
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
        IconButton(
            enabled = isDatabaseConnected && (value > valueProgression.first),
            onClick = { if (value > valueProgression.first) onValueChange(-60) }
        ) {
            Icon(StepperDefaults.Decrease, "Decrease")
        }

        // Value in the middle
        valueContent(value)

        // Increase button on the right
        IconButton(
            enabled = isDatabaseConnected,
            onClick = { if (value < valueProgression.last) onValueChange(60) }
        ) {
            Icon(StepperDefaults.Increase, "Increase")
        }
    }
}

@WearPreviewDevices
@Composable
fun StepperPreview() {
    CustomStepper(
        value = 4,
        onValueChange = {},
        isDatabaseConnected = false,
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

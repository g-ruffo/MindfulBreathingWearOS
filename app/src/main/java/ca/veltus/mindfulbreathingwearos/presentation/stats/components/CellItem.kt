package ca.veltus.mindfulbreathingwearos.presentation.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.presentation.stats.StatsScreen

@Composable
fun CellItem(stats: Resource<DatabaseStats>) {
    val tealColor = Color(0xFF008080)  // Teal color
    val opaqueTeal = Color(0xFF20B2AA).copy(alpha = 0.1f)  // 10% alpha

    // This state will be used to get the combined height of the two Text fields
    var textFieldsHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 16.dp)  // Reduced vertical padding
            .background(opaqueTeal, shape = RoundedCornerShape(50))  // Capsule shape
            .border(1.dp, opaqueTeal, shape = RoundedCornerShape(50))
            .padding(horizontal = 16.dp),  // Removed vertical padding here
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Icon adjusted according to the combined height of the two Text fields
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = "Folder icon",
            tint = tealColor,  // Icon color set to teal
            modifier = Modifier
                .size(textFieldsHeight * 0.8f)  // Set icon size to 80% of the combined text height
                .padding(end = 8.dp)
        )

        // The combined height of the two Text fields is measured and set to the textFieldsHeight state
        BoxWithConstraints(
            Modifier.layoutId("textFieldsBox")
                .onGloballyPositioned { layoutCoordinates ->
                    textFieldsHeight = layoutCoordinates.size.height.dp
                }
        ) {
            Column {
                Text(text = "1234", style = TextStyle(fontWeight = FontWeight.Bold, color = tealColor))  // Bold text set to teal color
                Text(text = "Last Synced", fontSize = 12.sp, color = Color.Gray)  // Smaller font size and gray color for the "Last Synced" text
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun SessionScreenPreview() {
    val stats = Resource.Success(DatabaseStats(count = 43563, lastAddedDate = "August 11, 2023 12:00:22"))
    CellItem(stats = stats)
}
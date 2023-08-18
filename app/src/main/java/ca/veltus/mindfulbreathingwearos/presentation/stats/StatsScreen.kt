package ca.veltus.mindfulbreathingwearos.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.R
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.presentation.stats.components.CellItem
import ca.veltus.mindfulbreathingwearos.presentation.stats.components.CustomStepper

@Composable
fun StatsScreen(
    uncachedStats: Resource<DatabaseStats>,
    cacheStats: Resource<DatabaseStats>,
    databaseStats: Resource<DatabaseStats>,
    isDatabaseEnabled: Boolean,
    startStopPressed: () -> Unit,
    stepperPressed: (Int) -> Unit,
    time: Int
) {
    val tealColor = Color(0xFF03A1A1)
    val orangeColor = Color(0xFFDB8C02)
    val orangeOpaque = Color(0xFF20B2AA).copy(alpha = 0.1f)

            LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            Spacer(modifier = Modifier.padding(vertical = 14.dp))

            Icon(
                imageVector = if (isDatabaseEnabled) {
                    Icons.Default.Cloud
                } else {
                    Icons.Default.CloudOff
                },
                contentDescription = "Database enabled status icon",
                tint = if (isDatabaseEnabled) tealColor else Color.Red
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

        }
        item {
            CustomStepper(
                value = time,
                onValueChange = { value -> stepperPressed(value) },
                isDatabaseConnected = isDatabaseEnabled,
                valueProgression = 0..9999
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isDatabaseEnabled) {
                        val minutes = it / 60
                        Text("$minutes")
                        Text(text = "min", fontSize = 8.sp,)
                    } else {
                        val displayMinutes = it / 60
                        val displaySeconds = it % 60
                        Text("${displayMinutes.toString().padStart(2, '0')}:${displaySeconds.toString().padStart(2, '0')}")
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Button(
                enabled = time > 0,
                onClick = {
                    startStopPressed()
                },
                colors = ButtonDefaults.buttonColors(
                    if (isDatabaseEnabled) orangeColor else orangeOpaque
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .background(orangeOpaque, shape = RoundedCornerShape(50))
                    .border(
                        1.dp, if (time > 0) orangeColor else tealColor,
                        shape = RoundedCornerShape(50)
                    )
                    .alpha(if (time > 0) 1.0f else 0.2f),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = if (isDatabaseEnabled) "Disable" else "Enable",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
        }
        items(3) { index ->
            when(index) {
                0 -> CellItem(uncachedStats, imagePainter = painterResource(id = R.drawable.no_sim), name = "Uncached")
                1 -> CellItem(cacheStats, imagePainter = painterResource(id = R.drawable.sd_storage), name = "Cached")
                2 -> CellItem(databaseStats, imagePainter = painterResource(id = R.drawable.database), name = "Saved", databaseEnabled = isDatabaseEnabled,bottomPadding = 12.dp)
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
        isDatabaseEnabled = true,
        startStopPressed = {},
    stepperPressed = {},
    time = 40
    )
}

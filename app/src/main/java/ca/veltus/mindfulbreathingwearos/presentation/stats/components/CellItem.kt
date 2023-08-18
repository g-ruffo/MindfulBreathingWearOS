package ca.veltus.mindfulbreathingwearos.presentation.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.R
import ca.veltus.mindfulbreathingwearos.common.Resource
import ca.veltus.mindfulbreathingwearos.domain.model.DatabaseStats
import ca.veltus.mindfulbreathingwearos.presentation.stats.StatsScreen

@Composable
fun CellItem(
    stats: Resource<DatabaseStats>,
    imagePainter: Painter,
    name: String,
    bottomPadding: Dp = 2.dp,
    databaseEnabled: Boolean = true
    ) {
    val tealColor = Color(0xFF03A1A1)
    val opaqueTeal = tealColor.copy(alpha = 0.1f)
    val opaqueRed = Color.Red.copy(alpha = 0.1f)

    var textFieldsHeight by remember { mutableStateOf(0.dp) }

    var rememberedDateText by rememberSaveable { mutableStateOf("--") }

    when (stats) {
        is Resource.Success -> {
            val newDateText = stats.data?.lastAddedDate ?: rememberedDateText
            rememberedDateText = newDateText
            newDateText
        }
        is Resource.Error -> rememberedDateText
        else -> rememberedDateText
    }

    val count = when (stats) {
        is Resource.Success -> stats.data?.count ?: "??"
        is Resource.Error -> "??"
        else -> "--"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.95f)
            .padding(
                top = 2.dp,
                bottom = bottomPadding,
                start = 8.dp,
                end = 16.dp
            )
            .background(
                if (databaseEnabled) opaqueTeal else opaqueRed,
                shape = RoundedCornerShape(50)
            )
            .border(
                1.dp,
                if (databaseEnabled) opaqueTeal else opaqueRed,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        Icon(
            painter = imagePainter,
            contentDescription = "Storage icon",
            tint = tealColor,
            modifier = Modifier
                .size(textFieldsHeight * 0.6f)
                .padding(end = 4.dp)
        )

        BoxWithConstraints(
            Modifier
                .layoutId("textFieldsBox")
                .onGloballyPositioned { layoutCoordinates ->
                    textFieldsHeight = layoutCoordinates.size.height.dp
                }
        ) {
            Column {
                Row {
                Text(text = "$count", style = TextStyle(fontWeight = FontWeight.Bold, color = tealColor))
                    Text(text = "$name", fontSize = 6.sp, color = tealColor, modifier = Modifier
                        .padding(start = 4.dp))
                }
                Spacer(modifier = Modifier.padding(vertical = 1.dp))
                Row {
                    Text(text = "$rememberedDateText", fontSize = 9.sp, color = Color.LightGray)
                    Text(text = "Updated", fontSize = 6.sp, color = Color.LightGray, modifier = Modifier
                        .padding(start = 4.dp))
                }
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun SessionScreenPreview() {
    val stats = Resource.Success(DatabaseStats(count = 43563, lastAddedDate = "August 11, 2023 12:00:22"))
    CellItem(stats = stats, imagePainter = painterResource(id = R.drawable.database), name = "Cache")
}
package ca.veltus.mindfulbreathingwearos.presentation.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedModifier
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.basicCurvedText
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.foundation.padding
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.common.Resource


@Composable
fun CacheAndDatabaseCountDisplay(
    cacheCount: Resource<Int>,
    databaseCount: Resource<Int>
) {
    val databaseText = when (databaseCount) {
        is Resource.Success -> databaseCount.data.toString()
        is Resource.Error -> "??"
        is Resource.Loading -> "--"
    }

    val cacheText = when (cacheCount) {
        is Resource.Success -> cacheCount.data.toString()
        is Resource.Error -> "??"
        is Resource.Loading -> "--"
    }
    val state = LocalConfiguration.current.isScreenRound
    if (state) {
        CurvedLayout(modifier = Modifier.fillMaxSize()) {
            basicCurvedText(
                "Database: $databaseText",
                CurvedModifier.padding(10.dp),
                style = {
                    CurvedTextStyle(
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            )
            basicCurvedText(
                "Cache: $cacheText",
                CurvedModifier.padding(10.dp),
                style = {
                    CurvedTextStyle(
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            )
        }
    } else {
        CurvedLayout(modifier = Modifier.fillMaxSize()) {
            curvedComposable {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Gray)
                )
            }
            curvedComposable {
                BasicText(
                    "Normal Text",
                    Modifier.padding(5.dp),
                    TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                        background = Color.White
                    )
                )
            }
        }
    }
}



@Composable
@WearPreviewDevices
fun CacheAndDatabaseCountDisplayPreview() {
    CacheAndDatabaseCountDisplay(cacheCount = Resource.Success(data = 90), databaseCount = Resource.Success(data = 20))
}
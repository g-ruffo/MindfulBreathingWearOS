package ca.veltus.mindfulbreathingwearos.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import ca.veltus.mindfulbreathingwearos.R

@Composable
fun Unavailable() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Block,
            contentDescription = stringResource(id = R.string.not_supported),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            tint = Color.Red
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Black)
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.not_supported),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.title3,
                color = Color.White
            )
        }
    }
}

@WearPreviewDevices
@Composable
fun UnavailablePreview() {
    Unavailable()
}
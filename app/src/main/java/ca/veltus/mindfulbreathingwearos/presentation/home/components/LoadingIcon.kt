package ca.veltus.mindfulbreathingwearos.presentation.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices

@Composable
fun LoadingIcon() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@WearPreviewDevices
@Composable
fun PreviewLoadingIcon() {
    LoadingIcon()
}
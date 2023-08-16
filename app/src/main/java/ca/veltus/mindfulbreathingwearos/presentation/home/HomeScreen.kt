package ca.veltus.mindfulbreathingwearos.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.veltus.mindfulbreathingwearos.presentation.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import ca.veltus.mindfulbreathingwearos.presentation.Navigation

@Composable
fun HomeScreen(navController: NavController) {
    var text by remember {
        mutableStateOf("Home Screen")
    }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)

    ) {
        Button(
            onClick = {
                navController.navigate(Screen.SessionScreen.withArgs(text))
            },
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
        ) {
            Text(text = "To Detail Screen")
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}


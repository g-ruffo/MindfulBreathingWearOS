package ca.veltus.mindfulbreathingwearos.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ca.veltus.mindfulbreathingwearos.presentation.home.HomeScreen
import ca.veltus.mindfulbreathingwearos.presentation.session.SessionScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.SessionScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Grayson"
                    nullable = true
                }
            )
        ) { entry ->
            SessionScreen(name = entry.arguments?.getString("name"))
        }
    }
}
package ca.veltus.mindfulbreathingwearos.presentation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object StatsScreen : Screen("stats_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

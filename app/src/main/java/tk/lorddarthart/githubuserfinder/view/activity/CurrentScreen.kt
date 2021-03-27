package tk.lorddarthart.githubuserfinder.view.activity

sealed class CurrentScreen {
    object AuthScreen: CurrentScreen(),
    object MainScreen: CurrentScreen()
}
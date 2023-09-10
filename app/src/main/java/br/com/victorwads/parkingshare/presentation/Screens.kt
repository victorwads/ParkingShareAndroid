package br.com.victorwads.parkingshare.presentation

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object UserInfo : Screens("userInfo")
    object Home : Screens("home")
    object Places : Screens("places")
    object Place : Screens("place")
    object ParkingEditor : Screens("parkingEditor")
    object ParkingView : Screens("parkingView")
    object About : Screens("about")
    object Settings : Screens("settings")
}
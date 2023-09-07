package br.com.victorwads.parkingshare.presentation.screens

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object UserInfo : Screens("userInfo")
    object Home : Screens("home")
    object ParkingEditor : Screens("parkingEditor")
    object ParkingView : Screens("parkingView")
}
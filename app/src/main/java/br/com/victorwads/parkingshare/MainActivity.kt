package br.com.victorwads.parkingshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.victorwads.parkingshare.data.UserRepository
import br.com.victorwads.parkingshare.presentation.screens.Screens
import br.com.victorwads.parkingshare.presentation.screens.home.HomeScreen
import br.com.victorwads.parkingshare.presentation.screens.login.LoginScreenWithGoogle
import br.com.victorwads.parkingshare.presentation.screens.parking.ParkingEditorScreen
import br.com.victorwads.parkingshare.presentation.theme.ParkingShareTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            ParkingShareTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screens.Login.route
                    ) {
                        loginGraph(navController)
                        mvp(navController)
                    }

                }
            }
        }
    }

    private fun NavGraphBuilder.loginGraph(navController: NavController) {
        composable(Screens.Login.route) {
            LoginScreenWithGoogle {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
                UserRepository.shared
                    .createUserIfNotExists(packageManager.getPackageInfo(packageName, 0))
            }
        }
    }

    private fun NavGraphBuilder.mvp(navController: NavController) {
        composable(Screens.Home.route) {
            HomeScreen(navController)
        }

        composable(Screens.ParkingEditor.route) {
            ParkingEditorScreen()
        }
    }

}

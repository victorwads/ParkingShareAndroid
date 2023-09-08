package br.com.victorwads.parkingshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.victorwads.parkingshare.data.UserRepository
import br.com.victorwads.parkingshare.presentation.screens.Screens
import br.com.victorwads.parkingshare.presentation.screens.login.LoginScreenWithGoogle
import br.com.victorwads.parkingshare.presentation.screens.parking.DragAndDropSquares
import br.com.victorwads.parkingshare.presentation.screens.parking.ParkingEditViewModel
import br.com.victorwads.parkingshare.presentation.theme.ParkingShareTheme
import com.google.firebase.auth.FirebaseAuth

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
            Column {
                Button(
                    onClick = { navController.navigate(Screens.ParkingEditor.route) }
                ) { Text("Editor") }
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screens.Login.route)
                    }
                ) { Text("LogOut") }
            }
        }

        composable(Screens.ParkingEditor.route) {
            val viewModel: ParkingEditViewModel = viewModel()
            Column(modifier = Modifier.fillMaxSize()) {
                Button(onClick = { viewModel.addParkingSpot() }) {
                    Text("Add")
                }
                DragAndDropSquares(viewModel = viewModel)
            }
        }
    }

}

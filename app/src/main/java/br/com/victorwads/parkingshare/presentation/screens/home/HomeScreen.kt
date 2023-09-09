package br.com.victorwads.parkingshare.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import br.com.victorwads.parkingshare.presentation.screens.Screens
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navController: NavController
) {
    Column {
        Button(
            onClick = { navController.navigate(Screens.ParkingEditor.route) }
        ) { Text("Editor") }
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Identity.getSignInClient(navController.context).signOut()
                navController.navigate(Screens.Login.route)
            }
        ) { Text("LogOut") }
    }
}
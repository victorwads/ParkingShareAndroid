package br.com.victorwads.parkingshare.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import br.com.victorwads.parkingshare.R
import br.com.victorwads.parkingshare.presentation.Screens
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth

@Preview(showSystemUi = true)
@Composable
fun HomeScreen(
    navController: NavController? = null,
    userName: String = "Wads",
    userPhotoUrl: String? = null
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (scrollArea, bottomArea) = createRefs()
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .constrainAs(scrollArea) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomArea.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(96.dp))
            Text("Olá $userName!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Seja Bem vindo(a) ao Parking Share (Beta)", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.home_description),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Acessível a Adminstradores:", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            ElevatedButton(modifier = Modifier.padding(8.dp), onClick = {
                navController?.navigate(Screens.ParkingEditor.route)
            }) { Text("Editar Layout de Vagas") }

            Text(
                "Acessivel a Qualquer Publico:", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            ElevatedButton(modifier = Modifier.padding(8.dp), onClick = {
                navController?.navigate(Screens.ParkingView.route)
            }) { Text("Layout de Vagas") }
            ElevatedButton(onClick = {
                navController?.navigate(Screens.ParkingSearch.route)
            }) { Text("Localizar Vaga") }
            FilledTonalButton(onClick = {}, enabled = false) { Text("Procurar Vagas Disponíveis") }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Conteúdo Relacionado ao Usuário:", fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            FilledTonalButton(onClick = {}, enabled = false) { Text("Minhas Vagas") }
            Spacer(modifier = Modifier.height(8.dp))
            FilledTonalButton(onClick = {}, enabled = false) { Text("Disponibilizar Vaga") }
            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(onClick = {}, enabled = false) { Text("Meus Carros") }
            Spacer(modifier = Modifier.height(8.dp))
            FilledTonalButton(onClick = {}, enabled = false) { Text("Meus Pedidos") }
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(74.dp))
        }

        Column(
            modifier = Modifier
                .constrainAs(bottomArea) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(onClick = {
                navController?.let {
                    FirebaseAuth.getInstance().signOut()
                    Identity.getSignInClient(it.context).signOut()
                    it.navigate(Screens.Login.route)
                }
            }) { Text("Deslogar") }
        }

    }
}
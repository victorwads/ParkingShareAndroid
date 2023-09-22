package br.com.victorwads.parkingshare.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.victorwads.parkingshare.R
import br.com.victorwads.parkingshare.presentation.Screens
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

val sectionModifier = Modifier.padding(top = 16.dp, bottom = 4.dp, start = 8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun HomeScreen(
    navController: NavController? = null,
    userName: String = "Wads",
    userPhotoUrl: String? = null
) {
    val state = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerState = state,
        drawerContent = {
            ModalDrawerSheet {
                Text("Beta Flows", modifier = Modifier.padding(16.dp))

                Divider()
                Text("Acessível a Adminstradores:", fontWeight = FontWeight.Bold, modifier = sectionModifier)

                DrawerButton("Editar Layout de Vagas") {
                    navController?.navigate(Screens.ParkingEditor.route)
                }

                Divider()
                Text("Acessivel a Qualquer Publico:", fontWeight = FontWeight.Bold, modifier = sectionModifier)
                DrawerButton("Layout de Vagas") {
                    navController?.navigate(Screens.ParkingView.route)
                }
                DrawerButton("Localizar Vaga") {
                    navController?.navigate(Screens.ParkingSearch.route)
                }
                DrawerButton("Procurar Vagas Disponíveis")

                Divider()
                Text("Conteúdo Relacionado a Interações:", fontWeight = FontWeight.Bold, modifier = sectionModifier)
                DrawerButton("Meus Pedidos")

                Divider()
                Text("Conteúdo Relacionado ao Usuário:", fontWeight = FontWeight.Bold, modifier = sectionModifier)

                DrawerButton("Meus Carros")
                DrawerButton("Minhas Vagas")

                Divider()
                DrawerButton("Deslogar", Icons.Filled.ExitToApp) {
                    navController?.let {
                        FirebaseAuth.getInstance().signOut()
                        Identity.getSignInClient(it.context).signOut()
                        it.navigate(Screens.Login.route)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { state.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    title = { Text("Olá $userName!") }
                )
            },
            bottomBar = { /*TODO*/ }
        ) { contentPadding ->
            HomeScreenBody(
                contentPadding,
                navController
            )
        }
    }
}

@Composable
private fun HomeScreenBody(
    paddingValues: PaddingValues,
    navController: NavController? = null,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Olá User!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Seja Bem vindo(a) ao Parking Share (Beta)", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.home_description),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Acessível a Adminstradores:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )
        ElevatedButton(modifier = Modifier.padding(8.dp), onClick = {
            navController?.navigate(Screens.ParkingEditor.route)
        }) { Text("Editar Layout de Vagas") }

        Text(
            "Acessivel a Qualquer Publico:",
            fontWeight = FontWeight.Bold,
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
    }
}

@Composable
private fun DrawerButton(
    label: String,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) = NavigationDrawerItem(
    modifier = Modifier
        .alpha(0.5f)
        .takeIf { onClick == null }
        ?: Modifier,
    icon = { Icon(imageVector = icon ?: Icons.Filled.Clear, contentDescription = label) },
    label = { Text(label) },
    selected = false,
    onClick = onClick ?: {}
)

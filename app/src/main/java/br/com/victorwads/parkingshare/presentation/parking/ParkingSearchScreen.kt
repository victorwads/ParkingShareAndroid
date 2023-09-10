package br.com.victorwads.parkingshare.presentation.parking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory.Companion.createMediumParkingStop
import br.com.victorwads.parkingshare.presentation.parking.components.ParkingGraph
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingViewEditorErrors

@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
@Composable
fun ParkingSearchScreen(
    navController: NavController? = null,
    viewModel: ParkingEditViewModel = viewModel<ParkingEditViewModel>(factory = PreviewViewModelsFactory())
        .createMediumParkingStop()
) {
    var showDialog by remember { mutableStateOf(true) }
    var showErrorDialog by remember { mutableStateOf(false) }
    ParkingGraph(viewModel = viewModel)
    LaunchedEffect(true) {
        viewModel.loadParkingSpots()
        viewModel.errors.observeForever {
            when (it) {
                is ParkingViewEditorErrors.SpotNotFound -> showErrorDialog = true
                else -> {}
            }
        }
    }
    if (showDialog) {
        var textInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Informe a vaga") },
            text = {
                Column {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search, autoCorrect = false
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                showDialog = false
                                viewModel.findSpot(textInput)
                            }
                        )
                    )
                    Text("caso a vaga não existir, ela será criada")
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false; viewModel.findSpot(textInput); }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    val error = viewModel.errors.value
    if (showErrorDialog && error is ParkingViewEditorErrors.SpotNotFound) {
        val onDismissRequest = {
            showErrorDialog = false
            navController?.popBackStack()
            Unit
        }
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Vaga não encontrada") },
            text = {
                Text(
                    "Não encontramos a vaga com o nome ${error.id}.\n" +
                            "Gostaria de procurar novamente?"
                )
            },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false; showDialog = true }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancelar")
                }
            }
        )
    }
}
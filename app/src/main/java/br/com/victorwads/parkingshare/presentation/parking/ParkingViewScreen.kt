package br.com.victorwads.parkingshare.presentation.parking

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory.Companion.createMediumParkingStop
import br.com.victorwads.parkingshare.presentation.parking.components.ParkingGraph
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel

@Composable
fun ParkingViewScreen(
    viewModel: ParkingEditViewModel
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        ParkingGraph(viewModel = viewModel)
    }
    LaunchedEffect(true) {
        viewModel.loadParkingSpots()
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
}

@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
@Composable
fun ParkingViewEditorPreview() {
    val viewModel = viewModel<ParkingEditViewModel>(factory = PreviewViewModelsFactory())
    ParkingEditorScreen(viewModel = viewModel)
    LaunchedEffect(Unit) { createMediumParkingStop(viewModel) }
}

@Preview(device = Devices.PIXEL_4, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.TABLET, showSystemUi = true)
@Composable
fun ParkingViewEditorPreviewNight() {
    val viewModel = viewModel<ParkingEditViewModel>(factory = PreviewViewModelsFactory())
    ParkingEditorScreen(viewModel = viewModel)
    LaunchedEffect(Unit) { createMediumParkingStop(viewModel) }
}
package br.com.victorwads.parkingshare.presentation.screens.parking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ParkingEditorScreen() {
    val showDialog = remember { mutableStateOf(false) }
    val viewModel: ParkingEditViewModel = viewModel()
    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            Button(onClick = { viewModel.addParkingSpot() }) {
                Text("Add")
            }
            Button(onClick = { viewModel.loadParkingSpots() }) {
                Text("Reload")
            }
            Button(onClick = { showDialog.value = true }) {
                Text("Find")
            }
        }
        Row {
            if (viewModel.selectedSpot.value != null) {
                Button(onClick = { viewModel.rotateSpot() }) {
                    Text("Rotate")
                }
                Button(onClick = { viewModel.deleteSpot() }) {
                    Text("Delete")
                }
                Button(onClick = { viewModel.unselectSpot() }) {
                    Text("Unselect")
                }
            }
        }
        DragAndDropSquares(viewModel = viewModel)
    }
    LaunchedEffect(true) {
        viewModel.loadParkingSpots()
    }
    if (showDialog.value) {
        var textInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Informe a vaga") },
            text = {
                Column {
                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions(onDone = { showDialog.value = false })
                    )
                    Text("caso a vaga não existir ela será criada")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog.value = false
                    viewModel.findSpot(textInput)
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
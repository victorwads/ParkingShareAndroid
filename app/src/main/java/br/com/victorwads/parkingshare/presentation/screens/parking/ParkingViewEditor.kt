package br.com.victorwads.parkingshare.presentation.screens.parking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.di.ViewModelsFactory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParkingViewEditor() {
    var showDialog by remember { mutableStateOf(false) }
    val viewModel: ParkingEditViewModel = viewModel(factory = ViewModelsFactory())
    var longPress by remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxSize()) {
        FlowRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            var newItemsJump by remember { viewModel.newItemsJump }
            TextField(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(50.dp, Dp.Unspecified),
                value = newItemsJump.toString(),
                onValueChange = { newItemsJump = it.toIntOrNull() ?: 1 },
                singleLine = true,
                textStyle = TextStyle(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = { showDialog = false })
            )
            Button(onClick = { viewModel.addParkingSpot() }) {
                Text("Add")
            }
            Button(onClick = { viewModel.loadParkingSpots() }) {
                Text("Reload")
            }
            Button(onClick = { showDialog = true }) {
                Text("Find")
            }
            Button(onClick = { viewModel.center() }) {
                Text("Center")
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = longPress, onCheckedChange = { longPress = it })
                Text("LongPress", modifier = Modifier.padding(start = 8.dp, end = 8.dp))

                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { viewModel.newItemsAlignment }
                Button(onClick = { expanded = !expanded }) {
                    Text("NewAlignment:")
                    Text(selectedOption.name)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    PlaceSpot.Alignment.values().forEach { value ->
                        DropdownMenuItem(
                            text = { Text(value.name) },
                            onClick = {
                                selectedOption = value
                                expanded = false
                            }
                        )
                    }
                }
            }
            if (viewModel.selectedSpot.value != null) {
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
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
        }
        ParkingView(viewModel = viewModel, longPress = longPress)
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions(onDone = { showDialog = false })
                    )
                    Text("caso a vaga não existir ela será criada")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.findSpot(textInput)
                }) {
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
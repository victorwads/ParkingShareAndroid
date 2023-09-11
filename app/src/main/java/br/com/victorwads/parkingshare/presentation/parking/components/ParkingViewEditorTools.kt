package br.com.victorwads.parkingshare.presentation.parking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ParkingViewEditorTools(
    viewModel: ParkingEditViewModel,
    showDialog: () -> Unit = {},
    changeLongPress: (Boolean) -> Unit = {}
) {
    var longPress by remember { mutableStateOf(true) }
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        var newItemsJump by remember { mutableStateOf("") }
        val keyboardManager = LocalSoftwareKeyboardController.current
        TextField(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .size(50.dp, Dp.Unspecified),
            value = newItemsJump,
            onValueChange = {
                newItemsJump = it
            },
            isError = newItemsJump.toIntOrNull() == null,
            singleLine = true,
            textStyle = TextStyle(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done, autoCorrect = false
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.newItemsJump.intValue = newItemsJump.toIntOrNull() ?: 1
                    newItemsJump = viewModel.newItemsJump.intValue.toString()
                    keyboardManager?.hide()
                },
            )
        )
        Button(onClick = { viewModel.addParkingSpot() }) {
            Text("Add")
        }
        Button(onClick = { showDialog() }) {
            Text("Find")
        }
        Button(onClick = { viewModel.center() }) {
            Text("Center")
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = longPress, onCheckedChange = { longPress = it; changeLongPress(it) })
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
        if (viewModel.graphController.selectedSpot.value != null) {
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.rotateSelectedSpot() }) {
                    Text("Rotate")
                }
                Button(onClick = { viewModel.deleteSpot() }) {
                    Text("Delete")
                }
                Button(onClick = { viewModel.graphController.unselectSpot() }) {
                    Text("Unselect")
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
@Composable
fun ParkingViewEditorToolsPreview() {
    ParkingViewEditorTools(
        viewModel = viewModel(factory = PreviewViewModelsFactory()),
    )
}
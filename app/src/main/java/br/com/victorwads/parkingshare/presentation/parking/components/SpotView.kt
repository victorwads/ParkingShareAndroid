package br.com.victorwads.parkingshare.presentation.parking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import br.com.victorwads.parkingshare.R
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.data.models.PlaceSpot.Position
import br.com.victorwads.parkingshare.isDebug

@Composable
internal fun SpotView(
    spot: PlaceSpot,
    selected: Boolean,
    inputMode: SpotInputMode = SpotInputMode.None,
    events: ParkingViewEvents = ParkingViewEvents(),
) {
    val density: Float = LocalDensity.current.density
    var offset by remember { mutableStateOf(spot.position) }
    Box(
        modifier = Modifier
            .offset(
                x = offset.x.dp,
                y = offset.y.dp
            )
            .size(spot.size.width.dp, spot.size.height.dp)
            .border(2.dp, if (selected) Color.Green else Color.Yellow)
            .zIndex(if (selected) 20f else 10f)
            .background(Color.Transparent)
            .pointerInput(inputMode) {
                if (inputMode == SpotInputMode.LongPress) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { events.onDragStart(spot) },
                        onDragEnd = { offset = events.onDragEnd(spot, offset) },
                    ) { _, dragAmount ->
                        if (dragAmount != Offset(0f, 0f)) {
                            offset = offset.plus((dragAmount / density))
                        }
                    }
                } else if (inputMode == SpotInputMode.Touch) {
                    detectDragGestures(
                        onDragStart = { events.onDragStart(spot) },
                        onDragEnd = { offset = events.onDragEnd(spot, offset) }
                    ) { _, dragAmount ->
                        if (dragAmount != Offset(0f, 0f)) {
                            offset = offset.plus((dragAmount / density))
                        }
                    }
                }
            },
        contentAlignment = spot.size.let {
            if (it.width > it.height) Alignment.CenterEnd
            else Alignment.BottomCenter
        }
    ) {
        SpotNameView(
            modifier = Modifier
                .offset(
                    x = if (spot.size.width > spot.size.height) (-15).dp else 0.dp,
                    y = if (spot.size.width > spot.size.height) 0.dp else (-15).dp
                ),
            boxColor = if (selected) Color.Green else Color.Yellow,
            text = spot.id
        )
        if (isDebug) {
            Box(
                contentAlignment = spot.size.let {
                    if (it.width > it.height) Alignment.CenterStart
                    else Alignment.TopCenter
                },
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column {
                    Text(text = "ox: ${offset.x.toInt()}")
                    Text(text = "oy: ${offset.y.toInt()}")
                    Text(text = "sx: ${spot.position.x.toInt()}")
                    Text(text = "sy: ${spot.position.y.toInt()}")
                }
            }
        }
        if (selected && inputMode != SpotInputMode.None) {
            val dpOffset = 70.dp
            AddButton(Modifier.offset(x = -dpOffset), PlaceSpot.Alignment.LEFT, events.onAdd)
            AddButton(Modifier.offset(x = dpOffset), PlaceSpot.Alignment.RIGHT, events.onAdd)
            AddButton(Modifier.offset(y = -dpOffset), PlaceSpot.Alignment.TOP, events.onAdd)
            AddButton(Modifier.offset(y = dpOffset), PlaceSpot.Alignment.BOTTOM, events.onAdd)
        }
    }
}

sealed class SpotInputMode {
    object None : SpotInputMode()
    object LongPress : SpotInputMode()
    object Touch : SpotInputMode()
}

@Composable
private fun AddButton(
    modifier: Modifier = Modifier,
    alignment: PlaceSpot.Alignment,
    onAdd: (PlaceSpot.Alignment) -> Unit
) {
    val composeAlignment = when (alignment) {
        PlaceSpot.Alignment.TOP -> Alignment.TopCenter
        PlaceSpot.Alignment.BOTTOM -> Alignment.BottomCenter
        PlaceSpot.Alignment.LEFT -> Alignment.CenterStart
        PlaceSpot.Alignment.RIGHT -> Alignment.CenterEnd
    }
    val description = when (alignment) {
        PlaceSpot.Alignment.TOP -> R.string.add_top
        PlaceSpot.Alignment.BOTTOM -> R.string.add_bottom
        PlaceSpot.Alignment.LEFT -> R.string.add_left
        PlaceSpot.Alignment.RIGHT -> R.string.add_right
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = composeAlignment) {
        IconButton(
            modifier = Modifier.background(Color.Gray.copy(alpha = 0.6f), CircleShape),
            onClick = { onAdd(alignment) }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(description))
        }
    }

}

class ParkingViewEvents(
    val onDragStart: (PlaceSpot) -> Unit = {},
    val onDragEnd: (PlaceSpot, Position) -> Position = { _, _ -> Position(0f, 0f) },
    val onAdd: (PlaceSpot.Alignment) -> Unit = {}
)

@Preview(group = "ParkingSpot")
@Composable
fun PreviewParkingSpotSelected() {
    Box(
        modifier = Modifier.padding(75.dp),
        contentAlignment = Alignment.Center
    ) {
        SpotView(
            spot = PlaceSpot("1"),
            selected = true,
        )
    }
}

@Preview(group = "ParkingSpot")
@Composable
fun PreviewParkingSpot() {
    Box(
        modifier = Modifier.padding(75.dp),
        contentAlignment = Alignment.Center
    ) {
        SpotView(
            spot = PlaceSpot("1534"),
            selected = false,
        )
    }
}


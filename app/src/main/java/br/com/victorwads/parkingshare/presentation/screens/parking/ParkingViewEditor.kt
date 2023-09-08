package br.com.victorwads.parkingshare.presentation.screens.parking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.data.models.PlaceSpot

@Composable
fun DragAndDropSquares(
    viewModel: ParkingEditViewModel = viewModel()
) {
    val squares = viewModel.parkingSpots
    val selectedSpot by remember { viewModel.selectedSpot }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var zoomState by remember { mutableFloatStateOf(1f) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .border(1.dp, Color.Red)
            .onSizeChanged { size = it }
            .pointerInput(squares) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    Log.i("Gesture", "X: ${pan.x}, Y: ${pan.y}, Zoom: $zoom, Rotation: $rotation")
                    zoomState *= zoom
                    if (zoomState > 1f) zoomState = 1f
                    else if (zoomState < 0.1f) zoomState = 0.1f

                    offset = (offset * zoom + (pan / density))
                        .limitOut(squares, zoomState, size.width.toFloat(), size.height.toFloat())
                }
            }
    ) {
        Column {
            Text(text = "zoom: $zoomState")
            Text(text = "offset: $offset")
            Text(text = "size: $size")
        }
        squares.forEach { (id, square) ->
            key(id) {
                ParkingSpot(
                    square = square,
                    selected = id == selectedSpot?.id,
                    zoom = zoomState, boxOffset = offset,
                    onDrag = { viewModel.selectSpot(square) }
                ) { newSquare -> viewModel.saveSpotChanges(newSquare) }
            }
        }
    }
}

@Composable
private fun ParkingSpot(
    square: PlaceSpot,
    selected: Boolean,
    zoom: Float, boxOffset: Offset,
    density: Float = LocalDensity.current.density,
    onDrag: () -> Unit,
    onDragEnd: (PlaceSpot) -> Unit
) {
    var offset by remember { mutableStateOf(square.position) }
    Box(
        modifier = Modifier
            .offset(
                x = (offset.x * zoom + boxOffset.x).dp,
                y = (offset.y * zoom + boxOffset.y).dp
            )
            .scale(zoom)
            .size(square.size.width.dp, square.size.height.dp)
            .border(2.dp, if (selected) Color.Blue else Color.Yellow)
            .background(Color.Transparent)
            .pointerInput(selected) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDrag() },
                    onDragEnd = {
                        square.position = offset
                        onDragEnd(square)
                    },
                ) { _, dragAmount ->
                    if (dragAmount != Offset(0f, 0f)) {
                        offset = offset.plus((dragAmount / density))
                    }
                }
            }
    ) {
        Column {
            Text(text = "id: ${square.id}")
            Text(text = "ox: ${offset.x.toInt()}")
            Text(text = "oy: ${offset.y.toInt()}")
            Text(text = "sx: ${square.position.x.toInt()}")
            Text(text = "sy: ${square.position.y.toInt()}")
        }
    }
}

fun Offset.limitOut(
    squaresMap: Map<String, PlaceSpot>,
    zoom: Float,
    marginHorizontal: Float,
    marginVertical: Float
): Offset {
    val squares = squaresMap.map { it.value }
    val minX = squares.minOfOrNull { it.position.x } ?: 0f
    val maxX = squares.maxOfOrNull { it.position.x } ?: 0f
    val minY = squares.minOfOrNull { it.position.y } ?: 0f
    val maxY = squares.maxOfOrNull { it.position.y } ?: 0f

    return Offset(
        this.x.coerceIn(
            (maxX * (-1) + marginHorizontal) * zoom,
            (minX * (-1) + marginHorizontal) * zoom
        ),
        this.y.coerceIn(
            (maxY * (-1) + marginVertical) * zoom,
            (minY * (-1) + marginVertical) * zoom
        )
    )
}

@Preview
@Composable
fun PreviewParkingSpotSelected() {
    ParkingSpot(
        square = PlaceSpot("1"),
        selected = true,
        zoom = 1f,
        boxOffset = Offset(0f, 0f),
        onDrag = {}
    ) {}
}

@Preview
@Composable
fun PreviewParkingSpot() {
    ParkingSpot(
        square = PlaceSpot("1"),
        selected = false,
        zoom = 1f,
        boxOffset = Offset(0f, 0f),
        onDrag = {}
    ) {}
}

@Preview
@Composable
fun PreviewDragAndDropSquares() {
    val viewModel: ParkingEditViewModel = viewModel()
    viewModel.addParkingSpot()
    viewModel.addParkingSpot()
    viewModel.addParkingSpot()
    DragAndDropSquares(viewModel = viewModel)
}
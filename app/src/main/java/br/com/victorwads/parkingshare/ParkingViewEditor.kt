package br.com.victorwads.parkingshare

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DragAndDropSquares(
    viewModel: ParkingEditViewModel = viewModel()
) {
    var squares = viewModel.parkingSpots
    val selectedSpot by viewModel.selectedSpot.collectAsState()
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var zoomState by remember { mutableFloatStateOf(1f) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }

    Box(
        modifier = Modifier
            .fillMaxSize().clipToBounds()
            .border(1.dp, Color.Red)
            .onSizeChanged { size = it }
            .pointerInput(squares) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    Log.i("Gesture", "X: ${pan.x}, Y: ${pan.y}, Zoom: $zoom, Rotation: $rotation")
                    zoomState *= zoom
                    if (zoomState > 1f) zoomState = 1f
                    else if (zoomState < 0.1f) zoomState = 0.1f

                    offset = (offset * zoom + (pan / density))
                        .limitOut(squares, zoomState, 50f)
                }
            }
    ) {
        Column {
            Text(text = "zoom: $zoomState")
            Text(text = "offset: $offset")
            Text(text = "size: $size")
        }
        squares.forEach { square ->
            ParkingSpot(
                square = square,
                selected = square.id == selectedSpot?.id,
                zoom = zoomState, boxOffset = offset,
                onDrag = { viewModel.setSelectedSpot(square.id) },
                onDragEnd = { offset -> viewModel.saveSpotChanges(square, offset) }
            )
        }
    }
}

@Composable
private fun ParkingSpot(
    square: ParkingSpace,
    selected: Boolean,
    zoom: Float, boxOffset: Offset,
    density: Float = LocalDensity.current.density,
    onDrag: () -> Unit,
    onDragEnd: (Offset) -> Unit
) {
    var offset by remember { mutableStateOf(square.position) }
    Box(
        modifier = Modifier.offset(
                x = (offset.x * zoom + boxOffset.x).dp,
                y = (offset.y * zoom + boxOffset.y).dp
            )
            .scale(zoom)
            .size(square.size.width.dp, square.size.height.dp)
            .border(2.dp, if (selected) Color.Blue else Color.Yellow)
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDrag()},
                    onDragEnd = { onDragEnd(square.position) },
                ) { _, dragAmount ->
                    if (dragAmount != Offset(0f, 0f)) {
                        offset += (dragAmount / density)
                        square.position = offset
                    }
                }
            }
    ) {
        Column {
            Text(text = "x: ${square.position.x.toInt()}")
            Text(text = "y: ${square.position.y.toInt()}")
        }
    }
}

fun Offset.limitOut(
    squares: List<ParkingSpace>,
    zoom: Float,
    margin: Float
): Offset {
    val minX = squares.minOfOrNull { it.position.x } ?: 0f
    val maxX = squares.maxOfOrNull { it.position.x } ?: 0f
    val minY = squares.minOfOrNull { it.position.y } ?: 0f
    val maxY = squares.maxOfOrNull { it.position.y } ?: 0f

    return Offset(
        this.x.coerceIn((maxX * (-1) + margin) * zoom, (minX * (-1) + margin) * zoom),
        this.y.coerceIn((maxY * (-1) + margin) * zoom, (minY * (-1) + margin) * zoom)
    )
}

package br.com.victorwads.parkingshare.presentation.screens.parking

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.data.models.area
import br.com.victorwads.parkingshare.data.models.minX
import br.com.victorwads.parkingshare.data.models.minY
import br.com.victorwads.parkingshare.data.models.shadowMargin
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory
import br.com.victorwads.parkingshare.di.ViewModelsFactory
import br.com.victorwads.parkingshare.isDebug

val darkYellow = Color(0xFFDDBB00)

@Composable
fun ParkingView(
    viewModel: ParkingEditViewModel = viewModel(factory = ViewModelsFactory()),
    longPress: Boolean
) {
    val squares = viewModel.parkingSpots
    val selectedSpot by remember { viewModel.selectedSpot }
    var zoomState by remember { viewModel.zoom }
    val offset by remember { viewModel.offset }
    var size by remember { viewModel.size }
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .let {
                if (isDebug) it.border(1.dp, Color.Red)
                else it
            }
            .onSizeChanged {
                size = IntSize(
                    (it.width.toFloat() / density).toInt(),
                    (it.height.toFloat() / density).toInt()
                )
            }
            .pointerInput(squares) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    Log.i("Gesture", "X: ${pan.x}, Y: ${pan.y}, Zoom: $zoom, Rotation: $rotation")
                    zoomState *= zoom
                    if (zoomState < 0.1f) zoomState = 0.1f
                    else if (zoomState > 1f) {
                        zoomState = 1f
                        return@detectTransformGestures
                    }
                    viewModel.updateOffset((offset * zoom + (pan / density)))
                }
            }
    ) {
        if (isDebug)
            Column {
                Text(text = "zoom: $zoomState")
                Text(text = "offset: $offset")
                Text(text = "boxSize: $size")
                Text(text = "squareArea: ${squares.area}")
                Text("PonX: ${((squares.area.width.value - size.width) / 2f) + (size.width / 2f) - offset.x}")
                Text("PonY: ${((squares.area.height.value - size.height) / 2f) + (size.height / 2f) - offset.y}")
            }
        Box(
            modifier = Modifier
                .requiredSize(squares.area)
                .offset(x = offset.x.dp, y = offset.y.dp)
                .scale(zoomState)
        ) {
            Box(
                modifier = Modifier
                    .requiredSize(squares.area)
                    .offset(x = squares.minX.dp - shadowMargin, y = squares.minY.dp - shadowMargin)
                    .border(1.dp, Color.Gray)
                    .zIndex(0f)
                    .background(Color(0x11000000))
            )
            squares.forEach { (id, square) ->
                key(id) {
                    ParkingSpot(
                        square = square,
                        selected = id == selectedSpot?.id,
                        longPress = longPress,
                        onAdd = { viewModel.addParkingSpot(align = it) },
                        onDragStart = { viewModel.selectSpot(square) }
                    ) { newSquare, position -> viewModel.saveSpotChanges(newSquare, position) }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewParkingView() {
    val viewModel: ParkingEditViewModel = viewModel(factory = PreviewViewModelsFactory())
    ParkingView(viewModel = viewModel, longPress = false)
    LaunchedEffect(Unit) {
        with(viewModel) {
            addParkingSpot()
            addParkingSpot(PlaceSpot.Alignment.BOTTOM)
            addParkingSpot(PlaceSpot.Alignment.RIGHT, 2, parkingSpots["0"])
            addParkingSpot(PlaceSpot.Alignment.BOTTOM)
            unselectSpot()
            zoom.value = 0.5f
            center()
        }
    }
}
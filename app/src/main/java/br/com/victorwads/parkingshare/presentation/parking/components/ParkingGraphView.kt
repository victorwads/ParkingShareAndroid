package br.com.victorwads.parkingshare.presentation.parking.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
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
import br.com.victorwads.parkingshare.isDebug
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel

@Composable
fun ParkingGraph(
    controller: ParkingGraphViewController = ParkingGraphViewController(),
    inputMode: SpotInputMode = SpotInputMode.None,
    maxZoom: Float = 1f,
    minZoom: Float = 0.1f,
    onAddSpot: (PlaceSpot.Alignment, PlaceSpot?) -> Unit = { _, _ -> },
    onItemChanged: (PlaceSpot, PlaceSpot.Position) -> PlaceSpot.Position = { _, p -> p }
) {
    val squares = controller.parkingSpots
    val selectedSpot by remember { controller.selectedSpot }
    var zoomState by remember { controller.zoom }
    var rotationState by remember { controller.rotation }
    val offset by remember { controller.offset }
    var size by remember { controller.size }
    val density = LocalDensity.current.density

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .let {
                if (inputMode is SpotInputMode.None) {
                    it.rotate(rotationState)
                } else {
                    it
                }
            }
            .let {
                if (isDebug) {
                    it.border(1.dp, Color.Red)
                } else {
                    it
                }
            }
            .onSizeChanged {
                size = IntSize(
                    (it.width.toFloat() / density).toInt(),
                    (it.height.toFloat() / density).toInt()
                )
            }
            .pointerInput(squares) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    zoomState *= zoom
                    rotationState += rotation
                    var calcZoom = 1f
                    if (zoomState < minZoom) {
                        zoomState = minZoom
                    } else if (zoomState > maxZoom) {
                        zoomState = maxZoom
                    } else {
                        calcZoom = zoom
                    }
                    (offset * calcZoom + (pan / density))
                }
            }
    ) {
        if (isDebug) {
            Column(
                modifier = Modifier.let {
                    if (inputMode is SpotInputMode.None) {
                        it.rotate(-rotationState)
                    } else {
                        it
                    }
                }
            ) {
                Text(text = "zoom: $zoomState")
                Text(text = "offset: $offset")
                Text(text = "boxSize: $size")
                Text(text = "squareArea: ${squares.area}")
                Text(text = "rotation: $rotationState")
                Text("PonX: ${((squares.area.width.value - size.width) / 2f) + (size.width / 2f) - offset.x}")
                Text("PonY: ${((squares.area.height.value - size.height) / 2f) + (size.height / 2f) - offset.y}")
            }
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
                    .background(Color(0x05000000))
            )
            squares.forEach { (id, spot) ->
                key(id) {
                    SpotView(
                        spot = spot,
                        selected = id == selectedSpot?.id,
                        inputMode = inputMode,
                        events = ParkingViewEvents(
                            onDragStart = { controller.selectSpot(it) },
                            onDragEnd = { it, position ->
                                onItemChanged(it, position)
                            },
                            onAdd = {
                                onAddSpot(it, spot)
                            }
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewParkingView() {
    val viewModel: ParkingEditViewModel = viewModel(factory = PreviewViewModelsFactory())
    ParkingGraph(controller = viewModel.graphController)
    LaunchedEffect(Unit) {
        with(viewModel) {
            addParkingSpot()
            addParkingSpot(PlaceSpot.Alignment.BOTTOM)
            addParkingSpot(
                PlaceSpot.Alignment.RIGHT,
                2,
                from = viewModel.graphController.parkingSpots["0"]
            )
            addParkingSpot(PlaceSpot.Alignment.BOTTOM)
            viewModel.graphController.unselectSpot()
            viewModel.graphController.zoom.floatValue = 0.5f
            viewModel.graphController.animateToCenter(null)
        }
    }
}

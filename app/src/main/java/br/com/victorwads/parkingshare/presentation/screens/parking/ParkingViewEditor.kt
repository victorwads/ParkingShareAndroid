package br.com.victorwads.parkingshare.presentation.screens.parking

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.BuildConfig
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.data.models.PlaceSpot.Position

val darkYellow = Color(0xFFDDBB00)

@Composable
fun DragAndDropSquares(
    viewModel: ParkingEditViewModel = viewModel()
) {
    val squares = viewModel.parkingSpots
    val selectedSpot by remember { viewModel.selectedSpot }
    var zoomState by remember { viewModel.zoom }
    var offset by remember { viewModel.offset }
    var size by remember { viewModel.size }
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .border(1.dp, Color.Red)
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
        if (BuildConfig.DEBUG)
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
            squares.forEach { (id, square) ->
                key(id) {
                    ParkingSpot(
                        square = square,
                        selected = id == selectedSpot?.id,
                        onDragStart = { viewModel.selectSpot(square) }
                    ) { newSquare, position -> viewModel.saveSpotChanges(newSquare, position) }
                }
            }
            Box(
                modifier = Modifier
                    .requiredSize(squares.area)
                    .offset(x = squares.minX.dp - shadowMargin, y = squares.minY.dp - shadowMargin)
                    .border(1.dp, Color.Gray)
                    .background(Color(0x33000000))
            )
        }
    }
}

@Composable
private fun ParkingSpot(
    square: PlaceSpot,
    selected: Boolean,
    onDragStart: () -> Unit = {},
    onDragEnd: (PlaceSpot, Position) -> Position = { _, _ -> Position(0f, 0f) }
) {
    val density: Float = LocalDensity.current.density
    var offset by remember { mutableStateOf(square.position) }
    Box(
        modifier = Modifier
            .offset(
                x = offset.x.dp,
                y = offset.y.dp
            )
            .size(square.size.width.dp, square.size.height.dp)
            .border(2.dp, if (selected) Color.White else darkYellow)
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDragEnd = {
                        offset = onDragEnd(square, offset)
                    },
                ) { _, dragAmount ->
                    if (dragAmount != Offset(0f, 0f)) {
                        offset = offset.plus((dragAmount / density))
                    }
                }
            },
        contentAlignment = square.size.let {
            if (it.width > it.height) Alignment.CenterEnd
            else Alignment.BottomCenter
        }
    ) {
        TextWithBorder(
            modifier = Modifier
                .offset(
                    x = if (square.size.width > square.size.height) (-15).dp else 0.dp,
                    y = if (square.size.width > square.size.height) 0.dp else (-15).dp
                ),
            boxColor = if (selected) Color.White else darkYellow,
            text = square.id
        )
        if (BuildConfig.DEBUG) {
            Box(
                contentAlignment = square.size.let {
                    if (it.width > it.height) Alignment.CenterStart
                    else Alignment.TopCenter
                },
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column() {
                    Text(text = "ox: ${offset.x.toInt()}")
                    Text(text = "oy: ${offset.y.toInt()}")
                    Text(text = "sx: ${square.position.x.toInt()}")
                    Text(text = "sy: ${square.position.y.toInt()}")
                }
            }
        }
    }
}

@Composable
fun TextWithBorder(
    modifier: Modifier = Modifier,
    boxColor: Color,
    text: String,
    size: Float = 38f,
) {
    val textPaintStroke = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        textSize = size
        color = android.graphics.Color.BLACK
        strokeWidth = size * 0.25f
        strokeMiter = size * 0.25f
        strokeJoin = android.graphics.Paint.Join.ROUND
    }
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL
        textSize = size
        color = android.graphics.Color.WHITE
    }
    val density = LocalDensity.current.density
    val heightSize = size / density * 1.2
    val widthSize = size / density * 0.6
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp, 30.dp)
            .background(boxColor, CircleShape)
    ) {
        Canvas(
            modifier = Modifier.size((widthSize * text.length).dp, heightSize.dp),
            onDraw = {
                drawIntoCanvas {
                    it.nativeCanvas.drawText(text, 0f, size, textPaintStroke)
                    it.nativeCanvas.drawText(text, 0f, size, textPaint)
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewParkingSpotSelected() {
    ParkingSpot(
        square = PlaceSpot("1"),
        selected = true,
    )
}

@Preview
@Composable
fun PreviewParkingSpot() {
    ParkingSpot(
        square = PlaceSpot("15"),
        selected = false,
    )
}

@Preview
@Composable
fun PreviewDragAndDropSquares() {
    val viewModel: ParkingEditViewModel = viewModel()
    viewModel.addParkingSpot(save = false)
    viewModel.addParkingSpot(save = false)
    viewModel.addParkingSpot(save = false)
    DragAndDropSquares(viewModel = viewModel)
}
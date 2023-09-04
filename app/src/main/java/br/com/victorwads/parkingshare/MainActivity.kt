package br.com.victorwads.parkingshare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import br.com.victorwads.parkingshare.ui.theme.ParkingShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkingShareTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Greeting("Android")
                        Greeting("Android")
                        Greeting("Android")
                        DragAndDropSquares()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! gdgtrfgttf  jhhjiuu",
        modifier = modifier
    )
    Text(
        text = "Hello $name! gdgtrfgttf  jhhjiuu",
        modifier = modifier
    )
}

@Composable
fun DragAndDropSquares() {
    var squares by remember { mutableStateOf(generateInitialSquares()) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var zoomState by remember { mutableFloatStateOf(1f) }
    var size by remember { mutableStateOf(IntSize(0,0)) }

    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.Red)
            .clipToBounds()
            .onSizeChanged {
                size = it
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    Log.i("Gesture", "X: ${pan.x}, Y: ${pan.y}, Zoom: $zoom, Rotation: $rotation")
                    zoomState *= zoom
                    if (zoomState > 1f) zoomState = 1f
                    else if (zoomState < 0.1f) zoomState = 0.1f

                    offset = (offset * zoom + (pan / density))
                        .limitOut(squares, zoomState, size, 50f)
                }
            }
    ) {
        Column {
            Text(text = "zoom: $zoomState")
            Text(text = "offset: $offset")
            Text(text = "size: $size")
        }
        squares.forEachIndexed { index, square ->
            Box(
                modifier = Modifier
                    .size(square.size.width.dp, square.size.height.dp)
                    .offset(
                        x = (square.position.x * zoomState + offset.x).dp,
                        y = (square.position.y * zoomState + offset.y).dp
                    )
                    .scale(zoomState)
                    .border(2.dp, Color.Blue)
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            if (dragAmount != Offset(0f, 0f)) {
                                squares = squares
                                    .mapIndexed { i, it ->
                                        if (i == index) {
                                            val dpPan = dragAmount / density
                                            it.copy(position = it.position + dpPan)
                                        } else {
                                            it
                                        }
                                    }
                            }
                        }
                    }
            ) {
                Column {
                    Text(text = "x: ${square.position.x.toInt()}", color = Color.Black)
                    Text(text = "y: ${square.position.y.toInt()}", color = Color.Black)
                }
            }
        }
    }
}

fun Offset.limitOut(squares: List<Square>, zoom: Float, size: IntSize, margin: Float): Offset {
    val minX = squares.minOfOrNull { it.position.x } ?: 0f
    val maxX = squares.maxOfOrNull { it.position.x } ?: 0f
    val minY = squares.minOfOrNull { it.position.y } ?: 0f
    val maxY = squares.maxOfOrNull { it.position.y } ?: 0f

    return Offset(
        this.x.coerceIn((maxX * (-1) +  margin) * zoom, (minX * (-1) + margin) * zoom),
        this.y.coerceIn((maxY * (-1) + margin) * zoom, (minY * (-1) + margin) * zoom)
    )
}

data class Square(val position: Offset, val size: Size = Size(100f,200f)) {
    val maxPosition: Offset
        get() = position.copy(position.x + size.width, position.y + size.height)
}

fun generateInitialSquares(): List<Square> {
    return List(5) {
        Square(position = Offset(0f, it * 70f))
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParkingShareTheme {
        Greeting("Android")
    }
}
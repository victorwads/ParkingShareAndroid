package br.com.victorwads.parkingshare

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
                    Greeting("Android")
                    Greeting("Android")
                    Greeting("Android")
                    DragAndDropSquares()
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
    var zoomState by remember { mutableStateOf(1f) }

    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    Log.i("Gesture", "X: ${pan.x}, Y: ${pan.y}, Zoom: $zoom, Rotation: $rotation")
                    zoomState *= zoom
                    offset = (offset * zoom + pan)
                }
            }
    ) {
        squares.forEachIndexed { index, square ->
            Box(
                modifier = Modifier
                    .size(50.dp, 100.dp)
                    .offset(
                        x = (square.position.x * zoomState + offset.x).dp,
                        y = (square.position.y * zoomState + offset.y).dp
                    )
                    .scale(zoomState)
                    .border(2.dp, Color.Blue)
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, _, _ ->
                            if (pan != Offset(0f, 0f)) {
                                squares = squares.mapIndexed { i, it ->
                                    if (i == index) {
                                        val dpPan = pan / density
                                        it.copy(position = it.position + dpPan)
                                    } else {
                                        it
                                    }
                                }
                            }
                        }
                    }
            )
        }
    }
}

data class Square(val position: Offset)

fun generateInitialSquares(): List<Square> {
    return List(5) {
        Square(position = Offset(it * 70f, it * 70f))
    }
}

fun List<Square>.moveElement(fromIndex: Int, toIndex: Int): List<Square> {
    val result = toMutableList()
    val element = result.removeAt(fromIndex)
    result.add(toIndex, element)
    return result
}

fun findDraggingSquareIndex(squares: List<Square>, pointerPosition: Offset): Int {
    return squares.indexOfFirst { square ->
        val squareBounds = square.position.bounds()
        pointerPosition.isInBounds(squareBounds)
    }
}

fun Offset.bounds(): Rect {
    return Rect(
        left = x,
        top = y,
        right = x,
        bottom = y
    )
}

fun Offset.isInBounds(bounds: Rect): Boolean {
    return x >= bounds.left && x <= bounds.right && y >= bounds.top && y <= bounds.bottom
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParkingShareTheme {
        Greeting("Android")
    }
}
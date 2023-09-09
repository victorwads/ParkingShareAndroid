package br.com.victorwads.parkingshare.presentation.screens.parking

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.data.models.PlaceSpot.Position
import br.com.victorwads.parkingshare.isDebug
import br.com.victorwads.parkingshare.presentation.components.TextWithBorder

@Composable
internal fun ParkingSpot(
    square: PlaceSpot,
    selected: Boolean,
    longPress: Boolean = false,
    onAdd: (PlaceSpot.Alignment) -> Unit = {},
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
            .border(2.dp, if (selected) Color.Green else Color.Yellow)
            .zIndex(if (selected) 20f else 10f)
            .background(Color.Transparent)
            .pointerInput(longPress) {
                if (longPress) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { onDragStart() },
                        onDragEnd = { offset = onDragEnd(square, offset) },
                    ) { _, dragAmount ->
                        if (dragAmount != Offset(0f, 0f)) {
                            offset = offset.plus((dragAmount / density))
                        }
                    }
                } else {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDragEnd = { offset = onDragEnd(square, offset) }
                    ) { _, dragAmount ->
                        if (dragAmount != Offset(0f, 0f)) {
                            offset = offset.plus((dragAmount / density))
                        }
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
            boxColor = if (selected) Color.Green else Color.Yellow,
            text = square.id
        )
        if (isDebug) {
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
        if (selected) {
            val dpOffset = 70.dp
            val css = Modifier.background(Color.Gray.copy(alpha = 0.6f), CircleShape)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = -dpOffset), contentAlignment = Alignment.CenterStart
            ) {
                IconButton(modifier = css, onClick = { onAdd(PlaceSpot.Alignment.LEFT) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar a Esquerda")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = dpOffset), contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(modifier = css, onClick = { onAdd(PlaceSpot.Alignment.RIGHT) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar a Direita")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = -dpOffset), contentAlignment = Alignment.TopCenter
            ) {
                IconButton(modifier = css, onClick = { onAdd(PlaceSpot.Alignment.TOP) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar em Cima")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = dpOffset), contentAlignment = Alignment.BottomCenter
            ) {
                IconButton(modifier = css, onClick = { onAdd(PlaceSpot.Alignment.BOTTOM) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar em Baixo")
                }
            }
        }
    }
}

@Preview(group = "ParkingSpot")
@Composable
fun PreviewParkingSpotSelected() {
    Box(
        modifier = Modifier.padding(75.dp),
        contentAlignment = Alignment.Center
    ) {
        ParkingSpot(
            square = PlaceSpot("1"),
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
        ParkingSpot(
            square = PlaceSpot("1534"),
            selected = false,
        )
    }
}


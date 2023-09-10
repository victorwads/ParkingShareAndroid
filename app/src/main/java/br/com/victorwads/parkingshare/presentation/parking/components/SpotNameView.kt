package br.com.victorwads.parkingshare.presentation.parking.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SpotNameView(
    modifier: Modifier = Modifier,
    boxColor: Color,
    text: String,
    size: Float = 30f,
) {
    val textPaintStroke = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        textSize = size
        color = android.graphics.Color.BLACK
        strokeWidth = size * 0.1f
        strokeMiter = size * 0.1f
        strokeJoin = android.graphics.Paint.Join.ROUND
    }
    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL_AND_STROKE
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
fun SpotNameViewPreview() {
    SpotNameView(
        boxColor = Color.DarkGray,
        text = "A1",
        size = 30f
    )
}
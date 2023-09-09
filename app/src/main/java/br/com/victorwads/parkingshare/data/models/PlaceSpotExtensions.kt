package br.com.victorwads.parkingshare.data.models

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.victorwads.parkingshare.presentation.screens.parking.shadowMargin

val Map<String, PlaceSpot>.minX
    get() = this.minOfOrNull { it.value.position.x } ?: 0f
val Map<String, PlaceSpot>.minY
    get() = this.minOfOrNull { it.value.position.y } ?: 0f
val Map<String, PlaceSpot>.maxX
    get() = this.maxOfOrNull { it.value.position.x + it.value.size.width } ?: 0f
val Map<String, PlaceSpot>.maxY
    get() = this.maxOfOrNull { it.value.position.y + it.value.size.height } ?: 0f
val Map<String, PlaceSpot>.centerX
    get() = (minX + maxX) / 2
val Map<String, PlaceSpot>.centerY
    get() = (minY + maxY) / 2
val Map<String, PlaceSpot>.boxSpot
    get() = PlaceSpot(position = PlaceSpot.Position(centerX, centerY))

val Map<String, PlaceSpot>.area
    get() = DpSize(
        (maxX - minX).dp + (shadowMargin * 2),
        (maxY - minY).dp + (shadowMargin * 2)
    )
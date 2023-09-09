package br.com.victorwads.parkingshare.presentation.screens.parking

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.victorwads.parkingshare.data.ParkingSpotsRepository
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ParkingEditViewModel : ViewModel() {

    private val parkingSpotsRepository = ParkingSpotsRepository()
    private val tempFloor = "T"

    val parkingSpots = mutableStateMapOf<String, PlaceSpot>()
    val selectedSpot = mutableStateOf<PlaceSpot?>(null)
    val zoom = mutableFloatStateOf(0f)
    val offset = mutableStateOf(Offset(0f, 0f))
    val size = mutableStateOf(IntSize(0, 0))

    fun loadParkingSpots() {
        viewModelScope.launch {
            parkingSpotsRepository.getAllSpots(tempFloor).let {
                parkingSpots.clear()
                parkingSpots.putAll(it)
                animateToSpot(parkingSpots.boxSpot, 0.1f)
            }
        }
    }

    fun findSpot(id: String) {
        parkingSpots[id]?.let {
            selectedSpot.value = it
            animateToSpot(it)
        } ?: addParkingSpot(name = id)
    }

    fun addParkingSpot(name: String? = null, save: Boolean = true) {
        val newName = name ?: parkingSpots.size.toString()
        val newSpot = selectedSpot.value?.let {
            PlaceSpot(
                id = newName,
                floor = tempFloor,
                position = it.position + Offset(it.size.width, 0f)
            )
        } ?: PlaceSpot(newName)
        parkingSpots[newName] = newSpot
        selectedSpot.value = newSpot
        if (save) saveSpotChanges(newSpot)
        animateToSpot(newSpot)
    }

    fun saveSpotChanges(spot: PlaceSpot, position: PlaceSpot.Position): PlaceSpot.Position {
        spot.position = position
        saveSpotChanges(spot)
        return spot.position
    }

    fun selectSpot(spot: PlaceSpot) {
        selectedSpot.value = spot
    }

    fun unselectSpot() {
        selectedSpot.value = null
    }

    fun rotateSpot() {
        selectedSpot.value?.let {
            it.size = it.size.copy(it.size.height, it.size.width)
            parkingSpots[it.id] = it
            saveSpotChanges(it)
        }
    }

    fun deleteSpot(square: PlaceSpot? = selectedSpot.value) {
        square ?: return
        parkingSpotsRepository.deleteSpot(tempFloor, square)
        parkingSpots.remove(square.id)
    }

    fun updateOffset(offset: Offset) {
        this.offset.value = offset
        return // TODO review limits
        val halfScreenX = size.value.width / 2
        val halfScreenY = size.value.height / 2
        val maxX = (parkingSpots.minX * -1) + halfScreenX
        val minX = (parkingSpots.maxX * -1) + halfScreenX
        val maxY = (parkingSpots.minY * -1) + halfScreenY
        val minY = (parkingSpots.maxY * -1) + halfScreenY
        val zoom = this.zoom.floatValue

        this.offset.value = Offset(
            offset.x.coerceIn(minX * zoom, maxX * zoom),
            offset.y.coerceIn(minY * zoom, maxY * zoom)
        )
    }

    private fun saveSpotChanges(spot: PlaceSpot) = parkingSpotsRepository.updateSpot(tempFloor, spot)

    private fun animateToSpot(spot: PlaceSpot, zoom: Float = 1f) = animateToTarget(
        targetOffset = Offset(spot.position.x, spot.position.y),
        targetZoom = zoom,
        targetSize = IntSize(spot.size.width.toInt(), spot.size.height.toInt())
    )

    private fun animateToTarget(
        targetOffset: Offset = offset.value,
        targetZoom: Float = zoom.floatValue,
        targetSize: IntSize = IntSize(0, 0),
        duration: Long = 250,
        steps: Int = (duration / 1000f * 120f).toInt()
    ) {
        viewModelScope.launch {
            val delayTime = duration / steps
            val initialOffset = offset.value
            val initialZoom = zoom.floatValue
            var realTargetOffset = targetOffset.adaptToBox()
            // center the target on screen
            realTargetOffset += Offset((size.value.width / 2).toFloat(), (size.value.height / 2).toFloat())
            realTargetOffset -= Offset((targetSize.width / 2).toFloat(), (targetSize.height / 2).toFloat())
            realTargetOffset *= targetZoom

            val stepX = (realTargetOffset.x - initialOffset.x) / steps
            val stepY = (realTargetOffset.y - initialOffset.y) / steps
            val stepZoom = (targetZoom - initialZoom) / steps

            for (i in 1..steps) {
                offset.value = Offset(
                    initialOffset.x + stepX * i,
                    initialOffset.y + stepY * i
                )
                zoom.floatValue = initialZoom + stepZoom * i
                delay(delayTime)
            }
            offset.value = realTargetOffset
            zoom.floatValue = targetZoom
        }
    }

    private fun Offset.adaptToBox() = Offset(
        ((parkingSpots.area.width.value - size.value.width) / 2f) - this.x,
        ((parkingSpots.area.height.value - size.value.height) / 2f) - this.y
    )
}

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

val shadowMargin = 100.dp
val Map<String, PlaceSpot>.area
    get() = DpSize(
        (maxX - minX).dp + (shadowMargin * 2),
        (maxY - minY).dp + (shadowMargin * 2)
    )
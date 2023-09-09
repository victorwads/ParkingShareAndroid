package br.com.victorwads.parkingshare.presentation.screens.parking

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
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
    val zoom = mutableFloatStateOf(1f)
    val offset = mutableStateOf(Offset(0f, 0f))
    val size = mutableStateOf(IntSize(0, 0))

    fun loadParkingSpots() {
        viewModelScope.launch {
            parkingSpotsRepository.getAllSpots(tempFloor).let {
                parkingSpots.clear()
                parkingSpots.putAll(it)
                animateToTarget(
                    targetOffset = parkingSpots.initBoxOffSet,
                    targetZoom = 0.1f,
                    centerOffset = Offset(size.value.width.toFloat(), size.value.height.toFloat())
                )
            }
        }
    }

    fun findSpot(id: String) {
        parkingSpots[id]?.let {
            selectedSpot.value = it
            animateToSpot(it, zoom = 1f)
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

    private fun saveSpotChanges(spot: PlaceSpot) {
        parkingSpotsRepository.updateSpot(tempFloor, spot)
    }

    private fun animateToSpot(spot: PlaceSpot, zoom: Float? = null) {
        animateToTarget(
            targetOffset = Offset(spot.position.x * -1, spot.position.y * -1),
            centerOffset = Offset(spot.size.width, spot.size.height),
            targetZoom = zoom ?: this.zoom.floatValue,
        )
    }

    private fun animateToTarget(
        targetOffset: Offset = offset.value,
        targetZoom: Float = zoom.floatValue,
        center: Boolean = true,
        centerOffset: Offset = Offset(0f, 0f),
        duration: Long = 250,
        steps: Int = (duration / 1000f * 120f).toInt()
    ) {
        viewModelScope.launch {
            val initialOffset = offset.value
            var realTargetOffset = (targetOffset * targetZoom)
            if (center) {
                realTargetOffset += Offset(size.value.width / 2f, size.value.height / 2f)
                realTargetOffset -= centerOffset / 2f
            }
            val initialZoom = zoom.floatValue

            val diffX = realTargetOffset.x - initialOffset.x
            val diffY = realTargetOffset.y - initialOffset.y
            val diffZoom = targetZoom - initialZoom

            val stepX = diffX / steps
            val stepY = diffY / steps
            val stepZoom = diffZoom / steps

            val delayTime = duration / steps

            for (i in 1..steps) {
                delay(delayTime)
                offset.value = Offset(
                    initialOffset.x + stepX * i,
                    initialOffset.y + stepY * i
                )
                zoom.floatValue = initialZoom + stepZoom * i
            }
            offset.value = realTargetOffset
            zoom.floatValue = targetZoom
        }
    }
}

val Map<String, PlaceSpot>.minX
    get() = this.minOfOrNull { it.value.position.x } ?: 0f
val Map<String, PlaceSpot>.minY
    get() = this.minOfOrNull { it.value.position.y } ?: 0f
val Map<String, PlaceSpot>.maxX
    get() = this.maxOfOrNull { it.value.position.x } ?: 0f
val Map<String, PlaceSpot>.maxY
    get() = this.maxOfOrNull { it.value.position.y } ?: 0f


val Map<String, PlaceSpot>.initBoxOffSet // Center
    get() = Offset(
        (maxX - minX) / 2f,
        (maxY - minY) / 2f
    )

fun Offset.limitOut(
    squaresMap: Map<String, PlaceSpot>,
    zoom: Float,
    marginHorizontal: Float,
    marginVertical: Float
): Offset {
//    val squares = squaresMap.map { it.value }
//    val minX = squares.minX
//    val maxX = squares.maxOfOrNull { it.position.x } ?: 0f
//    val minY = squares.minY
//    val maxY = squares.maxOfOrNull { it.position.y } ?: 0f

    return this
//    Offset(
//        this.x.coerceIn(
//            (maxX * (-1) + marginHorizontal) * zoom,
//            (minX * (-1) + marginHorizontal) * zoom
//        ),
//        this.y.coerceIn(
//            (maxY * (-1) + marginVertical) * zoom,
//            (minY * (-1) + marginVertical) * zoom
//        )
//    )
}
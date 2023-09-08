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

    init {
        loadParkingSpots()
    }

    fun addParkingSpot() {
        val newName = parkingSpots.size.toString()
        val newSpot = selectedSpot.value?.let {
            PlaceSpot(
                id = newName,
                floor = tempFloor,
                position = it.position + Offset(it.size.width, 0f)
            )
        } ?: PlaceSpot(newName)

        parkingSpots[newName] = newSpot
        selectedSpot.value = newSpot
        saveSpotChanges(newSpot)
    }

    fun selectSpot(spot: PlaceSpot) {
        selectedSpot.value = spot
    }

    fun saveSpotChanges(spot: PlaceSpot) {
        parkingSpotsRepository.updateSpot(tempFloor, spot)
    }

    fun loadParkingSpots() {
        viewModelScope.launch {
            parkingSpotsRepository.getAllSpots(tempFloor).let {
                parkingSpots.clear()
                parkingSpots.putAll(it)
                animateToTarget(targetOffset = parkingSpots.initBoxOffSet, targetZoom = 0.6f)
            }
        }
    }

    fun deleteSpot(square: PlaceSpot) {
        parkingSpotsRepository.deleteSpot(tempFloor, square)
        parkingSpots.remove(square.id)
    }

    fun animateToTarget(
        targetOffset: Offset = offset.value,
        targetZoom: Float = zoom.floatValue,
        duration: Long = 250,
        steps: Int = (duration / 1000f * 120f).toInt()
    ) {
        viewModelScope.launch {
            val initialOffset = offset.value
            val realTargetOffset = targetOffset * targetZoom
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
        }
    }
}

val Map<String, PlaceSpot>.minX
    get() = this.minOfOrNull { it.value.position.x } ?: 0f
val Map<String, PlaceSpot>.minY
    get() = this.minOfOrNull { it.value.position.y } ?: 0f
val Map<String, PlaceSpot>.initBoxOffSet
    get() = Offset(x = this.minX * -1, y = this.minY * -1)

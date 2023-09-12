package br.com.victorwads.parkingshare.presentation.parking.components

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import br.com.victorwads.parkingshare.animateToTarget
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.data.models.area
import br.com.victorwads.parkingshare.data.models.boxSpot
import br.com.victorwads.parkingshare.data.models.fixPosition
import br.com.victorwads.parkingshare.data.models.lastSpot
import kotlinx.coroutines.CoroutineScope

class ParkingGraphViewController(
    private val turnOffAnimations: Boolean = false
) {
    private lateinit var inputMode: SpotInputMode

    val parkingSpots = mutableStateMapOf<String, PlaceSpot>()
    val selectedSpot = mutableStateOf<PlaceSpot?>(null)
    val rotation = mutableFloatStateOf(0f)
    val zoom = mutableFloatStateOf(0f)
    val offset = mutableStateOf(Offset(0f, 0f))
    val size = mutableStateOf(IntSize(0, 0))

    val referenceSpot: PlaceSpot
        get() = selectedSpot.value ?: parkingSpots.lastSpot

    fun setSpots(spots: Map<String, PlaceSpot>) {
        parkingSpots.clear()
        parkingSpots.putAll(spots)
    }

    fun addSpot(spot: PlaceSpot) {
        spot.fixPosition(parkingSpots)
        parkingSpots[spot.id] = spot
        selectedSpot.value = spot
    }

    fun selectSpot(spot: PlaceSpot) {
        selectedSpot.value = spot
    }

    fun unselectSpot() {
        selectedSpot.value = null
    }

    fun animateToSpot(
        coroutineScope: CoroutineScope?,
        spot: PlaceSpot,
        zoom: Float = this.zoom.floatValue
    ) = animateToTarget(
        coroutineScope,
        targetOffset = Offset(spot.position.x, spot.position.y),
        targetZoom = zoom,
        targetSize = IntSize(spot.size.width.toInt(), spot.size.height.toInt())
    )

    fun animateToTarget(
        coroutineScope: CoroutineScope?,
        targetOffset: Offset = offset.value,
        targetZoom: Float = zoom.floatValue,
        targetSize: IntSize = IntSize(0, 0),
    ) {
        var realTargetOffset = targetOffset.adaptToBox()
        realTargetOffset += Offset((size.value.width / 2).toFloat(), (size.value.height / 2).toFloat())
        realTargetOffset -= Offset((targetSize.width / 2).toFloat(), (targetSize.height / 2).toFloat())
        realTargetOffset *= targetZoom
        if (turnOffAnimations || coroutineScope == null) {
            offset.value = realTargetOffset
            zoom.floatValue = targetZoom
            rotation.floatValue = 0f
            return
        }
        coroutineScope.animateToTarget(offset.value, realTargetOffset) { offset.value = it }
        coroutineScope.animateToTarget(zoom.floatValue, targetZoom) { zoom.floatValue = it }
        if (rotation.floatValue != 0f) {
            coroutineScope.animateToTarget(rotation.floatValue % 360, 0f) { rotation.floatValue = it }
        }
    }

    private fun Offset.adaptToBox() = Offset(
        ((parkingSpots.area.width.value - size.value.width) / 2f) - this.x,
        ((parkingSpots.area.height.value - size.value.height) / 2f) - this.y
    )

    fun animateToCenter(coroutineScope: CoroutineScope?) {
        animateToSpot(coroutineScope, selectedSpot.value ?: parkingSpots.boxSpot, zoom = zoom.floatValue)
    }

    fun updateSpot(spot: PlaceSpot) {
        parkingSpots[spot.id] = spot
    }

    fun removeSpot(spot: PlaceSpot? = selectedSpot.value): PlaceSpot? {
        spot ?: return null
        parkingSpots.remove(spot.id)
        selectedSpot.value = null
        selectedSpot.value = referenceSpot
        return spot
    }
}

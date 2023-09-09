package br.com.victorwads.parkingshare.presentation.screens.parking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.victorwads.parkingshare.data.ParkingSpotsRepository
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.data.models.area
import br.com.victorwads.parkingshare.data.models.boxSpot
import br.com.victorwads.parkingshare.data.models.fixPosition
import br.com.victorwads.parkingshare.data.models.lastId
import br.com.victorwads.parkingshare.data.models.maxXPoint
import br.com.victorwads.parkingshare.data.models.maxYPoint
import br.com.victorwads.parkingshare.data.models.minX
import br.com.victorwads.parkingshare.data.models.minY
import br.com.victorwads.parkingshare.di.PreviewViewModelsFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ParkingEditViewModel(
    private val parkingSpotsRepository: ParkingSpotsRepository,
    private val turnOffAnimations: Boolean = false
) : ViewModel() {

    private val tempFloor = "T"

    val errors = MutableLiveData<ParkingViewEditorErrors?>()
    val newItemsAlignment = mutableStateOf(PlaceSpot.Alignment.RIGHT)
    val newItemsJump = mutableIntStateOf(1)

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

    fun findSpot(term: String) {
        val searched = parkingSpots[term]
            ?: parkingSpots.values.find { it.id.lowercase().startsWith(term.lowercase()) }
            ?: parkingSpots.values.find { it.id.lowercase().endsWith(term.lowercase()) }
            ?: parkingSpots.values.find { it.id.lowercase().contains(term.lowercase()) }
        searched?.let {
            selectedSpot.value = it
            animateToSpot(it)
        } ?: addParkingSpot(name = term)
    }

    fun addParkingSpot(align: PlaceSpot.Alignment, jump: Int? = null, from: PlaceSpot? = null): PlaceSpot {
        from?.let { selectedSpot.value = it }
        jump?.let { newItemsJump.intValue = it }
        newItemsAlignment.value = align
        return addParkingSpot()
    }

    fun addParkingSpot(name: String? = null, align: PlaceSpot.Alignment? = null): PlaceSpot {
        align?.let { newItemsAlignment.value = it }
        val new = (selectedSpot.value ?: parkingSpots.lastId).let { selectedSpot ->
            selectedSpot.copy(
                id = name ?: selectedSpot.id.toIntOrNull()?.plus(newItemsJump.intValue)?.toString() ?: "0",
            ).alignWith(selectedSpot, newItemsAlignment.value)
        }
        parkingSpots[new.id]?.let {
            errors.value = ParkingViewEditorErrors.SpotAlreadyExists(it)
            return it
        }
        new.let {
            parkingSpots[it.id] = it.fixPosition(parkingSpots)
            selectedSpot.value = it
            animateToSpot(it)
            saveSpotChanges(it)
        }
        return new
    }

    fun saveSpotChanges(spot: PlaceSpot, position: PlaceSpot.Position): PlaceSpot.Position {
        spot.position = position
        spot.fixPosition(parkingSpots)
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
            it.size = it.size.copy(width = it.size.height, height = it.size.width)
            parkingSpots[it.id] = it
            it.fixPosition(parkingSpots)
            saveSpotChanges(it)
            animateToSpot(it, zoom = zoom.floatValue)
        }
    }

    fun deleteSpot(square: PlaceSpot? = selectedSpot.value) {
        square ?: return
        parkingSpotsRepository.deleteSpot(tempFloor, square)
        parkingSpots.remove(square.id)
        selectedSpot.value = parkingSpots.keys
            .map { it.toIntOrNull() ?: 0 }.sorted()
            .lastOrNull()?.let { parkingSpots[it.toString()] }
    }

    fun updateOffset(offset: Offset) {
        this.offset.value = offset
        return // TODO review limits
        val halfScreenX = size.value.width / 2
        val halfScreenY = size.value.height / 2
        val maxX = (parkingSpots.minX * -1) + halfScreenX
        val minX = (parkingSpots.maxXPoint * -1) + halfScreenX
        val maxY = (parkingSpots.minY * -1) + halfScreenY
        val minY = (parkingSpots.maxYPoint * -1) + halfScreenY
        val zoom = this.zoom.floatValue

        this.offset.value = Offset(
            offset.x.coerceIn(minX * zoom, maxX * zoom),
            offset.y.coerceIn(minY * zoom, maxY * zoom)
        )
    }

    private fun saveSpotChanges(spot: PlaceSpot) = Unit//parkingSpotsRepository.updateSpot(tempFloor, spot)

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
        var realTargetOffset = targetOffset.adaptToBox()
        realTargetOffset += Offset((size.value.width / 2).toFloat(), (size.value.height / 2).toFloat())
        realTargetOffset -= Offset((targetSize.width / 2).toFloat(), (targetSize.height / 2).toFloat())
        realTargetOffset *= targetZoom
        if (turnOffAnimations) {
            offset.value = realTargetOffset
            zoom.floatValue = targetZoom
            return
        }
        viewModelScope.launch {
            val initialOffset = offset.value
            val delayTime = duration / steps
            val initialZoom = zoom.floatValue

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

    fun center() {
        animateToSpot(selectedSpot.value ?: parkingSpots.boxSpot, zoom = zoom.floatValue)
    }
}

@Preview
@Composable
fun PreviewParkingViewModel() {
    val viewModel: ParkingEditViewModel = viewModel(factory = PreviewViewModelsFactory())
    ParkingView(viewModel = viewModel, longPress = false)
    LaunchedEffect(Unit) {
        with(viewModel) {
            addParkingSpot()
            addParkingSpot(PlaceSpot.Alignment.BOTTOM)
            addParkingSpot(PlaceSpot.Alignment.RIGHT, 2, from = parkingSpots["0"])
            addParkingSpot(PlaceSpot.Alignment.BOTTOM)
            unselectSpot()
            center()
        }
    }
}
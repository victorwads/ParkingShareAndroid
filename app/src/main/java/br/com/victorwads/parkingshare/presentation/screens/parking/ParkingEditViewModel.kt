package br.com.victorwads.parkingshare.presentation.screens.parking

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.victorwads.parkingshare.data.ParkingSpotsRepository
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import kotlinx.coroutines.launch

class ParkingEditViewModel : ViewModel() {

    private val parkingSpotsRepository = ParkingSpotsRepository()
    private val tempFloor = "T"

    val parkingSpots = mutableStateMapOf<String, PlaceSpot>()
    val selectedSpot = mutableStateOf<PlaceSpot?>(null)

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
            }
        }
    }

    fun deleteSpot(square: PlaceSpot) {
        parkingSpotsRepository.deleteSpot(tempFloor, square)
        parkingSpots.remove(square.id)
    }
}
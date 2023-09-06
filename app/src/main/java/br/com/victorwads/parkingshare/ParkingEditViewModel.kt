package br.com.victorwads.parkingshare

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParkingEditViewModel : ViewModel() {

    private val _parkingSpots = mutableStateListOf<ParkingSpace>()
    private val _selectedSpot = MutableStateFlow<ParkingSpace?>(null)

    val parkingSpots: List<ParkingSpace> = _parkingSpots
    val selectedSpot: StateFlow<ParkingSpace?> = _selectedSpot.asStateFlow()

    fun addParkingSpot() {
       _selectedSpot.value?.let {
           _parkingSpots.add(
               ParkingSpace(
               position = it.position + Offset(it.size.width, 0f)
           )
           )
       } ?: _parkingSpots.add(ParkingSpace())
        setSelectedSpot(_parkingSpots.last().id)
    }

    fun setSelectedSpot(id: String) {
        _selectedSpot.value = _parkingSpots.find { it.id == id }
    }

    fun unselectSpot() {
        _selectedSpot.value = null
    }

    fun saveSpotChanges(spot: ParkingSpace, offset: Offset) {
        spot.position = offset
        _parkingSpots[_parkingSpots.indexOfFirst { it.id == spot.id }] = spot
    }

    fun deleteSpot(spot: ParkingSpace) {
        _parkingSpots.removeIf { it.id == spot.id }
    }
}
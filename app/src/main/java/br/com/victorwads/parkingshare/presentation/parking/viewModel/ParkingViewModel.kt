package br.com.victorwads.parkingshare.presentation.parking.viewModel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.victorwads.parkingshare.data.ParkingSpotsRepository
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.presentation.parking.components.ParkingGraphViewController
import br.com.victorwads.parkingshare.presentation.parking.components.SpotInputMode
import kotlinx.coroutines.launch

class ParkingEditViewModel(
    private val parkingSpotsRepository: ParkingSpotsRepository,
    val graphController: ParkingGraphViewController = ParkingGraphViewController(),
    private val floor: String = "T",
) : ViewModel() {


    // Editor
    val newItemsAlignment = mutableStateOf(PlaceSpot.Alignment.RIGHT)
    val newItemsJump = mutableIntStateOf(1)

    // View
    private lateinit var inputMode: SpotInputMode

    val errors = MutableLiveData<ParkingViewErrors?>()

    fun getNextSpotName(referenceName: String = graphController.referenceSpot.id): String =
        referenceName.toIntOrNull()?.plus(newItemsJump.intValue)?.toString() ?: "0"

    fun init(editing: Boolean = false) {
        this.inputMode = if (editing) SpotInputMode.LongPress else SpotInputMode.None
        viewModelScope.launch {
            parkingSpotsRepository.getAllSpots(floor).let {
                graphController.setSpots(it)
                graphController.animateToCenter(this)
            }
        }
    }

    fun findSpot(term: String, add: Boolean = inputMode != SpotInputMode.None) {
        viewModelScope.launch {
            parkingSpotsRepository.findSpot(floor, term).let {
                if (it == null) {
                    if (add) addParkingSpot(term)
                    else errors.value = ParkingViewErrors.SpotNotFound(term)
                } else {
                    graphController.animateToSpot(viewModelScope, it)
                }
            }
        }
    }

    fun addParkingSpot(alignment: PlaceSpot.Alignment, jump: Int? = null, from: PlaceSpot? = null) {
        from?.let { graphController.selectedSpot.value = it }
        jump?.let { newItemsJump.intValue = it }
        return addParkingSpot(alignment = alignment)
    }

    fun addParkingSpot(name: String? = null, alignment: PlaceSpot.Alignment = newItemsAlignment.value) {
        val new = graphController.referenceSpot.let {
            it.copy(
                id = name ?: getNextSpotName(it.id),
            ).alignWith(it, alignment)
        }
        if (graphController.parkingSpots.containsKey(new.id)) {
            errors.value = ParkingViewErrors.SpotAlreadyExists(new)
            return
        }
        new.let {
            graphController.addSpot(it)
            graphController.animateToSpot(viewModelScope, it)
            saveSpotChanges(it)
        }
    }

    fun saveSpotChanges(spot: PlaceSpot, position: PlaceSpot.Position): PlaceSpot.Position {
        val hollBack = spot.position.copy()
        spot.position = position
        saveSpotChanges(spot) {
            spot.position = hollBack
        }
        return spot.position
    }

    fun rotateSelectedSpot() {
        graphController.selectedSpot.value?.let {
            it.size = it.size.copy(width = it.size.height, height = it.size.width)
            graphController.updateSpot(it)
            graphController.animateToSpot(viewModelScope, it)
            saveSpotChanges(it)
        }
    }

    fun deleteSpot() = viewModelScope.launch {
        graphController.removeSpot()?.let {
            parkingSpotsRepository.deleteSpot(floor, it)
        }
    }

    private fun saveSpotChanges(spot: PlaceSpot, onError: () -> Unit = {}) = viewModelScope.launch {
        if (inputMode == SpotInputMode.None) return@launch
        if (!parkingSpotsRepository.updateSpot(floor, spot)) {
            errors.value = ParkingViewErrors.RepositoryGenericError(spot)
            onError()
        }
    }

    fun center() = graphController.animateToCenter(viewModelScope)
}

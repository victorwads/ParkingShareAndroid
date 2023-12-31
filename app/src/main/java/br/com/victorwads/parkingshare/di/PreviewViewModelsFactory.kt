@file:Suppress("UNCHECKED_CAST")

package br.com.victorwads.parkingshare.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.victorwads.parkingshare.data.ParkingSpotsRepository
import br.com.victorwads.parkingshare.data.models.Place
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.presentation.parking.components.ParkingGraphViewController
import br.com.victorwads.parkingshare.presentation.parking.viewModel.ParkingEditViewModel

class PreviewViewModelsFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ParkingEditViewModel::class.java) -> {
                return ParkingEditViewModel(
                    object : ParkingSpotsRepository {
                        override val place: Place
                            get() = Place()

                        override suspend fun getAllSpots(floor: String, cache: Boolean): Map<String, PlaceSpot> = spots

                        private val spots = mutableMapOf<String, PlaceSpot>()

                        override suspend fun getAllSpots(floor: String): Map<String, PlaceSpot> = spots

                        override suspend fun updateSpot(floor: String, spot: PlaceSpot): Boolean {
                            spots[spot.id] = spot
                            return true
                        }

                        override suspend fun deleteSpot(floor: String, square: PlaceSpot) {
                            spots.remove(square.id)
                        }

                        override suspend fun findSpot(term: String): Pair<String, PlaceSpot>? = null

                        override suspend fun findSpot(term: String, floor: String): PlaceSpot? = null
                    },
                    graphController = ParkingGraphViewController(turnOffAnimations = true)
                ) as T
            }

            else -> throw IllegalArgumentException("Classe ViewModel desconhecida")
        }
    }

    companion object {
        fun ParkingEditViewModel.createMediumParkingStop(): ParkingEditViewModel {
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot(PlaceSpot.Alignment.BOTTOM, 12, from = graphController.parkingSpots["0"])
            addParkingSpot(PlaceSpot.Alignment.RIGHT)
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            addParkingSpot()
            graphController.unselectSpot()
            graphController.zoom.floatValue = 0.5f
            center()
            return this
        }
    }
}

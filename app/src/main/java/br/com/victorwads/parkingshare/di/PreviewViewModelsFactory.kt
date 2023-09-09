@file:Suppress("UNCHECKED_CAST")

package br.com.victorwads.parkingshare.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.victorwads.parkingshare.data.ParkingSpotsRepository
import br.com.victorwads.parkingshare.data.models.Place
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import br.com.victorwads.parkingshare.presentation.screens.parking.ParkingEditViewModel

class PreviewViewModelsFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ParkingEditViewModel::class.java) -> {
                return ParkingEditViewModel(
                    object : ParkingSpotsRepository {
                        override val place: Place
                            get() = Place()

                        private val spots = mutableMapOf<String, PlaceSpot>()

                        override suspend fun getAllSpots(floor: String): Map<String, PlaceSpot> = spots

                        override fun updateSpot(floor: String, spot: PlaceSpot) {
                            spots[spot.id] = spot
                        }

                        override fun deleteSpot(floor: String, square: PlaceSpot) {
                            spots.remove(square.id)
                        }

                    },
                    turnOffAnimations = true
                ) as T
            }

            else -> throw IllegalArgumentException("Classe ViewModel desconhecida")
        }
    }
}

package br.com.victorwads.parkingshare.presentation.parking.viewModel

import br.com.victorwads.parkingshare.data.models.PlaceSpot

sealed class ParkingViewErrors {
    data class SpotAlreadyExists(val spot: PlaceSpot) : ParkingViewErrors()
    data class RepositoryGenericError(val spot: PlaceSpot) : ParkingViewErrors()
    data class SpotNotFound(val id: String) : ParkingViewErrors()
}
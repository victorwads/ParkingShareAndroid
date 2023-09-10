package br.com.victorwads.parkingshare.presentation.parking.viewModel

import br.com.victorwads.parkingshare.data.models.PlaceSpot

sealed class ParkingViewEditorErrors {
    data class SpotAlreadyExists(val spot: PlaceSpot) : ParkingViewEditorErrors()
    data class RepositoryGenericError(val spot: PlaceSpot) : ParkingViewEditorErrors()
    data class SpotNotFound(val id: String) : ParkingViewEditorErrors()
}
package br.com.victorwads.parkingshare.presentation.parking.viewModel

import br.com.victorwads.parkingshare.data.models.PlaceSpot

sealed class ParkingViewEditorErrors {
    data class SpotAlreadyExists(val spot: PlaceSpot) : ParkingViewEditorErrors()
    object InvalidSpotId : ParkingViewEditorErrors()
    object InvalidSpotJump : ParkingViewEditorErrors()
    object InvalidSpotFloor : ParkingViewEditorErrors()
    object InvalidSpotName : ParkingViewEditorErrors()
    object InvalidSpotPrice : ParkingViewEditorErrors()
    object InvalidSpotStatus : ParkingViewEditorErrors()
}
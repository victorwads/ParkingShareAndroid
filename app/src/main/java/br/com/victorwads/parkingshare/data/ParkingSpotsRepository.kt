package br.com.victorwads.parkingshare.data

import br.com.victorwads.parkingshare.data.models.Place
import br.com.victorwads.parkingshare.data.models.PlaceSpot

interface ParkingSpotsRepository {

    val place: Place
    suspend fun getAllSpots(floor: String): Map<String, PlaceSpot>

    suspend fun updateSpot(floor: String, spot: PlaceSpot): Boolean

    suspend fun deleteSpot(floor: String, square: PlaceSpot)

}
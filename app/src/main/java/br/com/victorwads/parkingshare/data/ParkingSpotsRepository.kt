package br.com.victorwads.parkingshare.data

import br.com.victorwads.parkingshare.data.firebase.Collections
import br.com.victorwads.parkingshare.data.models.Place
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.suspendCoroutine

class ParkingSpotsRepository(
    private val place: Place = Place(
        id = "jbTpkKNwrV4G1k1xlTOY",
        name = "Temp Place",
        floors = listOf("T", "1", "2"),
    )
) {
    private val db = FirebaseFirestore.getInstance()

    fun updateSpot(floor: String, spot: PlaceSpot) {
        getFloorsRef(floor).document(spot.id).set(spot)
    }

    fun deleteSpot(floor: String, square: PlaceSpot) {
        getFloorsRef(floor).document(square.id).delete()
    }

    suspend fun getAllSpots(floor: String): Map<String, PlaceSpot> = suspendCoroutine { cont ->
        getFloorsRef(floor).get()
            .addOnSuccessListener {
                val spots = it.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(PlaceSpot::class.java)?.let { spot ->
                            Pair(doc.id, spot)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }.toMap()
                cont.resumeWith(Result.success(spots))
            }
            .addOnFailureListener {
                cont.resumeWith(Result.success(mapOf()))
            }
    }

    private fun getFloorsRef(floor: String) = db
        .collection(Collections.Places).document(place.id)
        .collection(Collections.Spots + floor)
}
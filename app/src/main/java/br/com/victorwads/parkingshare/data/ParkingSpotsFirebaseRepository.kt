package br.com.victorwads.parkingshare.data

import br.com.victorwads.parkingshare.data.firebase.Collections
import br.com.victorwads.parkingshare.data.models.Place
import br.com.victorwads.parkingshare.data.models.PlaceSpot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.suspendCoroutine

class ParkingSpotsFirebaseRepository(
    override val place: Place = Place(
        id = "jbTpkKNwrV4G1k1xlTOY",
        name = "Temp Place",
        floors = listOf("T", "1", "2"),
    )
) : ParkingSpotsRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val memoryCache = mutableMapOf<String, MutableMap<String, PlaceSpot>>()

    override suspend fun getAllSpots(floor: String): Map<String, PlaceSpot> = getAllSpots(floor, true)

    override suspend fun getAllSpots(floor: String, cache: Boolean): Map<String, PlaceSpot> = suspendCoroutine { cont ->
        val key = place.id + floor
        if (cache && memoryCache.containsKey(key)) {
            memoryCache[key]?.let {
                cont.resumeWith(Result.success(it))
                return@suspendCoroutine
            }
        }
        getFloorsRef(floor).get().addOnFailureListener { cont.resumeWith(Result.success(mapOf())) }
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
                memoryCache[key] = spots.toMutableMap()
                cont.resumeWith(Result.success(spots))
            }
    }

    override suspend fun updateSpot(floor: String, spot: PlaceSpot): Boolean = suspendCoroutine {
        memoryCache[place.id + floor]?.let { cache ->
            if (cache.containsKey(spot.id)) {
                cache[spot.id] = spot
            }
        }
        getFloorsRef(floor).document(spot.id).set(spot)
            .addOnSuccessListener { _ -> it.resumeWith(Result.success(true)) }
            .addOnFailureListener { _ -> it.resumeWith(Result.success(false)) }
    }

    override suspend fun deleteSpot(floor: String, square: PlaceSpot) {
        getFloorsRef(floor).document(square.id).delete()
    }

    override suspend fun findSpot(term: String) =
        findSpot(term, place.floors, true) ?: findSpot(term, place.floors, false)

    override suspend fun findSpot(term: String, floor: String): PlaceSpot? =
        findSpot(term, listOf(floor), true)?.second ?: findSpot(term, listOf(floor), false)?.second

    private fun getFloorsRef(floor: String) =
        db.collection(Collections.Places).document(place.id).collection(Collections.Spots + floor)

    private suspend fun findSpot(term: String, floors: List<String>, exactMach: Boolean): Pair<String, PlaceSpot>? {
        val finalTerm = term.trim().lowercase()
        floors.forEach { floor ->
            getAllSpots(floor, true).let { parkingSpots ->
                parkingSpots[finalTerm]?.let { return Pair(floor, it) }
                if (!exactMach) {
                    val search = parkingSpots.values.find { it.id.lowercase().startsWith(finalTerm) }
                        ?: parkingSpots.values.find { it.id.lowercase().endsWith(finalTerm) }
                        ?: parkingSpots.values.find { it.id.lowercase().contains(finalTerm) }
                    search?.let { return Pair(floor, it) }
                }
            }
        }
        return null
    }

}
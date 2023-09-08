package br.com.victorwads.parkingshare.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PlaceSpotRequest(
    @DocumentId val id: String? = null,
    @PropertyName("spotId") val spotId: String = "",
    @PropertyName("user") val userId: String = "",
    @PropertyName("neededTime") val neededTime: NeededTime = NeededTime(),
    @ServerTimestamp
    @PropertyName("publishTime") val publishTime: Timestamp = Timestamp(Date(0)),
){
    data class NeededTime(
        @PropertyName("start") val startTime: Timestamp = Timestamp(Date(0)),
        @PropertyName("end") val endTime: Timestamp = Timestamp(Date(0))
    )
}

package br.com.victorwads.parkingshare.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PlaceSpotRequest(
    @JvmField @DocumentId
    var id: String? = null,
    @JvmField @PropertyName("spotId")
    var spotId: String = "",
    @JvmField @PropertyName("user")
    var userId: String = "",
    @JvmField @PropertyName("neededTime")
    var neededTime: NeededTime = NeededTime(),
    @ServerTimestamp
    @JvmField @PropertyName("publishTime")
    var publishTime: Timestamp = Timestamp(Date(0)),
) {
    data class NeededTime(
        @JvmField @PropertyName("start")
        var startTime: Timestamp = Timestamp(Date(0)),
        @JvmField @PropertyName("end")
        var endTime: Timestamp = Timestamp(Date(0))
    )
}

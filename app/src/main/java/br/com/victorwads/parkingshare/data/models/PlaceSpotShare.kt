package br.com.victorwads.parkingshare.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PlaceSpotShare(
    @JvmField @DocumentId
    var id: String,
    @JvmField @PropertyName("spotId")
    var spotId: String = "",
    @JvmField @PropertyName("startDate")
    var startDate: Timestamp = Timestamp(Date(0)),
    @JvmField @PropertyName("endDate")
    var endDate: Timestamp = Timestamp(Date(0)),
    @ServerTimestamp
    @JvmField @PropertyName("publishTime")
    var publishTime: Timestamp = Timestamp(Date(0)),
)

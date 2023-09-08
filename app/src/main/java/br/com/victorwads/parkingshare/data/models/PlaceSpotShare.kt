package br.com.victorwads.parkingshare.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PlaceSpotShare(
    @DocumentId val id: String,
    @PropertyName("spotId") val spotId: String = "",
    @PropertyName("startDate") val startDate: Timestamp = Timestamp(Date(0)),
    @PropertyName("endDate") val endDate: Timestamp = Timestamp(Date(0)),
    @ServerTimestamp
    @PropertyName("publishTime") val publishTime: Timestamp = Timestamp(Date(0)),
)

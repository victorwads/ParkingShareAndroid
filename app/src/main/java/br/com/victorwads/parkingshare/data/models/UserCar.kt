package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserCar(
    @DocumentId val id: String? = null,
    @PropertyName("model") val model: String? = null,
    @PropertyName("licensePlate") val licensePlate: String = ""
)

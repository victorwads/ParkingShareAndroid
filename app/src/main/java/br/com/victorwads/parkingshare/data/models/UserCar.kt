package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserCar(
    @JvmField @DocumentId
    var id: String? = null,
    @JvmField @PropertyName("model")
    var model: String? = null,
    @JvmField @PropertyName("licensePlate")
    var licensePlate: String = ""
)

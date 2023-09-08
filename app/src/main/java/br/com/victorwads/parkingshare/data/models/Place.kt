package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Place(
    @JvmField @DocumentId
    var id: String = "",
    @JvmField @PropertyName("name")
    var name: String,
    @JvmField @PropertyName("address")
    var address: Address = Address(),
    @JvmField @PropertyName("floors")
    var floors: List<String> = arrayListOf(),
    @JvmField @PropertyName("owners")
    var owners: List<String> = listOf(),
) {
    data class Address(
        @JvmField @PropertyName("zip_code")
        var cep: String = "",
        @JvmField @PropertyName("number")
        var number: String = "",
        @JvmField @PropertyName("address")
        var address: String = "",
    )
}

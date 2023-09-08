package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Place(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String,
    @PropertyName("address") val address: Address = Address(),
    @PropertyName("floors") val floors: List<String> = arrayListOf(),
    @PropertyName("owners") val owners: List<String> = listOf(),
) {
    data class Address(
        @PropertyName("zip_code")
        val cep: String = "",
        @PropertyName("number")
        val number: String = "",
        @PropertyName("address")
        val address: String = "",
    )
}

package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserPublicProfile(
    @JvmField @DocumentId
    var id: String,
    @JvmField @PropertyName("name")
    var username: String,
    @JvmField @PropertyName("picture")
    var profilePicture: String
)

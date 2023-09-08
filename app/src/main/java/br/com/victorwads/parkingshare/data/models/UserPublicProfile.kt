package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserPublicProfile(
    @DocumentId val id: String,
    @PropertyName("name") val username: String,
    @PropertyName("profilePicture") val profilePicture: String
)

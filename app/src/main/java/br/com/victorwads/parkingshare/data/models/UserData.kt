package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserData(
    @DocumentId val id: String = "",
    @PropertyName("name") val username: String = "",
    @PropertyName("profilePicture") val profilePicture: String? = null,

    @PropertyName("acceptTerms") val acceptTerms: Boolean = false,
    @PropertyName("deviceInfo") val deviceInfo: DeviceInfo? = null,
    @PropertyName(FieldNotificationTokens) val notificationTokens: List<String> = listOf(),
) {
    data class DeviceInfo(
        @PropertyName("device") val device: String = "",
        @PropertyName("version") val version: String = "",
        @PropertyName("versionCode") val versionCode: String = "",
        @PropertyName("osVersion") val osVersion: String = "",
    )

    companion object {
        const val FieldNotificationTokens = "notificationTokens"
    }
}

package br.com.victorwads.parkingshare.data.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
data class UserData(
    @JvmField @DocumentId
    var id: String = "",
    @JvmField @PropertyName("profileName")
    var username: String = "",
    @JvmField @PropertyName("profileImage")
    var profilePicture: String? = null,
    @JvmField @PropertyName("acceptTerms")
    var acceptTerms: Boolean = false,
    @JvmField @PropertyName("info")
    var deviceInfo: DeviceInfo? = null,
    @JvmField @PropertyName(Fields.NotificationTokens)
    var notificationTokens: List<String> = listOf(),
) {
    data class DeviceInfo(
        @JvmField @PropertyName("deviceModel")
        var device: String = "",
        @JvmField @PropertyName("appVersion")
        var version: String = "",
        @JvmField @PropertyName("appVersionCode")
        var versionCode: String = "",
        @JvmField @PropertyName("deviceOs")
        var osVersion: String = "",
    )

    object Fields {
        const val NotificationTokens = "notificationTokens"
    }
}

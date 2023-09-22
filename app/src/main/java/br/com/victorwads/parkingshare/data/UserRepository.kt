package br.com.victorwads.parkingshare.data

import android.content.pm.PackageInfo
import android.os.Build
import br.com.victorwads.parkingshare.data.firebase.Collections
import br.com.victorwads.parkingshare.data.models.UserData
import br.com.victorwads.parkingshare.data.models.UserData.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val user: FirebaseUser?
        get() = auth.currentUser

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            it.result?.let { token -> setNotificationToken(token) }
        }
    }

    fun setNotificationToken(token: String?) {
        val uid = auth.currentUser?.uid ?: return
        db.collection(Collections.Users)
            .document(uid)
            .update(Fields.NotificationTokens, FieldValue.arrayUnion(token))
    }

    fun createUserIfNotExists(pInfo: PackageInfo) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = db.collection(Collections.Users).document(uid)
        val user = UserData(
            id = uid,
            username = auth.currentUser?.displayName ?: "No Name",
            profilePicture = auth.currentUser?.photoUrl?.toString(),
            deviceInfo = UserData.DeviceInfo(
                device = "${Build.MANUFACTURER} ${Build.MODEL}",
                version = pInfo.versionName,
                versionCode = pInfo.versionCode.toString(),
                osVersion = Build.VERSION.RELEASE
            ),
        )
        userRef.set(user)
    }

    companion object {
        val shared = UserRepository()
    }
}

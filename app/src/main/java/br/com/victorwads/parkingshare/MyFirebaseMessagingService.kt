package br.com.victorwads.parkingshare

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import br.com.victorwads.parkingshare.data.UserRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) = UserRepository.shared.setNotificationToken(token)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationTitle = remoteMessage.notification?.title ?: "Título padrão"
        val notificationBody = remoteMessage.notification?.body ?: "Mensagem padrão"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(
            NotificationChannel("teste", "Teste", NotificationManager.IMPORTANCE_DEFAULT)
        )

        val notificationBuilder = NotificationCompat.Builder(this, "meu_canal")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Substitua pelo drawable que você quiser usar
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

}

package br.com.victorwads.parkingshare

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore

class ParkingShareApplication : Application() {
    override fun onCreate() {
        if (isDebug) FirebaseFirestore.getInstance()
            .useEmulator("192.168.31.75", 8090)
        super.onCreate()
    }
}

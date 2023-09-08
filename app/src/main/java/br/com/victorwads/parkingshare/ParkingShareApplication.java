package br.com.victorwads.parkingshare;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;

public class ParkingShareApplication extends Application {

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) FirebaseFirestore.getInstance()
                .useEmulator("192.168.31.75", 8090);

        super.onCreate();
    }
}

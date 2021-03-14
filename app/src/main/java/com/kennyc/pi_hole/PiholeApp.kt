package com.kennyc.pi_hole

import android.app.Application
import android.os.StrictMode
import com.kennyc.pi_hole.di.components.DaggerAppComponent

class PiholeApp:Application() {

    val component by lazy {
        DaggerAppComponent
            .builder()
            .appContext(applicationContext)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
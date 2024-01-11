package com.ipv.Irigasi

import android.app.Application
import android.content.res.Resources
import android.util.Log
import com.ipv.Irigasi.di.ApplicationComponent
import com.ipv.Irigasi.di.ApplicationModule
import com.ipv.Irigasi.di.DaggerApplicationComponent
import io.realm.kotlin.mongodb.App

// Fungsi ekstensi untuk mendapatkan nama TAG
inline fun <reified T> T.TAG(): String = T::class.java.simpleName

class SmartFarmingApp : Application() {

    companion object {
        lateinit var instance: SmartFarmingApp
            private set
        lateinit var realmApp: App
            private set
        lateinit var mqttManager: MqttManager
            private set
        lateinit var apiManager: ApiManager
            private set
    }

    // Mendeklarasikan dan menginisialisasi komponen Dagger
    val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    // Metode yang dipanggil saat aplikasi dimulai
    override fun onCreate() {
        super.onCreate()
        instance = this
        realmApp = App.create(getString(R.string.realm_app_id))
        Log.v(TAG(), "Initialized the Realm App configuration for: ${realmApp.configuration.appId}")

        // Initialize MqttManager and ApiManager
        mqttManager = MqttManager(this)
        apiManager = ApiManager(this)
    }
}


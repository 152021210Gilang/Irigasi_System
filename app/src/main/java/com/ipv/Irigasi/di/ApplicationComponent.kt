package com.ipv.Irigasi.di

import com.ipv.Irigasi.SmartFarmingApp
import dagger.Component
import javax.inject.Singleton

// Komponen Dagger untuk aplikasi
@Singleton
@Component(
    modules = [
        ApplicationModule::class
    ]
)
interface ApplicationComponent {

    // Fungsi untuk menyediakan subkomponen ActivityComponent
    fun activityComponent(): ActivityComponent

    // Fungsi untuk melakukan injeksi dependency ke dalam SmartFarmingApp
    fun inject(app: SmartFarmingApp)
}

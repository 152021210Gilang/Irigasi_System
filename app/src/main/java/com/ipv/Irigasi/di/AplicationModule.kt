package com.ipv.Irigasi.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides

// Modul Dagger yang menyediakan dependency aplikasi
@Module
class ApplicationModule(val app: Application) {

    // Fungsi yang menyediakan instansi Application ke Dagger
    @Provides
    fun app() = app

    // Fungsi yang menyediakan instansi Context ke Dagger
    @Provides
    fun context(): Context = app

    // Fungsi yang menyediakan instansi Resources ke Dagger
    @Provides
    fun resources(): Resources = app.resources
}

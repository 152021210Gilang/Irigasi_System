package com.ipv.Irigasi.di

import com.ipv.Irigasi.LoginActivity
import com.ipv.Irigasi.MainActivity
import com.ipv.Irigasi.fragments.Home
import com.ipv.Irigasi.fragments.Item
import com.ipv.Irigasi.fragments.Alerts
import dagger.Subcomponent

// Subkomponen (Subcomponent) yang digunakan untuk menyediakan dependency ke aktivitas dan fragmen
@Subcomponent
interface ActivityComponent {

    // Activities
    fun inject(activity: MainActivity) // Menginject dependency ke dalam MainActivity
    fun inject(activity: LoginActivity) // Menginject dependency ke dalam LoginActivity

    // Fragments
    fun inject(fragment: Home) // Menginject dependency ke dalam fragment Home
    fun inject(fragment: Item) // Menginject dependency ke dalam fragment Item
    fun inject(fragment: Alerts) // Menginject dependency ke dalam fragment Alerts
}

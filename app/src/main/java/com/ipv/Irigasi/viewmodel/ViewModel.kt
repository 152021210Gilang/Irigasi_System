package com.ipv.Irigasi.viewmodel

import androidx.lifecycle.*
import com.ipv.Irigasi.adapter.NotificationsAdapter
import com.ipv.Irigasi.adapter.PlantationsAdapter
import javax.inject.Inject


// ViewModel yang merupakan bagian dari arsitektur Android's Jetpack
class MainViewModel @Inject constructor() : ViewModel() {
    // LiveData untuk mengetahui apakah terdapat notifikasi atau tidak
    val hasNotifications = MutableLiveData<Boolean>()

    // LiveData untuk menyimpan adapter yang digunakan dalam tampilan daftar perkebunan
    var plantationsAdapter = MutableLiveData<PlantationsAdapter>()

    // LiveData untuk menyimpan adapter yang digunakan dalam tampilan daftar notifikasi
    var notificationsAdapter = MutableLiveData<NotificationsAdapter>()

    // LiveData untuk mengetahui apakah notifikasi diaktifkan atau tidak
    var isNotificationsEnabled = MutableLiveData<Boolean>()

    // LiveData untuk mengetahui apakah tombol login ditampilkan atau tidak
    var isLoginButton = MutableLiveData<Boolean>()
}

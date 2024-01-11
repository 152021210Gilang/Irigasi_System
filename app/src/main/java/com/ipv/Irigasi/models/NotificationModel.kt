package com.ipv.Irigasi.models

// Import yang diperlukan untuk menggunakan Serializable
import java.io.Serializable

// Model data class Notification yang implement Serializable
data class Notification(
    var title: String = "",    // Properti untuk judul notifikasi
    var body: String = "",     // Properti untuk isi notifikasi
    var type: String = "",     // Properti untuk jenis notifikasi
    var time: Long = 0,        // Properti untuk waktu notifikasi (dalam bentuk timestamp)
) : Serializable







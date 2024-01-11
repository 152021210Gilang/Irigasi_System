package com.ipv.Irigasi.models

// Import yang diperlukan untuk menggunakan Serializable
import java.io.Serializable

// Data class Plantation yang implement Serializable
data class Plantation(
    var id: String = "",                // Properti untuk ID perkebunan
    var name: String = "",              // Properti untuk nama perkebunan
    var type: String = "",              // Properti untuk jenis tanaman
    var notificationsOn: Boolean = false, // Properti untuk menentukan apakah notifikasi aktif
    var automaticWaterOn: Boolean = false, // Properti untuk menentukan apakah sistem irigasi otomatis aktif
    var waterOn: Boolean = false,        // Properti untuk menentukan apakah pompa air aktif
    var readings: ArrayList<Reading> = arrayListOf() // Properti untuk menyimpan data pembacaan sensor
) : Serializable {
    constructor() : this("", "", "", true, readings = arrayListOf()) // Konstruktor tambahan untuk kenyamanan
}

// Data class Reading yang implement Serializable
data class Reading(
    var id: String = "",        // Properti untuk ID pembacaan
    var temperature: Float = 0f, // Properti untuk suhu
    var humidity: Float = 0f,    // Properti untuk kelembaban
    var moisture: Float = 0f,    // Properti untuk kelembaban tanah
    var light: Float = 0f,       // Properti untuk intensitas cahaya
    var time: Long = 0           // Properti untuk waktu pembacaan (dalam bentuk timestamp)
) : Serializable









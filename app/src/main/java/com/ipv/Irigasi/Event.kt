package com.ipv.Irigasi

/*
 * Kelas Event digunakan untuk mewakili sebuah event yang dapat diobservasi dalam arsitektur LiveData.
 * Ini membantu dalam mengelola kejadian dan menghindari pemrosesan ganda.
 */
class Event<out T>(private val content: T) {

  private var hasBeenHandled = false

  /*
   * Fungsi ini digunakan untuk mendapatkan konten kejadian hanya jika belum pernah di-handle sebelumnya.
   * Jika sudah di-handle, maka akan mengembalikan nilai null.
   */
  fun getContentIfNotHandled(): T? {
    return if (hasBeenHandled)
      null
    else {
      // Jika belum di-handle, tandai sebagai sudah di-handle dan kembalikan kontennya.
      hasBeenHandled = true
      content
    }
  }
}

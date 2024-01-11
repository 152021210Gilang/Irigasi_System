package com.ipv.Irigasi

import android.view.View
import com.google.android.material.snackbar.Snackbar

// Fungsi ekstensi untuk menampilkan Snackbar dengan pesan dan tindakan default
fun View.showSnackbar(msgId: Int, length: Int, actionMessageId: Int) {
    showSnackbar(msgId, length, actionMessageId) {}
}

// Fungsi ekstensi untuk menampilkan Snackbar dengan pesan dan tindakan khusus
fun View.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

// Fungsi ekstensi untuk menampilkan Snackbar dengan pesan, durasi, dan tindakan khusus
fun View.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    // Membuat objek Snackbar dengan pesan dan durasi yang diberikan
    val snackbar = Snackbar.make(this, msg, length)

    // Jika terdapat pesan tindakan, menambahkan tindakan pada Snackbar
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
}

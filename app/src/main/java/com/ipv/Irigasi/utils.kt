package com.ipv.Irigasi

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class Utils {

    companion object {
        // Fungsi untuk menetapkan mode layar penuh pada aktivitas
        @JvmStatic
        fun fullScreen(activity: Activity) {

            // Menetapkan properti untuk mengabaikan sistem windows
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)

            // Menyembunyikan status bar menggunakan WindowInsetsControllerCompat
            val windowInsetsCompat =
                WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
            windowInsetsCompat.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // Memeriksa versi Android untuk menyesuaikan perilaku
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Versi Android R atau di atasnya
                if (activity.window.insetsController != null) {
                    val insetsController = activity.window.insetsController
                    insetsController?.hide(
                        WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
                    )
                    insetsController?.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                // Versi Android di bawah R
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
        }
    }
}

package com.ipv.Irigasi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.Irigasi.R
import com.ipv.Irigasi.Utils.Companion.fullScreen

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    // Waktu tampilan splash screen
    private val SPLASH_TIME: Long = 4000

    // Metode yang dipanggil saat kelas dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menetapkan tata letak untuk tampilan splash screen
        setContentView(R.layout.activity_splash_screen)

        // Menetapkan mode layar penuh
        fullScreen(this)

        // Handler untuk menunda tindakan setelah waktu tampilan splash screen
        Handler(Looper.getMainLooper()).postDelayed({

            // Memeriksa apakah pengguna telah masuk atau belum
            if(Firebase.auth.currentUser == null){
                // Jika tidak masuk, arahkan ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }else{
                // Jika masuk, arahkan ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }

            // Menyelesaikan aktivitas splash screen
            finish()
        }
            ,SPLASH_TIME)
    }
}

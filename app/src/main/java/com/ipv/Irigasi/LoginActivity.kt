package com.ipv.Irigasi

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.Irigasi.databinding.ActivityLoginBinding
import com.ipv.Irigasi.viewmodel.MainViewModel
import com.ipv.Irigasi.viewmodel.ViewModelFactory
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    // Deklarasi variabel
    private lateinit var binding: ActivityLoginBinding
    val app get() = applicationContext as SmartFarmingApp
    val component by lazy { app.component.activityComponent() }
    private var isCreateUserMode = false
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var playStoreAppsFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, playStoreAppsFactory)[MainViewModel::class.java]
    }

    // Metode yang dipanggil saat aktivitas dibuat
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject dependency menggunakan Dagger
        component.inject(this)

        // Inflate layout menggunakan ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menyembunyikan status bar dan navigasi bar untuk tampilan penuh
        Utils.fullScreen(this)

        // Inisialisasi autentikasi Firebase
        auth = Firebase.auth

        // Menetapkan listener untuk tombol login dan toggle mode
        binding.buttonCreateLogin.setOnClickListener { onLoginButtonClicked() }
        binding.buttonExistingAccount.setOnClickListener { toggleCreateUserMode() }

        // Memantau perubahan pada input email dan password
        checkTextChanges()

        // Memantau perubahan pada status tombol login dari ViewModel
        viewModel.isLoginButton.observe(this) { isEnabled ->
            if (isEnabled) {
                enableLoginButton()
            } else {
                disableLoginButton()
            }
        }
    }

    // Metode untuk mengaktifkan tombol login
    private fun enableLoginButton() {
        binding.buttonCreateLogin.isEnabled = true
        binding.buttonCreateLogin.isClickable = true
        binding.buttonCreateLogin.alpha = 1f
    }

    // Metode untuk menonaktifkan tombol login
    private fun disableLoginButton() {
        binding.buttonCreateLogin.isEnabled = false
        binding.buttonCreateLogin.isClickable = false
        binding.buttonCreateLogin.alpha = 0.5f
    }

    // Metode untuk memantau perubahan pada input email dan password
    private fun checkTextChanges() {
        // Memantau perubahan pada input email
        binding.inputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkLoginButtonState()
            }
        })

        // Memantau perubahan pada input password
        binding.inputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkLoginButtonState()
            }
        })
    }

    // Metode untuk menentukan status tombol login berdasarkan input email dan password
    private fun checkLoginButtonState() {
        if ((binding.inputEmail.text?.isNotEmpty() == true &&
                    binding.inputPassword.text?.isNotEmpty() == true))
            viewModel.isLoginButton.postValue(true)
        else
            viewModel.isLoginButton.postValue(false)
    }

    // Metode yang dipanggil saat tombol login ditekan
    private fun onLoginButtonClicked() {
        val username = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        if (isCreateUserMode) {
            registerUser(username, password)
        } else {
            logIn(username, password)
        }
    }

    // Metode untuk mendaftarkan pengguna baru
    private fun registerUser(email: String, password: String) {
        // Menonaktifkan tombol selama proses pendaftaran
        binding.buttonCreateLogin.isEnabled = false

        // Mendaftarkan pengguna menggunakan Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Pendaftaran berhasil, beralih ke tampilan utama
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    Toast.makeText(this, "Autentikasi berhasil!",
                        Toast.LENGTH_SHORT).show()

                    startActivity(Intent(application, MainActivity::class.java))
                } else {
                    // Jika pendaftaran gagal, tampilkan pesan kesalahan
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Autentikasi gagal.",
                        Toast.LENGTH_SHORT).show()
                }
                // Mengaktifkan kembali tombol setelah proses selesai
                binding.buttonCreateLogin.isEnabled = true
            }
    }

    // Metode untuk proses login
    private fun logIn(email: String, password: String) {
        // Menonaktifkan tombol selama proses login
        binding.buttonCreateLogin.isEnabled = false

        // Melakukan login menggunakan Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login berhasil, beralih ke tampilan utama
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    Toast.makeText(this, "Login berhasil!",
                        Toast.LENGTH_SHORT).show()

                    startActivity(Intent(application, MainActivity::class.java))
                } else {
                    // Jika login gagal, tampilkan pesan kesalahan
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Autentikasi gagal.",
                        Toast.LENGTH_SHORT).show()
                }
                // Mengaktifkan kembali tombol setelah proses selesai
                binding.buttonCreateLogin.isEnabled = true
            }
    }

    // Metode untuk beralih antara mode membuat akun baru dan mode login
    private fun toggleCreateUserMode() {
        this.isCreateUserMode = !isCreateUserMode
        if (isCreateUserMode) {
            binding.buttonCreateLogin.text = getString(R.string.create_account)
            binding.buttonExistingAccount.text = getString(R.string.already_have_account)
        } else {
            binding.buttonCreateLogin.text = getString(R.string.log_in)
            binding.buttonExistingAccount.text = getString(R.string.does_not_have_account)
        }
    }

    // Metode untuk menampilkan pesan kesalahan
    private fun displayErrorMessage(errorMsg: String) {
        Log.e(TAG(), errorMsg)
        Toast.makeText(baseContext, errorMsg, Toast.LENGTH_LONG).show()
    }

    // Metode untuk memeriksa status layanan Google Play
    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG(), "Error")
            false
        } else {
            Log.i(TAG(), "Google play services updated")
            true
        }
    }
}

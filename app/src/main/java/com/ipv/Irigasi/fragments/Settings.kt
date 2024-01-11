// Import yang diperlukan untuk menggunakan kelas dan komponen
package com.ipv.Irigasi.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ipv.Irigasi.LoginActivity
import com.ipv.Irigasi.MainActivity
import com.ipv.Irigasi.R
import com.ipv.Irigasi.databinding.FragmentSettingsBinding
import kotlin.random.Random

// Kelas Settings yang merupakan turunan dari Fragment
class Settings : Fragment() {

    // Mendapatkan instance activity dan component dari MainActivity
    private val activity get() = context as MainActivity
    private val component get() = activity.component

    // Deklarasi variabel untuk view binding dan autentikasi Firebase
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    // Metode onCreate yang dipanggil saat fragment dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    // Metode onCreateView yang membuat tata letak tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Menggunakan view binding untuk menghubungkan tampilan
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Menetapkan gambar profil berdasarkan pilihan acak
        when (Random.nextInt(0, 10)) {
            0 -> binding.profileImage.setImageResource(R.drawable.tomatoes)
            1 -> binding.profileImage.setImageResource(R.drawable.lettuce_farm)
            2 -> binding.profileImage.setImageResource(R.drawable.cucumber)
            3 -> binding.profileImage.setImageResource(R.drawable.beetroot)
            4 -> binding.profileImage.setImageResource(R.drawable.couves)
            5 -> binding.profileImage.setImageResource(R.drawable.corn)
            6 -> binding.profileImage.setImageResource(R.drawable.beans)
            7 -> binding.profileImage.setImageResource(R.drawable.apples)
            8 -> binding.profileImage.setImageResource(R.drawable.lentils)
            9 -> binding.profileImage.setImageResource(R.drawable.citrus)
            else -> binding.profileImage.setImageResource(R.drawable.other)
        }

        // Menetapkan teks nama pengguna pada profil
        binding.profileUsername.text = auth.currentUser?.email ?: getString(R.string.profile)

        // Menginisialisasi tata letak untuk setiap bagian menu
        binding.account.categoryText.text = getString(R.string.account)
        binding.account.cardImage.setImageResource(R.drawable.person)

        binding.plantations.categoryText.text = getString(R.string.plantations)
        binding.plantations.cardImage.setImageResource(R.drawable.plantations)

        binding.notifications.categoryText.text = getString(R.string.notifications)
        binding.notifications.cardImage.setImageResource(R.drawable.notifications)

        binding.about.categoryText.text = getString(R.string.about)
        binding.about.cardImage.setImageResource(R.drawable.about)

        binding.logout.categoryText.text = getString(R.string.log_out)
        binding.logout.cardImage.setImageResource(R.drawable.logout)

        // Mengembalikan tampilan yang telah dihubungkan
        return binding.root
    }

    // Metode onViewCreated yang dipanggil setelah tampilan dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menetapkan judul untuk ActionBar
        activity.supportActionBar?.title = getString(R.string.settings)
        // Menyembunyikan tombol back di ActionBar
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        // Menampilkan BottomBar
        showBottomBar()

        // Menangani klik pada item daftar plantasi untuk beralih ke tampilan home
        binding.plantations.listItem.setOnClickListener {
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.selectedItemId = R.id.MenuHome
        }

        // Menangani klik pada item daftar notifikasi untuk beralih ke tampilan notifikasi
        binding.notifications.listItem.setOnClickListener {
            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.selectedItemId = R.id.MenuNotifications
        }

        // Menangani klik pada item daftar logout untuk keluar dari akun
        binding.logout.listItem.setOnClickListener {
            Firebase.auth.signOut()
            goToLoginActivity()
        }
    }

    // Metode untuk menampilkan BottomBar
    private fun showBottomBar() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        // Animasi untuk menampilkan BottomBar
        if (bottomNavigationView.visibility == View.GONE) {
            bottomNavigationView.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomNavigationView.visibility = View.VISIBLE
        }
        if (bottomAppBar.visibility == View.GONE) {
            bottomAppBar.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomAppBar.visibility = View.VISIBLE
        }
        if (fab.visibility == View.GONE) {
            fab.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            fab.visibility = View.VISIBLE
        }
    }

    // Metode untuk beralih ke LoginActivity saat logout
    private fun goToLoginActivity() {
        startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }
}

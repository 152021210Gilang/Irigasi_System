package com.ipv.Irigasi.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.Irigasi.MainActivity
import com.ipv.Irigasi.R
import com.ipv.Irigasi.databinding.FragmentNotificationsBinding
import com.ipv.Irigasi.viewmodel.MainViewModel
import com.ipv.Irigasi.viewmodel.ViewModelFactory
import com.ipv.Irigasi.MqttManager
import javax.inject.Inject


class Alerts : Fragment(), MenuProvider {

    @Inject
    lateinit var smartFarmingFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity, smartFarmingFactory)[MainViewModel::class.java]
    }

    protected val activity get() = context as MainActivity
    private val component get() = activity.component
    lateinit var binding: FragmentNotificationsBinding
    lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var mqttManager: MqttManager

    // Langkah 1: Metode onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dagger Injection
        component.inject(this)
        auth = Firebase.auth

        // Initialize MQTT Manager
        mqttManager = MqttManager(requireContext())
    }

    // Langkah 2: Metode onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout untuk fragmen
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Langkah 3: Metode onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menambahkan MenuProvider ke MenuHost
        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Konfigurasi RecyclerView dan ActionBar
        binding.notificationsList.layoutManager = LinearLayoutManager(activity)
        binding.notificationsList.setHasFixedSize(true)

        activity.supportActionBar?.title = getString(R.string.notifications)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Memantau perubahan pada adapter notifikasi
        viewModel.notificationsAdapter.observe(viewLifecycleOwner) { notificationsAdapter ->
            binding.notificationsList.adapter = notificationsAdapter

            // Menampilkan atau menyembunyikan pesan "Tidak ada notifikasi"
            if (notificationsAdapter.itemCount == 0) {
                binding.noNotifications.visibility = View.VISIBLE
                viewModel.hasNotifications.postValue(false)
            } else {
                binding.noNotifications.visibility = View.GONE
                viewModel.hasNotifications.postValue(true)
            }
        }
    }

    // Langkah 4: Implementasi MenuProvider
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Menghapus dan mengganti menu notifikasi
        menu.clear()
        menuInflater.inflate(R.menu.notifications_menu, menu)
    }

    // Langkah 5: Mengatur aksi item menu
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Menangani pemilihan item menu
        when (menuItem.itemId) {
            R.id.action_remove -> {
                removeNotifications()

                // Connect to MQTT broker when removing notifications
                mqttManager.connect()
            }
        }
        return true
    }

    // Langkah 6: Mengatur persiapan menu
    override fun onPrepareMenu(menu: Menu) {
        // Menyembunyikan atau menampilkan item menu berdasarkan keberadaan notifikasi
        viewModel.hasNotifications.observe(viewLifecycleOwner) { hasNotifications ->
            menu.findItem(R.id.action_remove).isVisible = hasNotifications
        }
    }

    // Langkah 7: Metode removeNotifications
    private fun removeNotifications() {
        // Membuat dialog konfirmasi untuk menghapus semua notifikasi
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Hapus Notifikasi")
        builder.setMessage("Apakah Anda ingin menghapus semua notifikasi?")
        builder.setPositiveButton(
            HtmlCompat.fromHtml(
                ("<font color='#ffffff'>OK</font>"),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        ) { _, _ ->
            // Mengupdate status pengguna untuk menghapus notifikasi
            db.collection("users")
                .document(getEmail())
                .update("removeNotifications", true)
                .addOnSuccessListener {
                    // Disconnect from MQTT broker after successfully updating Firebase
                    mqttManager.disconnect()
                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE", "Error removing document", e)
                    // Disconnect from MQTT broker in case of failure
                    mqttManager.disconnect()
                }
        }
        builder.setNegativeButton(
            HtmlCompat.fromHtml(
                ("<font color='#ffffff'>Batal</font>"),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        ) { _, _ -> }

        builder.show()
    }

    // Langkah 8: Metode getEmail
    private fun getEmail(): String {
        // Mendapatkan alamat email pengguna saat ini menggunakan Firebase Authentication
        var email = ""
        if (auth.currentUser != null) {
            email = auth.currentUser!!.email.toString()
        }
        return email
    }
}

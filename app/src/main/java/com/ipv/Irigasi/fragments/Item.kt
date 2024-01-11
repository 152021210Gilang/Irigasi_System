package com.ipv.Irigasi.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ipv.Irigasi.*
import com.ipv.Irigasi.databinding.FragmentItemBinding
import com.ipv.Irigasi.models.Plantation
import com.ipv.Irigasi.viewmodel.MainViewModel
import com.ipv.Irigasi.viewmodel.ViewModelFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.JsonObject
import org.json.JSONObject
import javax.inject.Inject

class Item(private var id: String) : Fragment(), MenuProvider {

    // Referensi ke Firestore
    val db = Firebase.firestore

    // Dependensi ViewModel
    @Inject
    lateinit var smartFarmingFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity, smartFarmingFactory)[MainViewModel::class.java]
    }

    // Objek ApiManager untuk mengakses data dari API
    private lateinit var apiManager: ApiManager

    // Objek perkebunan yang sedang ditampilkan
    private lateinit var plantation: Plantation

    // Referensi ke MainActivity
    private val activity get() = context as MainActivity
    private val component get() = activity.component

    // Autentikasi Firebase
    private lateinit var auth: FirebaseAuth

    // Binding untuk tata letak fragment item
    private lateinit var binding: FragmentItemBinding

    // Metode yang dipanggil saat fragment dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plantation = it.getSerializable("plantation") as Plantation
        }
        component.inject(this)
        auth = Firebase.auth

        // Inisialisasi ApiManager
        apiManager = ApiManager(requireContext())
    }

    private fun fetchDataFromApi() {
        val apiUrl = "https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json" // Ganti dengan URL API yang sesuai

        apiManager.fetchData(apiUrl) { data ->
            // Menangani data dari respons API
            try {
                // Proses data JSON dari API sesuai kebutuhan
                val jsonData = JSONObject(data)
                // Contoh: Mengambil nilai dari kunci "exampleKey"
                val exampleValue = jsonData.getString("exampleKey")

                // Gunakan nilai dari API sesuai kebutuhan
                // Memperbarui UI atau lakukan tindakan lainnya dengan data dari API
            } catch (e: Exception) {
                Log.e("Item", "Error processing API response: ${e.message}")
            }
        }
    }

    // Metode yang dipanggil saat tampilan fragment dimulai
    override fun onStart() {
        super.onStart()
        // Mengatur judul ActionBar saat tampilan dimulai
        activity.supportActionBar?.title = plantation.name
        // Menampilkan data kesehatan perkebunan
        setPlantationHealthData(plantation)

        // Langkah 6: Memanggil metode fetchDataFromApi()
        fetchDataFromApi()
    }

    // Metode untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate tata letak fragment item
        binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Metode yang dipanggil setelah tampilan fragment dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menambahkan provider menu ke host
        val menuHost: MenuHost = requireHost() as MenuHost
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Mengatur tampilan item
        setupItemView()

        // Menambahkan fungsi untuk tombol switch pompa air
        binding.switchWaterPump.setOnClickListener {
            setWaterPump()
        }

        // Menambahkan fungsi untuk tombol switch sistem irigasi otomatis
        binding.switchSmartIrrigationSystem.setOnClickListener {
            setSmartIrrigationSystem()
        }

        // Mendengarkan perubahan data pada perkebunan
        listenForChanges()
    }

    // Mendengarkan perubahan pada perkebunan menggunakan snapshot listener Firestore
    private fun listenForChanges() {
        val docRef = db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Data changed: ${snapshot.data}")

                val json = Gson().toJson(snapshot.data)
                val plantationModel = Gson().fromJson(json, Plantation::class.java)

                plantation = plantationModel

                // Menampilkan data kesehatan terkini perkebunan
                setPlantationHealthData(plantation)

            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    // Mengatur data kesehatan perkebunan pada tampilan
    @SuppressLint("SetTextI18n")
    private fun setPlantationHealthData(data: Plantation) {

        // Handler untuk memastikan pembaruan dilakukan di thread utama
        val handler = Handler(Looper.getMainLooper())
        handler.post {

            // Menetapkan gambar berdasarkan jenis tanaman
            when (data.type) {
                "Tomatoes" -> binding.image.setImageResource(R.drawable.tomatoes)
                "Lettuces" -> binding.image.setImageResource(R.drawable.lettuce_farm)
                "Cucumber" -> binding.image.setImageResource(R.drawable.cucumber)
                "Beetroot" -> binding.image.setImageResource(R.drawable.beetroot)
                "Cabbages" -> binding.image.setImageResource(R.drawable.couves)
                "Corn" -> binding.image.setImageResource(R.drawable.corn)
                "Beans" -> binding.image.setImageResource(R.drawable.beans)
                "Apples" -> binding.image.setImageResource(R.drawable.apples)
                "Lentils" -> binding.image.setImageResource(R.drawable.lentils)
                "Citrus" -> binding.image.setImageResource(R.drawable.citrus)
                "Other" -> binding.image.setImageResource(R.drawable.other)
                else -> binding.image.setImageResource(R.drawable.other)
            }

            // Mengatur status switch dan teksnya berdasarkan kondisi perkebunan
            binding.switchWaterPump.isChecked = data.waterOn
            binding.switchSmartIrrigationSystem.isChecked = data.automaticWaterOn
            checkNotifications()

            if (data.waterOn)
                binding.switchWaterPumpDescription.text =
                    "Perkebunan sedang disiram air!"
            else
                binding.switchWaterPumpDescription.text =
                    "Sistem air manual dimatikan!"

            if (data.automaticWaterOn)
                binding.switchSmartIrrigationSystemDescription.text =
                    "Sistem pompa air otomatis dihidupkan!"
            else
                binding.switchSmartIrrigationSystemDescription.text =
                    "Sistem pompa air otomatis dimatikan!"

            // Menampilkan data sensor terakhir
            if (data.readings.isNotEmpty()) {
                binding.lastTimeUpdated.text = getDate(data.readings.last().time)
                binding.temperature.text = "%.0f".format(data.readings.last().temperature) + "ºC"
                binding.moisture.text = "%.0f".format(data.readings.last().moisture) + "%"
                binding.light.text = "%.0f".format(data.readings.last().light) + "%"
                binding.humidity.text = "%.0f".format(data.readings.last().humidity) + "%"

                // Menentukan status kesehatan berdasarkan kelembaban tanah
                when (data.readings.last().moisture) {
                    in 0.0f..19.9f -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#C62828"))
                        binding.statusHealthText.text = "Rendah"
                        binding.statusHealthDescriptionText.text =
                            "Perkebunan membutuhkan air!"
                    }
                    in 20.0f..39.9f -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#AFB42B"))
                        binding.statusHealthText.text = "Normal"
                        binding.statusHealthDescriptionText.text = "Perkebunan dalam keadaan sehat!"
                    }
                    in 40.0f..84.9f -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#7CB342"))
                        binding.statusHealthText.text = "Baik"
                        binding.statusHealthDescriptionText.text =
                            "Perkebunan dalam keadaan sangat sehat!"
                    }
                    else -> {
                        binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#C62828"))
                        binding.statusHealthText.text = "Rendah"
                        binding.statusHealthDescriptionText.text =
                            "Perkebunan kelembaban tanahnya terlalu tinggi!"
                    }
                }
            } else {
                // Menampilkan status kesehatan sebagai tidak diketahui jika data tidak ditemukan
                binding.statusHealthCard.setCardBackgroundColor(Color.parseColor("#F57C00"))
                binding.statusHealthText.text = "Tidak Diketahui"
                binding.statusHealthDescriptionText.text = "Data tidak ditemukan!"
                binding.light.text = "-"
                binding.humidity.text = "-"
                binding.moisture.text = "-"
                binding.temperature.text = "-"
            }
        }
    }

    // Fungsi untuk mendapatkan tanggal dari timestamp
    private fun getDate(timestamp: Long): String {
        var dateTime = ""
        try {
            val df: DateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss")
            val date = Date(timestamp * 1000)
            dateTime = df.format(date)
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.d("Exception", it) }
        }
        return dateTime
    }

    // Fungsi untuk mengatur tampilan item
    private fun setupItemView(){
        val drawable = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_arrow_back
        )

        activity.supportActionBar?.setHomeAsUpIndicator(drawable)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        hideBottomBar()
    }

    // Fungsi untuk menyembunyikan bottom bar
    private fun hideBottomBar(){

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        if(bottomNavigationView.visibility == View.VISIBLE){
            bottomNavigationView.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            bottomNavigationView.visibility = View.GONE
        }
        if(bottomAppBar.visibility == View.VISIBLE){
            bottomAppBar.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            bottomAppBar.visibility = View.GONE
        }
        if(fab.visibility == View.VISIBLE){
            fab.animate()
                .translationY(bottomAppBar.height.toFloat())
                .alpha(0.0f)
                .duration = 300
            fab.visibility = View.GONE
        }
    }

    // Fungsi untuk mengecek notifikasi
    private fun checkNotifications(){
        if (plantation.notificationsOn){
            viewModel.isNotificationsEnabled.postValue(true)
        } else {
            viewModel.isNotificationsEnabled.postValue(false)
        }
    }

    // Override fungsi onCreateMenu untuk membuat menu
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.item_menu, menu)
    }

    // Override fungsi onMenuItemSelected untuk menangani item menu yang dipilih
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        when(menuItem.itemId){
            android.R.id.home -> activity.supportFragmentManager.popBackStack()
            R.id.notifications_off, R.id.notifications_on -> setNotificationsOnOff()
            R.id.action_remove ->  removePlantation()
        }

        return true
    }

    // Override fungsi onPrepareMenu untuk mempersiapkan menu
    override fun onPrepareMenu(menu: Menu) {

        viewModel.isNotificationsEnabled.observe(viewLifecycleOwner){ isEnabled ->
            menu.findItem(R.id.notifications_on).isVisible = isEnabled
            menu.findItem(R.id.notifications_off).isVisible = !isEnabled
        }
    }

    // Fungsi untuk mengatur status notifikasi on/off
    private fun setNotificationsOnOff(){
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
            .update("isNotificationsOn", !plantation.notificationsOn)
            .addOnSuccessListener {

                plantation.notificationsOn =  !plantation.notificationsOn
                viewModel.isNotificationsEnabled.postValue(plantation.notificationsOn)
                if (plantation.notificationsOn)
                    Toast.makeText(activity, "Notifications turned ON!", Toast.LENGTH_SHORT)
                        .show()
                else
                    Toast.makeText(activity, "Notifications turned OFF!", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error updating document", e)
            }
    }

    // Fungsi untuk mengatur Smart Irrigation System on/off
    private fun setSmartIrrigationSystem(){
        val waterSystemOn = plantation.automaticWaterOn
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
            .update("automaticWaterOn", !waterSystemOn)
            .addOnSuccessListener {
                if (!waterSystemOn)
                    Toast.makeText(activity, "Smart irrigation system turned ON!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity, "Smart irrigation system turned OFF!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error adding document", e)
            }
    }

    // Fungsi untuk mengatur Water Pump on/off
    private fun setWaterPump(){
        val waterOn = plantation.waterOn
        db
            .collection("users")
            .document(getEmail())
            .collection("plantations")
            .document(id)
            .update("waterOn", !waterOn)
            .addOnSuccessListener {
                if (!waterOn)
                    Toast.makeText(activity, "Water pump turned ON!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity, "Water pump turned OFF!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error adding document", e)
            }
    }

    // Fungsi untuk menghapus plantation
    private fun removePlantation(){
        val builder = AlertDialog.Builder(activity)

        builder.setTitle("Remove plantation")
        builder.setMessage("Do you wish to remove your plantation '${plantation.name}' ?")
        builder.setPositiveButton(HtmlCompat.fromHtml(("<font color='#ffffff'>OK</font>"), HtmlCompat.FROM_HTML_MODE_LEGACY))
        { _, _ ->
            db
                .collection("users")
                .document(getEmail())
                .collection("plantations")
                .document(id)
                .delete()
                .addOnSuccessListener {

                    Toast.makeText(activity, "Plantation '${plantation.name}' removed!", Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE", "Error removing document", e)
                }
        }
        builder.setNegativeButton(HtmlCompat.fromHtml(("<font color='#ffffff'>Cancel</font>"), HtmlCompat.FROM_HTML_MODE_LEGACY)) { _, _ ->

        }
        builder.show()
    }

    // Fungsi untuk mendapatkan alamat email
    private fun getEmail(): String {
        var email = ""
        if(auth.currentUser != null){
            email = auth.currentUser!!.email.toString()
        }
        return email
    }
}
package com.ipv.Irigasi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ipv.Irigasi.*
import com.ipv.Irigasi.databinding.FragmentHomeBinding
import com.ipv.Irigasi.viewmodel.MainViewModel
import com.ipv.Irigasi.viewmodel.ViewModelFactory

import javax.inject.Inject

// Kelas utama untuk tampilan beranda
open class Home : Fragment() {

    // Menandai untuk penyuntikan dependensi menggunakan Dagger
    @Inject
    lateinit var smartFarmingFactory: ViewModelFactory<MainViewModel>
    // ViewModel yang digunakan untuk mengelola data dan interaksi di tampilan beranda
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity, smartFarmingFactory)[MainViewModel::class.java]
    }

    // Fungsi untuk mendapatkan referensi ke MainActivity
    protected val activity get() = context as MainActivity
    // Fungsi untuk mendapatkan komponen Dagger yang digunakan dalam penyuntikan dependensi
    private val component get() = activity.component
    // Binding untuk tata letak fragment home
    private lateinit var binding: FragmentHomeBinding

    // Metode yang dipanggil saat fragment dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Melakukan penyuntikan dependensi Dagger ke dalam kelas
        component.inject(this)
    }

    // Metode untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate tata letak fragment home
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Metode yang dipanggil setelah tampilan fragment dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur tata letak daftar perkebunan
        binding.listIrigasi.layoutManager = LinearLayoutManager(activity)
        binding.listIrigasi.setHasFixedSize(true)

        // Mengatur judul tampilan ActionBar
        activity.supportActionBar?.title = getString(R.string.app_name)
        activity.supportActionBar?.subtitle = ""
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Menampilkan Bottom Bar dengan animasi
        tampilkanBottomBar()

        // Mengamati perubahan pada adapter perkebunan
        viewModel.plantationsAdapter.observe(viewLifecycleOwner) { plantationsAdapter ->
            // Mengatur adapter perkebunan ke dalam RecyclerView
            binding.listIrigasi.adapter = plantationsAdapter

            // Menampilkan pesan jika tidak ada perkebunan yang tersedia
            if (plantationsAdapter.itemCount == 0) {
                binding.noPlantations.visibility = View.VISIBLE
                // Menambahkan aksi untuk tombol tambah perkebunan
                binding.addPlant.setOnClickListener {
                    val bottomSheetDialogFragment = BottomSheetFragment()
                    bottomSheetDialogFragment.show(
                        activity.supportFragmentManager,
                        bottomSheetDialogFragment.tag
                    )
                }
            } else {
                binding.noPlantations.visibility = View.GONE
            }
        }
    }

    // Fungsi untuk menampilkan Bottom Bar dengan animasi
    private fun tampilkanBottomBar() {
        // Mendapatkan referensi ke elemen BottomNavigationView, BottomAppBar, dan FloatingActionButton
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        // Menampilkan BottomNavigationView jika sebelumnya tidak terlihat
        if (bottomNavigationView.visibility == View.GONE) {
            bottomNavigationView.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomNavigationView.visibility = View.VISIBLE
        }

        // Menampilkan BottomAppBar jika sebelumnya tidak terlihat
        if (bottomAppBar.visibility == View.GONE) {
            bottomAppBar.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomAppBar.visibility = View.VISIBLE
        }

        // Menampilkan FloatingActionButton jika sebelumnya tidak terlihat
        if (fab.visibility == View.GONE) {
            fab.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            fab.visibility = View.VISIBLE
        }
    }
}

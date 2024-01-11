package com.ipv.Irigasi.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ipv.Irigasi.MainActivity
import com.ipv.Irigasi.R
import com.ipv.Irigasi.databinding.FragmentBottomSheetBinding
import com.ipv.Irigasi.models.Plantation

class BottomSheetFragment : BottomSheetDialogFragment() {
    // Mendapatkan referensi ke MainActivity
    private val activity get() = context as MainActivity
    private val db = Firebase.firestore
    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var auth: FirebaseAuth

    // Langkah 1: Metode onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mengatur gaya untuk BottomSheetDialogFragment
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        auth = Firebase.auth
    }

    // Langkah 2: Metode onCreateView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate tata letak untuk fragmen bottom sheet
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Langkah 3: Metode onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mendapatkan array tanaman dari sumber daya strings.xml
        val plants = resources.getStringArray(R.array.plants)
        // Membuat ArrayAdapter untuk dropdown
        val arrayAdapter = ArrayAdapter(activity, R.layout.dropdown_item, plants)
        binding.irigasiType.setAdapter(arrayAdapter)

        // Menangani klik tombol untuk menambahkan perkebunan
        binding.addIrigasi.setOnClickListener {
            // Mendapatkan nilai dari input pengguna
            val name = binding.NamaIrigasi.text.toString()
            val type = binding.irigasiType.text.toString()
            val id = System.currentTimeMillis().toString()

            // Membuat objek perkebunan
            val plantation = Plantation(
                id = id,
                name = name,
                type = type,
                notificationsOn = true,
                automaticWaterOn = true,
                readings = arrayListOf()
            )

            // Menambahkan perkebunan ke Firebase Firestore
            db.collection("users")
                .document(getEmail())
                .collection("plantations")
                .document(id)
                .set(plantation)
                .addOnSuccessListener {
                    Log.d("FIREBASE", "DocumentSnapshot added with ID: $id")
                    // Menyembunyikan bottom sheet setelah menambahkan perkebunan
                    dialog?.hide()

                    // Menampilkan pesan sukses
                    Toast.makeText(
                        activity,
                        "Perkebunan '${name}' telah dibuat!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE", "Error adding document", e)
                }
        }
    }

    // Langkah 4: Metode getEmail
    private fun getEmail(): String {
        var email = ""
        if(auth.currentUser != null){
            email = auth.currentUser!!.email.toString()
        }
        return email
    }
}

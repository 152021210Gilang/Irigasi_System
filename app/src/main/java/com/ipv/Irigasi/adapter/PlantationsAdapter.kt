package com.ipv.Irigasi.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.*
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.ipv.Irigasi.R
import com.ipv.Irigasi.fragments.Item
import com.ipv.Irigasi.models.Plantation



class PlantationsAdapter(
    private var data: MutableList<Plantation>,
    private var ids: ArrayList<String>
) : RecyclerView.Adapter<PlantationsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var card: MaterialCardView = view.findViewById(R.id.card)
        var healthy: TextView = view.findViewById(R.id.health)
        var switchWaterPump: TextView  = view.findViewById(R.id.switchWaterPump)
        var plantationId: TextView  = view.findViewById(R.id.plantID)
    }

    // Membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ItemViewHolder(itemView)
    }

    // Mengisi data pada ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val plantation: Plantation = data[position]

        // Mengatur tampilan sesuai dengan data perkebunan
        holder.name.text = plantation.name
        holder.plantationId.text = plantation.id
        holder.healthy.text = setHealthy(plantation)

        val waterSystem: String = if (plantation.automaticWaterOn)
            "On"
        else
            "Off"
        holder.switchWaterPump.text = waterSystem


        // Menangani klik pada kartu perkebunan untuk membuka detail
        holder.card.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("plantation", plantation)

            val myFragment: Fragment = Item(ids[position])
            myFragment.arguments = bundle

            val activity = it.context as AppCompatActivity
            activity.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.frame_layout, myFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    // Mengatur status kesehatan tanaman berdasarkan data pembacaan terakhir
    private fun setHealthy(data: Plantation) : String{
        if(data.readings.isNotEmpty()) {
            when (data.readings.last().moisture) {
                in 0.0f..19.9f -> {
                    return "Sangat Rendah"
                }
                in 20.0f..39.0f -> {
                    return "Normal"
                }
                in 40.0f..59.9f -> {
                    return "Baik"
                }
                in 60.0f..79.0f -> {
                    return "Rendah"
                }
                else -> {
                    return "Sangat Rendah"
                }
            }
        } else {
            return "Tidak Diketahui"
        }
    }

    // Mendapatkan jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return data.size
    }
}

package com.ipv.Irigasi.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ipv.Irigasi.R
import com.ipv.Irigasi.models.Notification
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

// Adapter untuk mengelola data notifikasi pada RecyclerView
class NotificationsAdapter(
    private var data: MutableList<Notification>,  // Data notifikasi yang akan ditampilkan
) : RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {

    // ViewHolder untuk setiap item dalam RecyclerView
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.notificationImage)
        var title: TextView = view.findViewById(R.id.notificationTitle)
        var body: TextView = view.findViewById(R.id.notificationBody)
        var lastTimeUpdated: TextView = view.findViewById(R.id.lastTimeUpdated)
    }

    // Membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ItemViewHolder(itemView)
    }

    // Mengisi data pada ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val notification: Notification = data[position]

        // Mengatur gambar berdasarkan jenis notifikasi
        when (notification.type) {
            "Alert" -> holder.image.setImageResource(R.drawable.red_alert_icon)
            "Info" -> holder.image.setImageResource(R.drawable.info_icon)
            "Rain" -> holder.image.setImageResource(R.drawable.rain_icon)
            else -> holder.image.setImageResource(R.drawable.info_icon)
        }

        // Mengisi judul dan isi notifikasi
        holder.title.text = notification.title
        holder.body.text = notification.body

        // Mengatur waktu terakhir diperbarui
        holder.lastTimeUpdated.text = getDate(notification.time)
    }

    // Mendapatkan string tanggal dari timestamp
    private fun getDate(timestamp: Long): String {
        var dateTime = ""
        try {
            val df: DateFormat = SimpleDateFormat(" HH:mm  |  dd/MM/yyyy")
            val date = Date(timestamp * 1000)
            dateTime = df.format(date)
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.d("Exception", it) }
        }
        return dateTime
    }

    // Mendapatkan jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return data.size
    }
}

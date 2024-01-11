package com.ipv.Irigasi

import android.content.Context
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ApiManager(private val context: Context) {

    private val okHttpClient = OkHttpClient()

    fun fetchData(apiUrl: String, callback: (String) -> Unit) {
        val request = Request.Builder()
            .url(apiUrl)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiManager", "Failed to fetch data: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                callback.invoke(data ?: "")
            }
        })
    }
}
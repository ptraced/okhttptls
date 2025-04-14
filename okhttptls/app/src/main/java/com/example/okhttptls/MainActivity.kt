package com.example.okhttptls

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val TAG = "OkHttpTlsExample"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
		
        fetchTlsData()
    }

    private fun fetchTlsData() {
        try {
            val client = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1)) // Use HTTP/2 with fallback
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
				
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = Request.Builder()
                        .url("https://tls.peet.ws/api/all")
                        .build()
						
                    client.newCall(request).execute().use { response ->
                        Log.i(TAG, "Protocol used: ${response.protocol}")

                        if (!response.isSuccessful) {
                            Log.e(TAG, "Request failed with code: ${response.code}")
                            return@use
                        }

                        val responseBody = response.body?.string()

                        if (responseBody != null) {
                            Log.i(TAG, "Response received successfully")
							
                            Log.i(TAG, "Response preview: ${responseBody}...")
                        } else {
                            Log.e(TAG, "Response body is null")
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Network error: ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error: ${e.javaClass.simpleName} - ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Configuration error: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
        }
    }
}
package org.glotov.justipinfo.data

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class IpService {
    private val client = OkHttpClient()

    fun fetchIpInfo(): String {
        val request = Request.Builder().url("https://ipinfo.io/json").build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    "Error: Code ${response.code} - ${response.message}"
                } else {
                    response.body?.string() ?: "Error: Empty body"
                }
            }
        } catch (e: IOException) {
            "Error: ${e.message}"
        }
    }
}

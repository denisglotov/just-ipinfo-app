package org.glotov.justipinfo.data

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class IpService {
    private val client = OkHttpClient()

    fun fetchIpInfo(url: String): String =
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    "Error: Code ${response.code} - ${response.message}"
                } else {
                    response.body?.string() ?: "Error: Empty body"
                }
            }
        } catch (e: IllegalArgumentException) {
            "Error: Invalid URL - ${e.message}"
        } catch (e: IOException) {
            "Error: Network request failed - ${e.message}"
        }
}

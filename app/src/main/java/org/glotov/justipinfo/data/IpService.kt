package org.glotov.justipinfo.data

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

open class IpService(
    private val client: OkHttpClient = OkHttpClient(),
) {
    open fun fetchIpInfo(url: String): String {
        return try {
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
}

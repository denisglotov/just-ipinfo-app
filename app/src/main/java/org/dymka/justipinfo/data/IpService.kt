package org.dymka.justipinfo.data

import okhttp3.CacheControl
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class IpService {
    // A single shared client.
    //
    // This endpoint reports *the caller's* public IP, so a response that was
    // produced for one network must never be reused on another one (e.g. after
    // the user toggles a VPN). Two things are therefore disabled explicitly:
    //
    //  * `cache(null)` - no HTTP response cache in this process (OkHttp's
    //    default), kept explicit so it can never be turned on by accident.
    //  * `ConnectionPool(0, ...)` - no idle keep-alive connections are retained,
    //    so every call performs a fresh TCP + TLS handshake on the *current*
    //    route instead of reusing a socket that was opened before the VPN
    //    switch.
    //
    // Requests additionally carry `Cache-Control: no-cache, no-store` so no
    // intermediate cache (carrier/enterprise proxy, endpoint CDN) may answer
    // with a heuristic-fresh copy either.
    private val client =
        OkHttpClient
            .Builder()
            .cache(null)
            .connectionPool(ConnectionPool(0, 1, TimeUnit.SECONDS))
            .retryOnConnectionFailure(true)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    fun fetchIpInfo(url: String): String =
        try {
            val request =
                Request
                    .Builder()
                    .url(url)
                    .cacheControl(NO_STORE)
                    .header("Pragma", "no-cache")
                    .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    "Error: Code ${response.code} - ${response.message}"
                } else {
                    val body = response.body?.string()
                    if (body.isNullOrBlank()) "Error: Empty body" else body
                }
            }
        } catch (e: IllegalArgumentException) {
            "Error: Invalid URL - ${e.message}"
        } catch (e: IOException) {
            "Error: Network request failed - ${e.message}"
        }

    companion object {
        private const val CONNECT_TIMEOUT_SECONDS = 15L
        private const val READ_TIMEOUT_SECONDS = 15L
        private const val CALL_TIMEOUT_SECONDS = 30L

        private val NO_STORE: CacheControl =
            CacheControl
                .Builder()
                .noCache()
                .noStore()
                .maxAge(0, TimeUnit.SECONDS)
                .build()
    }
}

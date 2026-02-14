package org.glotov.justipinfo.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(
    private val ipService: IpService,
    private val logger: Logger,
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun getAndLogIpInfo(): String =
        withContext(Dispatchers.IO) {
            val result = ipService.fetchIpInfo(getBaseUrl())
            logger.appendLog(result)
            logger.readLogs() // Return updated logs
        }

    suspend fun getLogs(): String =
        withContext(Dispatchers.IO) {
            logger.readLogs()
        }

    suspend fun clearLogs(): String =
        withContext(Dispatchers.IO) {
            logger.clearLogs()
            ""
        }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean(KEY_DARK_THEME, true)
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME, isDark) }
    }

    fun getBaseUrl(): String {
        return prefs.getString(KEY_BASE_URL, DEFAULT_URL)!!
    }

    fun setBaseUrl(url: String) {
        prefs.edit { putString(KEY_BASE_URL, url) }
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_BASE_URL = "base_url"
        private const val DEFAULT_URL = "https://ipinfo.io/json"
    }
}

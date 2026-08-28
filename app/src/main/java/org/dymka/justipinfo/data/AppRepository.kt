package org.dymka.justipinfo.data

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

    suspend fun getAndLogIpInfo(): List<String> =
        withContext(Dispatchers.IO) {
            val result = ipService.fetchIpInfo(getBaseUrl())
            logger.appendLog(result)
            logger.readLogEntries()
        }

    suspend fun getLogs(): List<String> =
        withContext(Dispatchers.IO) {
            logger.readLogEntries()
        }

    suspend fun clearLogs(): List<String> =
        withContext(Dispatchers.IO) {
            logger.clearLogs()
            emptyList()
        }

    suspend fun deleteLogEntry(index: Int): List<String> =
        withContext(Dispatchers.IO) {
            logger.deleteLogEntry(index)
            logger.readLogEntries()
        }

    fun isDarkTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, true)

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME, isDark) }
    }

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_URL)!!

    fun setBaseUrl(url: String) {
        prefs.edit { putString(KEY_BASE_URL, url) }
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_BASE_URL = "base_url"
        const val DEFAULT_URL = "https://ipinfo.io/json"
    }
}

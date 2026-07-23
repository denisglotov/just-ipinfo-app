package org.glotov.justipinfo.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AppRepository {
    suspend fun getAndLogIpInfo(): String

    suspend fun getLogs(): String

    suspend fun clearLogs(): String

    fun isDarkTheme(): Boolean

    fun setDarkTheme(isDark: Boolean)

    fun getBaseUrl(): String

    fun setBaseUrl(url: String)
}

class DefaultAppRepository(
    private val ipService: IpService,
    private val logger: Logger,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AppRepository {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun getAndLogIpInfo(): String =
        withContext(ioDispatcher) {
            val result = ipService.fetchIpInfo(getBaseUrl())
            logger.appendLog(result)
            logger.readLogs() // Return updated logs
        }

    override suspend fun getLogs(): String =
        withContext(ioDispatcher) {
            logger.readLogs()
        }

    override suspend fun clearLogs(): String =
        withContext(ioDispatcher) {
            logger.clearLogs()
            ""
        }

    override fun isDarkTheme(): Boolean {
        return prefs.getBoolean(KEY_DARK_THEME, true)
    }

    override fun setDarkTheme(isDark: Boolean) {
        prefs.edit { putBoolean(KEY_DARK_THEME, isDark) }
    }

    override fun getBaseUrl(): String {
        return prefs.getString(KEY_BASE_URL, DEFAULT_URL)!!
    }

    override fun setBaseUrl(url: String) {
        prefs.edit { putString(KEY_BASE_URL, url) }
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_BASE_URL = "base_url"
        private const val DEFAULT_URL = "https://ipinfo.io/json"
    }
}

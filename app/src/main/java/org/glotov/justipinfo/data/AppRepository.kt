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
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    suspend fun getAndLogIpInfo(): String =
        withContext(Dispatchers.IO) {
            val result = ipService.fetchIpInfo()
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
        return prefs.getBoolean("dark_theme", true)
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit { putBoolean("dark_theme", isDark) }
    }
}

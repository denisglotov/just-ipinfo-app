package org.glotov.justipinfo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val ipService: IpService, private val logger: Logger) {
    suspend fun getAndLogIpInfo(): String {
        return withContext(Dispatchers.IO) {
            val result = ipService.fetchIpInfo()
            logger.appendLog(result)
            logger.readLogs() // Return updated logs
        }
    }

    suspend fun getLogs(): String {
        return withContext(Dispatchers.IO) { logger.readLogs() }
    }

    suspend fun clearLogs(): String {
        return withContext(Dispatchers.IO) {
            logger.clearLogs()
            ""
        }
    }
}

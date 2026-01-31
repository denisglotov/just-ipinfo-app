package org.glotov.justipinfo.data

import android.content.Context
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger(
    private val context: Context,
) {
    private val logFileName = "app_requests.log"

    private fun getFile(): File = File(context.filesDir, logFileName)

    fun appendLog(message: String) {
        val timestamp =
            LocalDateTime.now().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            )
        val entry = "[$timestamp] $message\n-------------------\n"
        try {
            getFile().appendText(entry)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readLogs(): String {
        val file = getFile()
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun clearLogs() {
        val file = getFile()
        if (file.exists()) {
            file.writeText("")
        }
    }
}

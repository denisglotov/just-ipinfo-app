package org.dymka.justipinfo.data

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
        val entry = "[$timestamp] $message\n$ENTRY_SEPARATOR\n"
        try {
            getFile().appendText(entry)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readLogs(): String {
        val file = getFile()
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun readLogEntries(): List<String> = parseLogEntries(readLogs())

    private fun writeLogEntries(entries: List<String>) {
        val newContent =
            if (entries.isEmpty()) {
                ""
            } else {
                entries.joinToString(separator = "") { entry ->
                    "$entry\n$ENTRY_SEPARATOR\n"
                }
            }
        try {
            getFile().writeText(newContent)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteLogEntry(index: Int): List<String> {
        val updated = removeLogEntry(readLogEntries(), index)
        writeLogEntries(updated)
        return updated
    }

    fun clearLogs() {
        val file = getFile()
        if (file.exists()) {
            file.writeText("")
        }
    }

    companion object {
        const val ENTRY_SEPARATOR = "-------------------"

        fun parseLogEntries(content: String): List<String> {
            if (content.isBlank()) return emptyList()
            return content
                .split(ENTRY_SEPARATOR)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

        fun removeLogEntry(
            entries: List<String>,
            index: Int,
        ): List<String> {
            if (index !in entries.indices) return entries
            val mutable = entries.toMutableList()
            mutable.removeAt(index)
            return mutable
        }
    }
}

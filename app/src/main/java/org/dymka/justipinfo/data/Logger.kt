package org.dymka.justipinfo.data

import android.content.Context
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger(
    private val logFile: File,
) {
    constructor(context: Context) : this(File(context.filesDir, LOG_FILE_NAME))

    @Synchronized
    fun appendLog(message: String) {
        val timestamp =
            LocalDateTime.now().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            )
        val entry = "[$timestamp] $message"
        try {
            if (logFile.isLegacyLog()) {
                // Upgrade a separator-delimited log written by an older version of the app
                // before appending, so that records of the two formats never end up mixed.
                logFile.writeText(formatLogEntries(parseLogEntries(logFile.readText())))
            }
            logFile.appendText(formatLogEntries(listOf(entry)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Returns the persisted entries, or an empty list when the log cannot be read. A storage
     * failure must never escape into the caller's coroutine, where it would crash the app.
     */
    @Synchronized
    fun readLogEntries(): List<String> {
        val content =
            try {
                if (logFile.exists()) {
                    logFile.readText()
                } else {
                    ""
                }
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        return parseLogEntries(content)
    }

    /**
     * `true` when the file was written using the legacy separator-delimited format, which
     * cannot represent an entry that itself contains [LEGACY_ENTRY_SEPARATOR].
     */
    private fun File.isLegacyLog(): Boolean {
        if (!exists() || length() == 0L) return false
        return try {
            inputStream().bufferedReader().use { reader ->
                reader.readLine()?.startsWith(RECORD_HEADER_PREFIX) != true
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun writeLogEntries(entries: List<String>) {
        try {
            logFile.writeText(formatLogEntries(entries))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun deleteLogEntry(index: Int): List<String> {
        val updated = removeLogEntry(readLogEntries(), index)
        writeLogEntries(updated)
        return updated
    }

    @Synchronized
    fun clearLogs() {
        try {
            if (logFile.exists()) {
                logFile.writeText("")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val LOG_FILE_NAME = "app_requests.log"

        /**
         * Prefix of every record header, followed by the number of characters in the record
         * itself, for example `JII1 42`.
         */
        private const val RECORD_HEADER_PREFIX = "JII1 "

        /**
         * Record separator used by log files written before v1.4. Such files are still parsed
         * for compatibility, but are upgraded to the current format on the next append.
         */
        const val LEGACY_ENTRY_SEPARATOR = "-------------------"

        /**
         * Serializes [entries] into length-prefixed records:
         *
         * ```
         * JII1 42
         * [2026-10-01T20:47:42.123] {"ip": "1.2.3.4"}
         * ```
         *
         * Because every record states its own length, an entry may contain newlines or any
         * separator-like text without ever being mistaken for a record boundary.
         */
        fun formatLogEntries(entries: List<String>): String =
            entries.joinToString(separator = "") { entry ->
                "$RECORD_HEADER_PREFIX${entry.length}\n$entry\n"
            }

        /**
         * Parses a log file written by [formatLogEntries], falling back to the legacy
         * separator-delimited format for files created by older versions of the app.
         */
        fun parseLogEntries(content: String): List<String> {
            if (content.isBlank()) return emptyList()
            return if (content.startsWith(RECORD_HEADER_PREFIX)) {
                parseRecordEntries(content)
            } else {
                parseLegacyEntries(content)
            }
        }

        private fun parseRecordEntries(content: String): List<String> {
            val entries = mutableListOf<String>()
            var cursor = 0
            while (cursor < content.length) {
                val headerEnd = content.indexOf('\n', cursor)
                if (headerEnd == -1) break
                val recordLength =
                    content
                        .substring(cursor, headerEnd)
                        .removePrefix(RECORD_HEADER_PREFIX)
                        .toIntOrNull() ?: break
                val recordStart = headerEnd + 1
                val recordEnd = recordStart + recordLength
                // Truncated or corrupted record: keep what was read so far and stop.
                if (recordLength < 0 || recordEnd > content.length) break
                entries += content.substring(recordStart, recordEnd)
                cursor = recordEnd
                if (cursor < content.length && content[cursor] == '\n') cursor++
            }
            return entries
        }

        private fun parseLegacyEntries(content: String): List<String> =
            content
                .split(LEGACY_ENTRY_SEPARATOR)
                .map { it.trim() }
                .filter { it.isNotEmpty() }

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

package org.dymka.justipinfo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LoggerTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val logFile: File
        get() = File(temporaryFolder.root, "app_requests.log")

    private fun newLogger(): Logger = Logger(logFile)

    // --- blank input ---

    @Test
    fun parseLogEntries_emptyString_returnsEmptyList() {
        assertTrue(Logger.parseLogEntries("").isEmpty())
    }

    @Test
    fun parseLogEntries_blankString_returnsEmptyList() {
        assertTrue(Logger.parseLogEntries("   \n\n   ").isEmpty())
    }

    // --- round trip through the current, length-prefixed format ---

    @Test
    fun formatLogEntries_thenParseLogEntries_roundTripsEveryEntry() {
        val entries =
            listOf(
                "[2026-10-01T20:47:42.123] 1.2.3.4",
                "[2026-10-01T20:48:00.456] {\n  \"ip\": \"8.8.8.8\",\n  \"city\": \"Mountain View\"\n}",
                "",
                "[2026-10-01T20:49:00.789] Türkçe: özel karakterler 📡",
            )

        assertEquals(entries, Logger.parseLogEntries(Logger.formatLogEntries(entries)))
    }

    @Test
    fun parseLogEntries_entryContainingLegacySeparator_staysASingleEntry() {
        val entry = "[2026-10-01T20:47:42.123] start\n${Logger.LEGACY_ENTRY_SEPARATOR}\nend"

        val entries = Logger.parseLogEntries(Logger.formatLogEntries(listOf(entry)))

        assertEquals(1, entries.size)
        assertEquals(entry, entries[0])
    }

    @Test
    fun parseLogEntries_entryContainingRecordHeader_staysASingleEntry() {
        val entry = "[2026-10-01T20:47:42.123] JII1 12\nnot a header"

        assertEquals(listOf(entry), Logger.parseLogEntries(Logger.formatLogEntries(listOf(entry))))
    }

    // --- malformed current-format content ---

    @Test
    fun parseLogEntries_truncatedRecord_keepsCompleteEntriesOnly() {
        val content = Logger.formatLogEntries(listOf("[ts] 1.1.1.1")) + "JII1 99\n[ts] truncated"

        assertEquals(listOf("[ts] 1.1.1.1"), Logger.parseLogEntries(content))
    }

    @Test
    fun parseLogEntries_recordWithoutTrailingNewline_isParsed() {
        assertEquals(listOf("abc"), Logger.parseLogEntries("JII1 3\nabc"))
    }

    @Test
    fun parseLogEntries_recordHeaderWithoutLength_isIgnored() {
        assertTrue(Logger.parseLogEntries("JII1 abc\nwhatever").isEmpty())
    }

    // --- legacy format compatibility ---

    @Test
    fun parseLogEntries_legacySingleEntry_returnsSingleItem() {
        val content = "[2026-08-28T19:42:49.123] 1.2.3.4\n${Logger.LEGACY_ENTRY_SEPARATOR}\n"

        val entries = Logger.parseLogEntries(content)

        assertEquals(1, entries.size)
        assertEquals("[2026-08-28T19:42:49.123] 1.2.3.4", entries[0])
    }

    @Test
    fun parseLogEntries_legacyMultipleEntries_returnsAllItems() {
        val content =
            """
            [2026-08-28T19:42:49.123] {
              "ip": "8.8.8.8",
              "city": "Mountain View"
            }
            ${Logger.LEGACY_ENTRY_SEPARATOR}
            [2026-08-28T19:43:00.456] 1.1.1.1
            ${Logger.LEGACY_ENTRY_SEPARATOR}
            [2026-08-28T19:43:10.789] Error: Network request failed
            ${Logger.LEGACY_ENTRY_SEPARATOR}
            """.trimIndent()

        val entries = Logger.parseLogEntries(content)

        assertEquals(3, entries.size)
        assertEquals(
            """
            [2026-08-28T19:42:49.123] {
              "ip": "8.8.8.8",
              "city": "Mountain View"
            }
            """.trimIndent(),
            entries[0],
        )
        assertEquals("[2026-08-28T19:43:00.456] 1.1.1.1", entries[1])
        assertEquals("[2026-08-28T19:43:10.789] Error: Network request failed", entries[2])
    }

    // --- persistence ---

    @Test
    fun appendLog_thenReadLogEntries_roundTripsThroughTheFile() {
        val logger = newLogger()
        val payload = "start\n${Logger.LEGACY_ENTRY_SEPARATOR}\nend 📡"

        logger.appendLog(payload)

        val entries = logger.readLogEntries()
        assertEquals(1, entries.size)
        assertTrue(entries[0].endsWith(payload))
    }

    @Test
    fun appendLog_multipleTimes_keepsEveryEntryInOrder() {
        val logger = newLogger()

        logger.appendLog("first")
        logger.appendLog("second")
        logger.appendLog("third")

        val entries = logger.readLogEntries()
        assertEquals(3, entries.size)
        assertTrue(entries[0].endsWith("first"))
        assertTrue(entries[1].endsWith("second"))
        assertTrue(entries[2].endsWith("third"))
    }

    @Test
    fun readLogEntries_missingFile_returnsEmptyList() {
        assertTrue(newLogger().readLogEntries().isEmpty())
    }

    @Test
    fun deleteLogEntry_removesTheRequestedEntry() {
        val logger = newLogger()
        logger.appendLog("first")
        logger.appendLog("second")
        logger.appendLog("third")

        val remaining = logger.deleteLogEntry(1)

        assertEquals(2, remaining.size)
        assertTrue(remaining[0].endsWith("first"))
        assertTrue(remaining[1].endsWith("third"))
        assertEquals(remaining, logger.readLogEntries())
    }

    @Test
    fun deleteLogEntry_entryContainingSeparator_doesNotRemoveTheWrongEntry() {
        val logger = newLogger()
        logger.appendLog("plain")
        logger.appendLog("start\n${Logger.LEGACY_ENTRY_SEPARATOR}\nend")

        val remaining = logger.deleteLogEntry(1)

        assertEquals(1, remaining.size)
        assertTrue(remaining[0].endsWith("plain"))
    }

    @Test
    fun clearLogs_emptiesTheLog() {
        val logger = newLogger()
        logger.appendLog("first")

        logger.clearLogs()

        assertTrue(logger.readLogEntries().isEmpty())
    }

    @Test
    fun appendLog_legacyFileOnDisk_isUpgradedWithoutLosingEntries() {
        val logger = newLogger()
        logFile.writeText(
            "[2026-08-28T19:42:49.123] 1.2.3.4\n${Logger.LEGACY_ENTRY_SEPARATOR}\n" +
                "[2026-08-28T19:43:00.456] 8.8.8.8\n${Logger.LEGACY_ENTRY_SEPARATOR}\n",
        )

        assertEquals(2, logger.readLogEntries().size)

        logger.appendLog("after upgrade")

        val entries = logger.readLogEntries()
        assertEquals(3, entries.size)
        assertEquals("[2026-08-28T19:42:49.123] 1.2.3.4", entries[0])
        assertEquals("[2026-08-28T19:43:00.456] 8.8.8.8", entries[1])
        assertTrue(entries[2].endsWith("after upgrade"))
        assertFalse(logFile.readText().contains(Logger.LEGACY_ENTRY_SEPARATOR))
    }

    // --- storage failures must never escape ---

    private fun unreadableLogFile(): File = File(temporaryFolder.root, "app_requests.log").apply { assertTrue(mkdirs()) }

    @Test
    fun readLogEntries_unreadableLog_returnsEmptyList() {
        assertTrue(Logger(unreadableLogFile()).readLogEntries().isEmpty())
    }

    @Test
    fun appendLog_unreadableLog_doesNotThrow() {
        Logger(unreadableLogFile()).appendLog("boom")
    }

    @Test
    fun clearLogs_unreadableLog_doesNotThrow() {
        Logger(unreadableLogFile()).clearLogs()
    }

    @Test
    fun deleteLogEntry_unreadableLog_doesNotThrow() {
        assertTrue(Logger(unreadableLogFile()).deleteLogEntry(0).isEmpty())
    }

    // --- removeLogEntry ---

    @Test
    fun removeLogEntry_validIndex_removesCorrectItem() {
        val list = listOf("entry0", "entry1", "entry2")

        assertEquals(listOf("entry0", "entry2"), Logger.removeLogEntry(list, 1))
    }

    @Test
    fun removeLogEntry_outOfBounds_returnsOriginalList() {
        val list = listOf("entry0", "entry1")

        assertEquals(list, Logger.removeLogEntry(list, -1))
        assertEquals(list, Logger.removeLogEntry(list, 5))
    }
}

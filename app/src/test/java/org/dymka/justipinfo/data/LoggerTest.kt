package org.dymka.justipinfo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoggerTest {
    @Test
    fun parseLogEntries_emptyString_returnsEmptyList() {
        val entries = Logger.parseLogEntries("")
        assertTrue(entries.isEmpty())
    }

    @Test
    fun parseLogEntries_blankString_returnsEmptyList() {
        val entries = Logger.parseLogEntries("   \n\n   ")
        assertTrue(entries.isEmpty())
    }

    @Test
    fun parseLogEntries_singleEntry_returnsSingleItem() {
        val logContent = "[2026-08-28T19:42:49.123] 1.2.3.4\n${Logger.ENTRY_SEPARATOR}\n"
        val entries = Logger.parseLogEntries(logContent)
        assertEquals(1, entries.size)
        assertEquals("[2026-08-28T19:42:49.123] 1.2.3.4", entries[0])
    }

    @Test
    fun parseLogEntries_multipleEntries_returnsAllItems() {
        val logContent =
            """
            [2026-08-28T19:42:49.123] {
              "ip": "8.8.8.8",
              "city": "Mountain View"
            }
            ${Logger.ENTRY_SEPARATOR}
            [2026-08-28T19:43:00.456] 1.1.1.1
            ${Logger.ENTRY_SEPARATOR}
            [2026-08-28T19:43:10.789] Error: Network request failed
            ${Logger.ENTRY_SEPARATOR}
            """.trimIndent()

        val entries = Logger.parseLogEntries(logContent)
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

    @Test
    fun removeLogEntry_validIndex_removesCorrectItem() {
        val list = listOf("entry0", "entry1", "entry2")
        val updated = Logger.removeLogEntry(list, 1)
        assertEquals(listOf("entry0", "entry2"), updated)
    }

    @Test
    fun removeLogEntry_outOfBounds_returnsOriginalList() {
        val list = listOf("entry0", "entry1")
        val updatedNegative = Logger.removeLogEntry(list, -1)
        val updatedTooLarge = Logger.removeLogEntry(list, 5)
        assertEquals(list, updatedNegative)
        assertEquals(list, updatedTooLarge)
    }
}

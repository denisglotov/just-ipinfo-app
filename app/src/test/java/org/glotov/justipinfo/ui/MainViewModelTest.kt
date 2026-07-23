package org.glotov.justipinfo.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.glotov.justipinfo.data.AppRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAppRepository(
    var logs: String = "",
    var isDark: Boolean = true,
    var url: String = "https://ipinfo.io/json",
) : AppRepository {
    override suspend fun getAndLogIpInfo(): String {
        logs += "Fetched info\n"
        return logs
    }

    override suspend fun getLogs(): String = logs

    override suspend fun clearLogs(): String {
        logs = ""
        return ""
    }

    override fun isDarkTheme(): Boolean = isDark

    override fun setDarkTheme(isDark: Boolean) {
        this.isDark = isDark
    }

    override fun getBaseUrl(): String = url

    override fun setBaseUrl(url: String) {
        this.url = url
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAppRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeAppRepository(logs = "Initial log", isDark = true, url = "https://ipinfo.io/json")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects repository preferences`() =
        runTest {
            val viewModel = MainViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("Initial log", viewModel.logs.value)
            assertTrue(viewModel.isDarkTheme.value)
            assertEquals("https://ipinfo.io/json", viewModel.baseUrl.value)
            assertFalse(viewModel.isLoading.value)
        }

    @Test
    fun `onRequestClicked fetches and updates logs`() =
        runTest {
            val viewModel = MainViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onRequestClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("Initial logFetched info\n", viewModel.logs.value)
            assertFalse(viewModel.isLoading.value)
        }

    @Test
    fun `onClearClicked clears logs`() =
        runTest {
            val viewModel = MainViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onClearClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("", viewModel.logs.value)
        }

    @Test
    fun `toggleDarkTheme updates theme state and repository`() =
        runTest {
            val viewModel = MainViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleDarkTheme(false)

            assertFalse(viewModel.isDarkTheme.value)
            assertFalse(repository.isDarkTheme())
        }

    @Test
    fun `updateBaseUrl updates url state and repository`() =
        runTest {
            val viewModel = MainViewModel(repository)
            testDispatcher.scheduler.advanceUntilIdle()

            val newUrl = "https://ifconfig.me"
            viewModel.updateBaseUrl(newUrl)

            assertEquals(newUrl, viewModel.baseUrl.value)
            assertEquals(newUrl, repository.getBaseUrl())
        }
}

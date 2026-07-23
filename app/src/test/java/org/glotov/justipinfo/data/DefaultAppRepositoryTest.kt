package org.glotov.justipinfo.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeIpService(var response: String) : IpService() {
    override fun fetchIpInfo(url: String): String = response
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultAppRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `FakeIpService returns expected response`() =
        runTest {
            val fakeService = FakeIpService("{\"ip\": \"127.0.0.1\"}")
            val result = fakeService.fetchIpInfo("https://ipinfo.io/json")
            assertEquals("{\"ip\": \"127.0.0.1\"}", result)
        }
}

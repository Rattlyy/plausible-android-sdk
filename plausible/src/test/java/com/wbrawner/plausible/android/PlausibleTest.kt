package com.wbrawner.plausible.android

import com.wbrawner.plausible.android.fake.FakePlausibleClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

internal const val SCREEN_WIDTH = 123

@RunWith(RobolectricTestRunner::class)
internal class PlausibleTest {
    private lateinit var client: FakePlausibleClient
    private lateinit var config: PlausibleConfig
    private lateinit var eventDir: File
    private lateinit var plausible: Plausible

    @Before
    fun setup() {
        eventDir = File.createTempFile("PlausibleTest", "", null)
        eventDir.delete()
        eventDir.mkdir()
        client = FakePlausibleClient()
        config = ThreadSafePlausibleConfig(eventDir, SCREEN_WIDTH)

        plausible = Plausible(client, config, "test.example.com")
    }

    @Test
    fun `enable is set on config via Plausible`() {
        assertTrue(config.enable)
        plausible.enable(false)
        assertFalse(config.enable)
    }

    @Test
    fun `user agent is set on config via Plausible`() {
        val oldUserAgent = config.userAgent
        plausible.setUserAgent("test user agent")
        assertNotEquals(oldUserAgent, config.userAgent)
        assertEquals("test user agent", config.userAgent)
    }

    @Test
    fun `events are sent to client`() {
        config.domain = "test.example.com"
        plausible.event("eventName", "eventUrl", "referrer", mapOf("prop1" to "propVal"))
        assertEquals(1, client.events.size)
        val event = client.events.first()
        assertEquals("test.example.com", event.domain)
        assertEquals("eventName", event.name)
        assertEquals("app://localhost/eventUrl", event.url)
        assertEquals(SCREEN_WIDTH, event.screenWidth)
        assertEquals("referrer", event.referrer)
        assertEquals(mapOf("prop1" to "propVal"), event.props)
    }
}
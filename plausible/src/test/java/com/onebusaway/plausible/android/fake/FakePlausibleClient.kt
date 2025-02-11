package com.onebusaway.plausible.android.fake

import com.onebusaway.plausible.android.Event
import com.onebusaway.plausible.android.PlausibleClient

internal class FakePlausibleClient : PlausibleClient {
    val events = mutableListOf<Event>()

    override fun event(event: Event) {
        events.add(event)
    }
}
package com.onebusaway.plausible.android

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Patterns
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

private val DEFAULT_USER_AGENT =
    "PlausibleAndroid ${BuildConfig.VERSION} Android ${Build.VERSION.RELEASE} ${Build.MANUFACTURER} ${Build.PRODUCT} ${Build.FINGERPRINT.hashCode()}"
private const val DEFAULT_PLAUSIBLE_HOST = "https://plausible.io/api"

/**
 * Configuration options for the Plausible SDK. See the [Events API reference](https://plausible.io/docs/events-api)
 * for more details on how these values are used by the backend.
 */
interface PlausibleConfig {

    /**
     * Domain name of the site in Plausible.
     */
    var domain: String

    /**
     * Whether or not events should be sent. Use this to allow users to opt-in or opt-out for
     * example.
     */
    var enable: Boolean

    /**
     * Directory to persist events upon upload failure.
     */
    val eventDir: File

    /**
     * The host for the Plausible backend server. Defaults to `https://plausible.io`.
     */
    var host: String

    /**
     * Whether or not to attempt to resend events upon failure. If true, events will be serialized
     * to disk in [eventDir] and the upload will be retried later.
     *
     * At the time of writing, the Plausible Events API doesn't support sending a timestamp for the
     * event, so attempting to resend the event at a later time may skew analytics data.
     */
    var retryOnFailure: Boolean

    /**
     * Width of the screen in dp.
     */
    val screenWidth: Int

    /**
     * The raw value of User-Agent is used to calculate the user_id which identifies a unique
     * visitor in Plausible.
     * User-Agent is also used to populate the Devices report in your
     * Plausible dashboard. The device data is derived from the open source database
     * device-detector. If your User-Agent is not showing up in your dashboard, it's probably
     * because it is not recognized as one in the device-detector database.
     */
    var userAgent: String
}

open class ThreadSafePlausibleConfig(
    override val eventDir: File,
    override val screenWidth: Int
) : PlausibleConfig {

    private val enableRef = AtomicBoolean(true)
    override var enable: Boolean
        get() = enableRef.get()
        set(value) = enableRef.set(value)

    private val domainRef = AtomicReference("")
    override var domain: String
        get() = domainRef.get()
        set(value) {
            require(Patterns.WEB_URL.matcher(value).matches() || value.isEmpty()) { "Invalid URL format" }
            domainRef.set(value)
        }

    private val hostRef = AtomicReference(DEFAULT_PLAUSIBLE_HOST)
    override var host: String
        get() = hostRef.get() ?: ""
        set(value) = hostRef.set(value.ifBlank { DEFAULT_PLAUSIBLE_HOST })

    private val retryRef = AtomicBoolean(true)
    override var retryOnFailure: Boolean
        get() = retryRef.get()
        set(value) = retryRef.set(value)

    private val userAgentRef = AtomicReference(DEFAULT_USER_AGENT)
    override var userAgent: String
        get() = userAgentRef.get()
        set(value) = userAgentRef.set(value.ifBlank { DEFAULT_USER_AGENT })
}

class AndroidResourcePlausibleConfig(context: Context) : ThreadSafePlausibleConfig(
    eventDir = File(context.applicationContext.filesDir, "events"),
    screenWidth = with(Resources.getSystem().displayMetrics) {
        widthPixels / density
    }.roundToInt()
) {
    init {
        context.resources.getString(R.string.plausible_enable_startup).toBooleanStrictOrNull()
            ?.let {
                enable = it
            }
    }
}
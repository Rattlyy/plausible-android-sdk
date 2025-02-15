package com.onebusaway.plausible.android

import android.content.Context
import timber.log.Timber

/**
 * Class for sending events to Plausible.
 */
class Plausible private constructor(
    private val client: PlausibleClient,
    private val config: PlausibleConfig
) {

    /**
     * Primary constructor for initializing Plausible with a context and domain.
     *
     * @param context The application context.
     * @param domain The domain to track events for.
     */
    constructor(context: Context, domain: String?) : this(
        client = NetworkFirstPlausibleClient(AndroidResourcePlausibleConfig(context)),
        config = AndroidResourcePlausibleConfig(context).apply { this.domain = domain.orEmpty() }
    )

    /**
     * Secondary constructor for advanced initialization with custom client and config.
     *
     * @param client The [PlausibleClient] to use for sending events.
     * @param config The [PlausibleConfig] to use for configuration.
     * @param domain The domain to track events for.
     */
    constructor(client: PlausibleClient, config: PlausibleConfig, domain: String?) : this(
        client = client,
        config = config.apply { this.domain = domain.orEmpty() }
    )

    /**
     * Tertiary constructor for advanced initialization with config, domain, and host.
     * This is useful for adding your own instance of plausible.io.
     * @param context The application context.
     * @param domain The domain to track events for.
     * @param host The host to send events to.
     */

    constructor(context: Context, domain: String?, host: String?) : this(
        client = NetworkFirstPlausibleClient(AndroidResourcePlausibleConfig(context)),
        config = AndroidResourcePlausibleConfig(context).apply { this.domain = domain.orEmpty(); this.host = host.orEmpty() }
    )

    /**
     * Enable or disable event sending
     */
    @Suppress("unused")
    fun enable(enable: Boolean) {
        config?.let {
            it.enable = enable
        } ?: Timber.tag("Plausible")
            .w("Ignoring call to enable(). Did you forget to call Plausible.init()?")
    }

    /**
     * The raw value of User-Agent is used to calculate the user_id which identifies a unique
     * visitor in Plausible.
     * User-Agent is also used to populate the Devices report in your
     * Plausible dashboard. The device data is derived from the open source database
     * device-detector. If your User-Agent is not showing up in your dashboard, it's probably
     * because it is not recognized as one in the device-detector database.
     */
    @Suppress("unused")
    fun setUserAgent(userAgent: String) {
        config?.let {
            it.userAgent = userAgent
        } ?: Timber.tag("Plausible")
            .w("Ignoring call to setUserAgent(). Did you forget to call Plausible.init()?")
    }

    /**
     * Send a `pageview` event.
     *
     * @param url URL of the page where the event was triggered. If the URL contains UTM parameters,
     * they will be extracted and stored.
     * The URL parameter will feel strange in a mobile app but you can manufacture something that looks
     * like a web URL. If you name your mobile app screens like page URLs, Plausible will know how to
     * handle it. So for example, on your login screen you could send something like
     * `app://localhost/login`. The pathname (/login) is what will be shown as the page value in the
     * Plausible dashboard.
     * @param referrer Referrer for this event.
     * Plausible uses the open source referer-parser database to parse referrers and assign these
     * source categories.
     * When no match has been found, the value of the referrer field will be parsed as an URL. The
     * hostname will be used as the `visit:source` and the full URL as the `visit:referrer`. So if
     * you send `https://some.domain.com/example-path`, it will be parsed as follows:
     * `visit:source == some.domain.com` `visit:referrer == some.domain.com/example-path`
     * @param props Custom properties for the event. Values must be scalar. See [https://plausible.io/docs/custom-event-goals#using-custom-props](https://plausible.io/docs/custom-event-goals#using-custom-props)
     * for more information.
     */
    fun pageView(
        url: String,
        referrer: String = "",
        props: Map<String, Any?>? = null
    ) = event(
        name = "pageview",
        url = url,
        referrer = referrer,
        props = props
    )

    /**
     * Send a custom event. To send a `pageview` event, consider using [pageView] instead.
     *
     * @param name Name of the event. Can specify `pageview` which is a special type of event in
     * Plausible. All other names will be treated as custom events.
     * @param url URL of the page where the event was triggered. If the URL contains UTM parameters,
     * they will be extracted and stored.
     * The URL parameter will feel strange in a mobile app but you can manufacture something that looks
     * like a web URL. If you name your mobile app screens like page URLs, Plausible will know how to
     * handle it. So for example, on your login screen you could send something like
     * `app://localhost/login`. The pathname (/login) is what will be shown as the page value in the
     * Plausible dashboard.
     * @param referrer Referrer for this event.
     * Plausible uses the open source referer-parser database to parse referrers and assign these
     * source categories.
     * When no match has been found, the value of the referrer field will be parsed as an URL. The
     * hostname will be used as the `visit:source` and the full URL as the `visit:referrer`. So if
     * you send `https://some.domain.com/example-path`, it will be parsed as follows:
     * `visit:source == some.domain.com` `visit:referrer == some.domain.com/example-path`
     * @param props Custom properties for the event. Values must be scalar. See [https://plausible.io/docs/custom-event-goals#using-custom-props](https://plausible.io/docs/custom-event-goals#using-custom-props)
     * for more information.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun event(
        name: String,
        url: String,
        referrer: String = "",
        props: Map<String, Any?>? = null
    ) {
        client?.let { client ->
            config?.let { config ->
                client.event(config.domain, name, url, referrer, config.screenWidth, props)
            } ?: Timber.tag("Plausible")
                .w("Ignoring call to event(). Did you forget to call Plausible.init()?")
        } ?: Timber.tag("Plausible")
            .w("Ignoring call to event(). Did you forget to call Plausible.init()?")
    }
}
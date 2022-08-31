package jp.co.ta.loaders

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppsDataLoader(
    private val context: Context,
    private val appsFlyerId: String,
    private val oneSignalId: String,
    private val initialLink: String,
    private val googleId: String
) {
    var deepLink: String? = null
    var appsFlyer: MutableMap<String, Any>? = null

    suspend fun loadAppsData(): String {
        deepLink = loadDeepLink()
        if (deepLink != null) return createLink()
        appsFlyer = loadAppsFlyer()
        return createLink()
    }

    private suspend fun loadDeepLink(): String? = suspendCoroutine { continuation ->
        AppLinkData.fetchDeferredAppLinkData(context) { appLinkData ->
            continuation.resume(appLinkData?.targetUri?.toString())
        }
    }

    private suspend fun loadAppsFlyer(): MutableMap<String, Any>? =
        suspendCoroutine {
            val conversionCallback = object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) = it.resume(p0)
                override fun onConversionDataFail(p0: String?) = it.resume(null)
                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) = Unit
                override fun onAttributionFailure(p0: String?) = Unit
            }
            AppsFlyerLib.getInstance().init(appsFlyerId, conversionCallback, context)
            AppsFlyerLib.getInstance().start(context)
        }

    private fun createLink(): String {
        val timeZone: String = TimeZone.getDefault().id
        val mediaSource: String =
            if (deepLink != null) "deeplink" else appsFlyer?.get("media_source")
                .toString()
        val uid: String =
            if (deepLink != null) "null" else AppsFlyerLib.getInstance().getAppsFlyerUID(context)

        val builder: Uri.Builder = "https://$initialLink".toUri().buildUpon()
        builder.appendQueryParameter(SECURE_GET_PARAMETR, SECURE_KEY)
        builder.appendQueryParameter(DEV_TMZ_KEY, timeZone)
        builder.appendQueryParameter(GADID_KEY, googleId)
        builder.appendQueryParameter(DEEPLINK_KEY, deepLink.toString())
        builder.appendQueryParameter(SOURCE_KEY, mediaSource)
        builder.appendQueryParameter(AF_ID_KEY, uid)
        builder.appendQueryParameter(AD_SET_ID_KEY, appsFlyer?.get("adset_id").toString())
        builder.appendQueryParameter(CAMPAIGN_ID_KEY, appsFlyer?.get("campaign_id").toString())
        builder.appendQueryParameter(APP_CAMPAIGN_KEY, appsFlyer?.get("campaign").toString())
        builder.appendQueryParameter(AD_SET_KEY, appsFlyer?.get("adset").toString())
        builder.appendQueryParameter(AD_GROUP_KEY, appsFlyer?.get("adgroup").toString())
        builder.appendQueryParameter(ORIG_COST_KEY, appsFlyer?.get("orig_cost").toString())
        builder.appendQueryParameter(AF_SITE_ID_KEY, appsFlyer?.get("af_siteid").toString())

        return builder.toString().also { sendTag() }
    }

    private fun sendTag() {
        OneSignal.initWithContext(context)
        OneSignal.setAppId(oneSignalId)
        OneSignal.setExternalUserId(googleId)

        val campaign = appsFlyer?.get("campaign").toString()

        when {
            campaign == "null" && deepLink == null -> {
                OneSignal.sendTag("key2", "organic")
            }
            deepLink != null -> {
                OneSignal.sendTag(
                    "key2",
                    deepLink?.replace("myapp://", "")?.substringBefore("/")
                )
            }
            campaign != "null" -> {
                OneSignal.sendTag(
                    "key2",
                    appsFlyer?.get("campaign").toString().substringBefore("_")
                )
            }
        }
    }

    companion object {
        const val SECURE_GET_PARAMETR = "zjZmG5fs1l"
        const val SECURE_KEY = "Dl7j7KoVGo"
        const val DEV_TMZ_KEY = "koo4KZWtWv"
        const val GADID_KEY = "A1yTr8hdyu"
        const val DEEPLINK_KEY = "OoS2iWe4sf"
        const val SOURCE_KEY = "yH6My0R9BT"
        const val AF_ID_KEY = "y7nRFUvj7l"
        const val AD_SET_ID_KEY = "VxYwZ5QHYC"
        const val CAMPAIGN_ID_KEY = "HacX1FVtdZ"
        const val APP_CAMPAIGN_KEY = "kVZZnv0oLe"
        const val AD_SET_KEY = "Kx8criY6MA"
        const val AD_GROUP_KEY = "6uBPdjlLi9"
        const val ORIG_COST_KEY = "0Ubxd0CHh3"
        const val AF_SITE_ID_KEY = "MGeqTtZF9l"
    }
}
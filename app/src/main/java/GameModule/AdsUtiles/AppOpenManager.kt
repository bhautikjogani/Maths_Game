package GameModule.AdsUtiles

import GameModule.GamePreference
import GameModule.GameSound
import GameModule.PrefKey
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame.Companion.MainTAG
import java.util.*

class AppOpenManager(var myGame: MyGame) : Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    private val TAG: String = MainTAG + javaClass.simpleName
    private var mAppOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var isAdLoading = false

    init {
        myGame.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        loadAppOpenAd()
    }

    private fun loadAppOpenAd() {
        if (GamePreference.getBoolean(PrefKey.isRemoveAds)) return
        if (GamePreference.getAdMobAppOpenAdID().isEmpty()) return
        if (isAdLoading) return
        isAdLoading = true
        mAppOpenAd ?: run {
            AppOpenAd.load(
                myGame.applicationContext,
                GamePreference.getAdMobAppOpenAdID(),
                adRequest(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(appOpen: AppOpenAd) {
                        super.onAdLoaded(appOpen)
                        Log.d(TAG, "onAdLoaded: ")
                        mAppOpenAd = appOpen
                        loadTime = Date().time
                        isAdLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Log.d(TAG, "onAdFailedToLoad:   --->  ${error.message}")
                        isAdLoading = false
                    }
                }
            )
        }
    }

    private fun adRequest(): AdRequest = AdRequest.Builder().build()

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4 /*4 - hours*/
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        if (GamePreference.getBoolean(PrefKey.isRemoveAds)) return false
        // Ad references in the app open beta will time out after four hours, but this time limit
        return mAppOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    private fun showAdIfAvailable() {
        Log.d(TAG, "showAdIfAvailable: isAdShowing   --->   ${AdsManager.isAdShowing}")
        if (AdsManager.isAdShowing) return

        if (!isAdAvailable()) {
            Log.d(TAG, "The app open ad is not ready yet.")
            loadAppOpenAd()
            return
        }

        if (currentActivity?.javaClass?.simpleName.toString().lowercase()
                .startsWith("Splash")
        ) {
            Log.d(TAG, "current activity is SplashActivity.")
            return
        }

        mAppOpenAd?.let {
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    Log.d(TAG, "onAdShowedFullScreenContent: ")
                    GameSound.play()?.pauseMusic()
                    AdsManager.isAdShowing = true
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    Log.d(TAG, "onAdDismissedFullScreenContent: ")
                    mAppOpenAd = null
                    loadAppOpenAd()
                    GameSound.play()?.startMusic()
                    AdsManager.isAdShowing = false
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    super.onAdFailedToShowFullScreenContent(error)
                    Log.d(
                        TAG,
                        "onAdFailedToShowFullScreenContent:   --->  ${error.message}"
                    )
                    GameSound.play()?.startMusic()
                    AdsManager.isAdShowing = false
                }
            }
            it.show(currentActivity!!)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // do nothing
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        // do nothing
    }

    override fun onActivityStopped(activity: Activity) {
        // do nothing
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // do nothing
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    /** LifecycleObserver methods  */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(TAG, "onStart:    --->   ${currentActivity?.javaClass?.simpleName}")
        if (currentActivity?.javaClass?.simpleName.toString().lowercase()
                .contains("splash")
        ) return
        showAdIfAvailable()
    }

}

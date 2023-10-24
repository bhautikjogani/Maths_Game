package GameModule.AdsUtiles

import AdsUtiles.BannerAdModel
import AdsUtiles.GoogleAds
import GameModule.GamePreference
import GameModule.PrefKey
import GameModule.ScreenUtil
import GameModule.Utils
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.BuildConfig
import com.google.android.gms.ads.AdSize
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame
import com.globtech.zone.multiplication.table.kids.maths.game.R

class AdsManager {

    private val TAG: String = MyGame.MainTAG + javaClass.simpleName

    var intAdCounter = 0

    companion object {
        var isAdShowing = false
        var instance: AdsManager? = null
        fun show(): AdsManager? = instance
        fun getBannerWidth(context: Context): Int =
            AdSize.BANNER.getWidthInPixels(context) + ScreenUtil.getSize(10)

        fun getBannerHeight(context: Context): Int =
            AdSize.BANNER.getHeightInPixels(context) + ScreenUtil.getSize(10)
    }

    private lateinit var googleAds: GoogleAds

//    private lateinit var unityAdsManager: UnityAdsManager

    fun initialize(activity: Activity) {
        instance = this
        googleAds = GoogleAds(activity)
//        unityAdsManager = UnityAdsManager(activity)
    }

    fun loadAds() {
        googleAds.loadAds()
//        unityAdsManager.loadAds()
    }

    fun InterstitialAd(
        activity: Activity, parent: ViewGroup? = null, adsListener: AdsListener? = null
    ) {
        if (isAdShowing || GamePreference.getBoolean(PrefKey.isRemoveAds) || !Utils.isNetworkAvailable(
                activity
            )
        ) {
            adsListener?.onAdClose()
            return
        }
        when {
            googleAds.isLoadedInterstitialAd() -> {

                showAdLoader(activity, parent) {
                    googleAds.showInterstitialAd(activity, object : AdsListener() {
                        override fun onAdClose() {
                            super.onAdClose()
                            hideAdLoader(it)
                            adsListener?.onAdClose()
                        }

                        override fun onShowedAdClose() {
                            super.onShowedAdClose()
                            adsListener?.onShowedAdClose()
                        }
                    })
                }
            }
            /*unityAdsManager.isLoadedInterstitialAd() -> {
                showAdLoader(activity, parent) {
                    unityAdsManager.showInterstitialAd(activity, object : AdsListener() {
                        override fun onAdClose() {
                            super.onAdClose()
                            hideAdLoader(it)
                            adsListener?.onAdClose()
                        }

                        override fun onShowedAdClose() {
                            super.onShowedAdClose()
                            adsListener?.onShowedAdClose()
                        }
                    })
                }
            }*/
            else -> {
                Log.d(TAG, "InterstitialAd:    --->   no interstitialAd loaded ")
                adsListener?.onAdClose()
            }
        }
    }


    fun InterstitialAdIntervaled(
        activity: Activity, parent: ViewGroup? = null, adsListener: AdsListener? = null
    ) {

        if (GamePreference.getIntInterval() == 0 || isAdShowing) {
            adsListener?.onAdClose()
            return
        }

        if (GamePreference.getBoolean(PrefKey.isRemoveAds) || !Utils.isNetworkAvailable(activity)) {
            adsListener?.onAdClose()
            return
        }
        intAdCounter++
        if (intAdCounter != GamePreference.getIntInterval()) {
            Log.d(TAG, "InterstitialAd: lack of ad counter")
            adsListener?.onAdClose()
            return
        }
        intAdCounter = 0
        InterstitialAd(activity, parent, adsListener)
    }

    fun RewardedAd(activity: Activity, adsListener: AdsListener? = null) {
        if (BuildConfig.DEBUG) {
            adsListener?.onAdRewarded()
            return
        }
        if (!Utils.isNetworkAvailable(activity)) {
            Toast.makeText(
                activity, activity.resources.getString(R.string.NoConnection), Toast.LENGTH_SHORT
            ).show()
            adsListener?.onAdClose()
            return
        }
        if (isAdShowing) {
            adsListener?.onAdClose()
            return
        }
        when {
            googleAds.isLoadedRewardedAd() -> googleAds.showRewardedAd(activity, adsListener)
            /*unityAdsManager.isLoadedRewardedAd() -> unityAdsManager.showRewardedAd(
                activity, adsListener
            )*/
            else -> {
                Toast.makeText(
                    activity, activity.resources.getString(R.string.NoAdLoaded), Toast.LENGTH_SHORT
                ).show()
                adsListener?.onAdClose()
            }
        }
    }

    fun RewardedInterstitialAd(activity: Activity, adsListener: AdsListener? = null) {
        if (!Utils.isNetworkAvailable(activity)) {
            Toast.makeText(
                activity, activity.resources.getString(R.string.NoConnection), Toast.LENGTH_SHORT
            ).show()
            adsListener?.onAdClose()
            return
        }
        if (isAdShowing || GamePreference.getBoolean(PrefKey.isRemoveAds)) {
            adsListener?.onAdClose()
            return
        }
        if (googleAds.isLoadedRewardedInterstitialAd()) googleAds.showRewardedInterstitialAd(
            activity, adsListener
        )
    }

    fun BannerAd(activity: Activity, parent: ViewGroup, bannerAdListener: BannerAdListener? = null) {
        if (GamePreference.getBoolean(PrefKey.isRemoveAds) || Utils.isNetworkAvailable(activity)
                .not()
        ) {
            bannerAdListener?.onBannerAdError()
            return
        }
        googleAds.showBannerAd(activity, parent, object : BannerAdListener() {
            override fun onBannerAdLoaded(bannerAdModel: BannerAdModel) {
                super.onBannerAdLoaded(bannerAdModel)
                bannerAdListener?.onBannerAdLoaded(bannerAdModel)
            }

            override fun onBannerAdError() {
                super.onBannerAdError()
                bannerAdListener?.onBannerAdError()
            }
        })
    }


    /**/

    private fun showAdLoader(
        activity: Activity, parent: ViewGroup? = null, callback: (view: View?) -> Unit
    ): View {
        return TextView(activity).apply {
            text = "Ad is Loading..."
            gravity = Gravity.CENTER
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.typeface = this.context.resources.getFont(R.font.candybeans)
            } else {
                this.typeface = ResourcesCompat.getFont(activity, R.font.candybeans)
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtil.getSize(24f))
            setTextColor(context.resources.getColor(R.color.ad_text))
            setBackgroundColor(context.resources.getColor(R.color.ad_background))
            this.setOnClickListener { }

            if (parent == null) {
                activity.addContentView(
                    this, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            } else {
                parent.addView(
                    this, ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }


            Handler(Looper.getMainLooper()).postDelayed({
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        this.visibility = View.GONE
                        (this.parent as ViewGroup).removeView(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 1000)
                callback.invoke(this)
            }, 1000)

        }

    }

    private fun hideAdLoader(view: View?) {
        try {
            (view?.parent as ViewGroup).removeView(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        try {
            googleAds.destroy()
            instance = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}

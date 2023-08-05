package AdsUtiles

import GameModule.AdsUtiles.AdsListener
import GameModule.AdsUtiles.AdsManager
import GameModule.AdsUtiles.BannerAdListener
import GameModule.GamePreference
import GameModule.GameSound
import GameModule.PrefKey
import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame

class GoogleAds(var activity: Activity) {

    private val TAG: String = MyGame.MainTAG + javaClass.simpleName

    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null

    init {
        MobileAds.initialize(activity.applicationContext) { initializationStatus ->
//            val statusMap =
//                initializationStatus.adapterStatusMap
//            for (adapterClass in statusMap.keys) {
//                val status = statusMap[adapterClass]
//                Log.d(
//                    TAG, String.format(
//                        "Adapter name: %s, Description: %s, Latency: %d",
//                        adapterClass, status!!.description, status.latency
//                    )
//                )
//            }
        }
        loadAds()
    }

    private fun adRequest(): AdRequest = AdRequest.Builder().build()

    fun loadAds() {
        loadInterstitialAd()
        loadRewardedAd()
        loadRewardedInterstitialAd()
    }

    //region: InterstitialAd
    private var isInterstitialAdLoading = false

    private fun loadInterstitialAd() {
        if (GamePreference.getBoolean(PrefKey.isRemoveAds)) return
        if (GamePreference.getAdMobInterstitialAdID().isEmpty() || isInterstitialAdLoading) return
        isInterstitialAdLoading = true
        mInterstitialAd ?: run {
            InterstitialAd.load(
                activity,
                GamePreference.getAdMobInterstitialAdID(),
                adRequest(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        super.onAdLoaded(interstitialAd)
                        Log.d(TAG, "I_onAdLoaded: ")
                        mInterstitialAd = interstitialAd
                        isInterstitialAdLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Log.d(TAG, "I_onAdFailedToLoad:  ---> " + error.message)
                        mInterstitialAd = null
                        isInterstitialAdLoading = false
                    }
                })
        }
    }

    fun isLoadedInterstitialAd(): Boolean = mInterstitialAd?.let { true } ?: run {
        loadInterstitialAd()
        false
    }

    fun showInterstitialAd(activity: Activity, adListener: AdsListener? = null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Log.d(TAG, "I_onAdShowedFullScreenContent: ")
                mInterstitialAd = null
                loadInterstitialAd()
                AdsManager.isAdShowing = true
                GameSound.play()?.pauseMusic()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d(TAG, "I_onAdDismissedFullScreenContent: ")
                adListener?.onShowedAdClose()
                adListener?.onAdClose()
                AdsManager.isAdShowing = false
                GameSound.play()?.startMusic()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                super.onAdFailedToShowFullScreenContent(error)
                Log.d(TAG, "I_onAdFailedToShowFullScreenContent: ")
                mInterstitialAd = null
                loadInterstitialAd()
                adListener?.onAdClose()
                AdsManager.isAdShowing = false
                GameSound.play()?.startMusic()
            }
        }
        mInterstitialAd?.show(activity)
    }
    //endregion

    //region: RewardedAd
    private var isRewardedAdLoading = false

    private fun loadRewardedAd() {
        if (GamePreference.getAdMobRewardedAdID().isEmpty() || isRewardedAdLoading) return
        isRewardedAdLoading = true
        mRewardedAd ?: run {
            RewardedAd.load(
                activity.applicationContext,
                GamePreference.getAdMobRewardedAdID(),
                adRequest(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        super.onAdLoaded(rewardedAd)
                        Log.d(TAG, "R_onAdLoaded: ")
                        mRewardedAd = rewardedAd
                        isRewardedAdLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Log.d(TAG, "R_onAdFailedToLoad:  --->  ${error.message}")
                        mRewardedAd = null
                        isRewardedAdLoading = false
                    }
                })
        }
    }

    fun isLoadedRewardedAd(): Boolean = mRewardedAd?.let { true } ?: run {
        loadRewardedAd()
        false
    }

    fun showRewardedAd(activity: Activity, adsListener: AdsListener? = null) {
        var isRewarded = false
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Log.d(TAG, "R_onAdShowedFullScreenContent: ")
                AdsManager.isAdShowing = true
                GameSound.play()?.pauseMusic()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d(TAG, "R_onAdDismissedFullScreenContent: ")
                AdsManager.isAdShowing = false
                mRewardedAd = null
                loadRewardedAd()
                adsListener?.let { it ->
                    if (isRewarded) it.onAdRewarded()
                    else it.onAdClose()
                }
                GameSound.play()?.startMusic()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                Log.d(TAG, "R_onAdFailedToShowFullScreenContent: ")
                AdsManager.isAdShowing = false
                mRewardedAd = null
                loadRewardedAd()
                GameSound.play()?.startMusic()
            }
        }
        mRewardedAd?.show(activity) { isRewarded = true }
    }
    //endregion

    //region: RewardedInterstitialAd
    private var isRewardedInterstitialAdLoading = false

    private fun loadRewardedInterstitialAd() {
        if (GamePreference.getBoolean(PrefKey.isRemoveAds)) return
        if (GamePreference.getAdMobRewardedIntAdID()
                .isEmpty() || isRewardedInterstitialAdLoading
        ) return
        isRewardedInterstitialAdLoading = true
        mRewardedInterstitialAd ?: run {
            RewardedInterstitialAd.load(
                activity.applicationContext,
                GamePreference.getAdMobRewardedIntAdID(),
                adRequest(),
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                        super.onAdLoaded(rewardedInterstitialAd)
                        Log.d(TAG, "RI_onAdLoaded: ")
                        mRewardedInterstitialAd = rewardedInterstitialAd
                        isRewardedInterstitialAdLoading = false
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Log.d(TAG, "RI_onAdFailedToLoad:  --->   ${error.message}")
                        mRewardedInterstitialAd = null
                        isRewardedInterstitialAdLoading = false
                    }
                })
        }
    }

    fun isLoadedRewardedInterstitialAd(): Boolean =
        mRewardedInterstitialAd?.let { true } ?: run {
            loadRewardedInterstitialAd()
            false
        }

    fun showRewardedInterstitialAd(activity: Activity, adsListener: AdsListener? = null) {
        var isRewarded = false
        mRewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Log.d(TAG, "RI_onAdShowedFullScreenContent: ")
                AdsManager.isAdShowing = true
                GameSound.play()?.pauseMusic()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d(TAG, "RI_onAdDismissedFullScreenContent: ")
                AdsManager.isAdShowing = false
                mRewardedInterstitialAd = null
                loadRewardedInterstitialAd()
                adsListener?.let { it ->
                    if (isRewarded) it.onAdRewarded()
                    else it.onAdClose()
                }
                GameSound.play()?.startMusic()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                super.onAdFailedToShowFullScreenContent(error)
                Log.d(TAG, "RI_onAdFailedToShowFullScreenContent:  --->   ${error.message}")
                AdsManager.isAdShowing = false
                mRewardedInterstitialAd = null
                loadRewardedInterstitialAd()
                GameSound.play()?.startMusic()
            }
        }
        mRewardedInterstitialAd?.show(activity) { rewardItem -> isRewarded = true }
    }
    //endregion

    //region: BannerAd
    fun showBannerAd(activity: Activity, parent: ViewGroup, bannerAdListener: BannerAdListener) {
        if (GamePreference.getAdMobBannerAdID().isEmpty()) {
            bannerAdListener.onBannerAdError()
            return
        }
        val adview = AdView(activity)
        adview.adUnitId = GamePreference.getAdMobBannerAdID()
        adview.setAdSize(AdSize.BANNER)
        adview.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "B_onAdLoaded: ")
                parent.addView(
                    adview,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_HORIZONTAL
                    )
                )
//                adview.pause()
                bannerAdListener.onBannerAdLoaded(BannerAdModel(adview))
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d(TAG, "B_onAdFailedToLoad:   --->   ${p0.message}")
                bannerAdListener.onBannerAdError()
            }
        }
        adview.loadAd(adRequest())
    }
    //endregion

    fun destroy() {
        mInterstitialAd = null
        mRewardedAd = null
        mRewardedInterstitialAd = null
    }

}

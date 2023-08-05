//package AdsUtiles
//
//import android.app.Activity
//import android.util.Log
//import com.oxygameslab.gamemodule.*
//import com.ironsource.mediationsdk.ISBannerSize
//import com.ironsource.mediationsdk.IronSource
//import com.ironsource.mediationsdk.integration.IntegrationHelper
//import com.ironsource.mediationsdk.logger.IronSourceError
//import com.ironsource.mediationsdk.model.Placement
//import com.ironsource.mediationsdk.sdk.BannerListener
//import com.ironsource.mediationsdk.sdk.InterstitialListener
//import com.ironsource.mediationsdk.sdk.RewardedVideoListener
//
//class IronSourceAds() {
//
//    private val TAG: String = MyGame.MainTAG + javaClass.simpleName
//
//    fun Initialize(activity: Activity) {
//        IronSource.init(
//            activity,
//            activity.resources.getString(R.string.ID_IronsourceAppID),
//            IronSource.AD_UNIT.BANNER,
//            IronSource.AD_UNIT.INTERSTITIAL,
//            IronSource.AD_UNIT.REWARDED_VIDEO
//        )
//        IntegrationHelper.validateIntegration(activity)
//        loadInterstitialAd()
//    }
//
//    //region: InterstitialAd
//    fun loadInterstitialAd() {
//        if (GamePreference.getBoolean(BuildConfig.isRemoveAds)) return
//        if (!AdsManager.isShowIronSourceAd || !AdsManager.isShowInterstitialAd) return
//        IronSource.loadInterstitial()
//    }
//
//    fun isLoadedInterstitialAd(): Boolean = IronSource.isInterstitialReady()
//
//    fun showInterstitialAd(activity: Activity, adListener: AdsListener? = null) {
//        IronSource.setInterstitialListener(object : InterstitialListener {
//            override fun onInterstitialAdReady() {
//                Log.d(TAG, "onInterstitialAdReady: ")
//            }
//
//            override fun onInterstitialAdLoadFailed(error: IronSourceError?) {
//                Log.d(TAG, "onInterstitialAdLoadFailed:   --->   ${error?.errorMessage}")
//            }
//
//            override fun onInterstitialAdOpened() {
//                Log.d(TAG, "onInterstitialAdOpened: ")
//                AdsManager.isAdShowing = true
//                GameSound.play()?.pauseMusic()
//            }
//
//            override fun onInterstitialAdClosed() {
//                Log.d(TAG, "onInterstitialAdClosed: ")
//                adListener?.onAdClose()
//                adListener?.onShowedAdClose()
//                AdsManager.isAdShowing = false
//                loadInterstitialAd()
//                GameSound.play()?.startMusic()
//            }
//
//            override fun onInterstitialAdShowSucceeded() {
//                Log.d(TAG, "onInterstitialAdShowSucceeded: ")
//            }
//
//            override fun onInterstitialAdShowFailed(error: IronSourceError?) {
//                Log.d(TAG, "onInterstitialAdShowFailed:  --->   ${error?.errorMessage}")
//                adListener?.onAdClose()
//                AdsManager.isAdShowing = false
//                loadInterstitialAd()
//                GameSound.play()?.startMusic()
//            }
//
//            override fun onInterstitialAdClicked() {
//                Log.d(TAG, "onInterstitialAdClicked: ")
//            }
//        })
//        IronSource.showInterstitial()
//    }
//    //endregion
//
//    //region: RewardedAd
//    fun isLoadedRewardedAd(): Boolean =
//        AdsManager.isShowIronSourceAd && IronSource.isRewardedVideoAvailable()
//
//    fun showRewardedAd(activity: Activity, adListener: AdsListener? = null) {
//        var isRewarded: Boolean = false
//        IronSource.setRewardedVideoListener(object : RewardedVideoListener {
//            override fun onRewardedVideoAdOpened() {
//                Log.d(TAG, "onRewardedVideoAdOpened: ")
//                AdsManager.isAdShowing = true
//                GameSound.play()?.pauseMusic()
//            }
//
//            override fun onRewardedVideoAdClosed() {
//                Log.d(TAG, "onRewardedVideoAdClosed: ")
//                AdsManager.isAdShowing = false
//                adListener?.let {
//                    if (isRewarded) it.onAdRewarded()
//                    else it.onAdClose()
//                }
//                GameSound.play()?.startMusic()
//            }
//
//            override fun onRewardedVideoAvailabilityChanged(p0: Boolean) {
//                Log.d(TAG, "onRewardedVideoAvailabilityChanged: ")
//            }
//
//            override fun onRewardedVideoAdStarted() {
//                Log.d(TAG, "onRewardedVideoAdStarted: ")
//            }
//
//            override fun onRewardedVideoAdEnded() {
//                Log.d(TAG, "onRewardedVideoAdEnded: ")
//            }
//
//            override fun onRewardedVideoAdRewarded(placement: Placement?) {
//                Log.d(TAG, "onRewardedVideoAdRewarded: ")
//                isRewarded = true
//            }
//
//            override fun onRewardedVideoAdShowFailed(error: IronSourceError?) {
//                Log.d(TAG, "onRewardedVideoAdShowFailed:   --->   ${error?.errorMessage}")
//                AdsManager.isAdShowing = false
//                adListener?.onAdClose()
//                GameSound.play()?.startMusic()
//            }
//
//            override fun onRewardedVideoAdClicked(placement: Placement?) {
//                Log.d(TAG, "onRewardedVideoAdClicked: ")
//            }
//
//        })
//        IronSource.showRewardedVideo()
//    }
//    //endregion
//
//    //region: BannerAd
//    fun showBannerAd(activity: Activity, bannerAdListener: BannerAdListener) {
//        val banner = IronSource.createBanner(activity, ISBannerSize.BANNER)
//        banner.bannerListener = object : BannerListener {
//            override fun onBannerAdLoaded() {
//                Log.d(TAG, "onBannerAdLoaded: ")
//                bannerAdListener.onBannerAdLoaded(BannerAdModel(banner))
//            }
//
//            override fun onBannerAdLoadFailed(p0: IronSourceError?) {
//                Log.d(
//                    TAG,
//                    "onBannerAdLoadFailed:    --->   ${p0?.errorMessage + "     --->     " + p0?.errorCode}"
//                )
//                bannerAdListener.onBannerAdError()
//            }
//
//            override fun onBannerAdClicked() {
//                Log.d(TAG, "onBannerAdClicked: ")
//            }
//
//            override fun onBannerAdScreenPresented() {
//                Log.d(TAG, "onBannerAdScreenPresented: ")
//            }
//
//            override fun onBannerAdScreenDismissed() {
//                Log.d(TAG, "onBannerAdScreenDismissed: ")
//            }
//
//            override fun onBannerAdLeftApplication() {
//                Log.d(TAG, "onBannerAdLeftApplication: ")
//            }
//        }
//        IronSource.loadBanner(banner)
//    }
//    //endregion
//
//}
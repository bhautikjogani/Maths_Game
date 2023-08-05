//package AdsUtiles
//
//import GameModule.AdsUtiles.AdsListener
//import GameModule.AdsUtiles.AdsManager
//import GameModule.GamePreference
//import GameModule.GameSound
//import GameModule.PrefKey
//import android.app.Activity
//import android.util.Log
//import com.ingenious.games.dominoesoffline.BuildConfig
//import com.ingenious.games.dominoesoffline.MyGame
//
//class UnityAdsManager(activity: Activity) {
//
//    private val TAG: String = MyGame.MainTAG + javaClass.simpleName
//
//    init {
//        UnityAds.initialize(
//            activity.applicationContext,
//            GamePreference.getUnityAppId(activity.applicationContext),
//            BuildConfig.DEBUG,
//            object : IUnityAdsInitializationListener {
//                override fun onInitializationComplete() {
//                    Log.d(TAG, "onInitializationComplete: ")
//                    loadAds()
//                }
//
//                override fun onInitializationFailed(
//                    error: UnityAds.UnityAdsInitializationError?,
//                    message: String?
//                ) {
//                    Log.d(TAG, "onInitializationFailed:  $message")
//                }
//
//            })
//    }
//
//    fun loadAds() {
//        loadRewardedAd()
//        loadInterstitialAd()
//    }
//
//    //region: InterstitialAd
//    private var isInterstitialAdLoaded = false
//    private var isInterstitialAdLoading = false
//
//    fun loadInterstitialAd() {
//        if (GamePreference.getBoolean(PrefKey.isRemoveAds)) return
//        if (GamePreference.isShowUnityInt().not()) return
//        if (isInterstitialAdLoaded || isInterstitialAdLoading) return
//        isInterstitialAdLoading = true
//        UnityAds.load("Interstitial_Android", object : IUnityAdsLoadListener {
//            override fun onUnityAdsAdLoaded(placementId: String?) {
//                Log.d(TAG, "onUnityAdsAdLoaded:   $placementId")
//                isInterstitialAdLoaded = true
//                isInterstitialAdLoading = false
//            }
//
//            override fun onUnityAdsFailedToLoad(
//                placementId: String?,
//                error: UnityAds.UnityAdsLoadError?,
//                message: String?
//            ) {
//                Log.d(TAG, "onUnityAdsFailedToLoad:  $message")
//                isInterstitialAdLoaded = false
//                isInterstitialAdLoading = false
//            }
//
//        })
//    }
//
//    fun isLoadedInterstitialAd(): Boolean = isInterstitialAdLoaded
//
//    fun showInterstitialAd(activity: Activity, adListener: AdsListener? = null) {
//        GameSound.play()?.pauseMusic()
//        UnityAds.show(
//            activity,
//            "Interstitial_Android",
//            UnityAdsShowOptions(),
//            object : IUnityAdsShowListener {
//                override fun onUnityAdsShowFailure(
//                    placementId: String?,
//                    error: UnityAds.UnityAdsShowError?,
//                    message: String?
//                ) {
//                    Log.d(TAG, "onUnityAdsShowFailure: ")
//                    adListener?.onAdClose()
//                    AdsManager.isAdShowing = false
//                    isInterstitialAdLoaded = false
//                    loadInterstitialAd()
//                    GameSound.play()?.startMusic()
//                }
//
//                override fun onUnityAdsShowStart(placementId: String?) {
//                    Log.d(TAG, "onUnityAdsShowStart: ")
//                    isInterstitialAdLoaded = false
//                    AdsManager.isAdShowing = true
//                    GameSound.play()?.pauseMusic()
//                }
//
//                override fun onUnityAdsShowClick(placementId: String?) {
//                    Log.d(TAG, "onUnityAdsShowClick: ")
//                }
//
//                override fun onUnityAdsShowComplete(
//                    placementId: String?,
//                    state: UnityAds.UnityAdsShowCompletionState?
//                ) {
//                    Log.d(TAG, "onUnityAdsShowComplete: ")
//
//                    adListener?.onShowedAdClose()
//                    adListener?.onAdClose()
//                    AdsManager.isAdShowing = false
//                    loadInterstitialAd()
//                    GameSound.play()?.startMusic()
//                }
//
//            }
//        )
//
//    }
//    //endregion
//
//    //region: RewardedAd
//    private var isRewardedAdsLoaded = false
//    private var isRewardedAdsLoading = false
//
//    private fun loadRewardedAd() {
//        if (isRewardedAdsLoaded || isRewardedAdsLoading) return
//        if (GamePreference.isShowUnityRewardedAd().not()) return
//        isRewardedAdsLoading = true
//        UnityAds.load("Rewarded_Android", object : IUnityAdsLoadListener {
//            override fun onUnityAdsAdLoaded(placementId: String?) {
//                Log.d(TAG, "onUnityAdsAdLoaded:   $placementId")
//                isRewardedAdsLoaded = true
//                isRewardedAdsLoading = false
//            }
//
//            override fun onUnityAdsFailedToLoad(
//                placementId: String?,
//                error: UnityAds.UnityAdsLoadError?,
//                message: String?
//            ) {
//                Log.d(TAG, "onUnityAdsAdLoaded:   $message")
//                isRewardedAdsLoaded = false
//                isRewardedAdsLoading = false
//            }
//        })
//    }
//
//    fun isLoadedRewardedAd(): Boolean {
//        if (isRewardedAdsLoaded) return true
//        loadRewardedAd()
//        return isRewardedAdsLoaded
//    }
//
//    fun showRewardedAd(activity: Activity, adsListener: AdsListener? = null) {
//        GameSound.play()?.pauseMusic()
//        UnityAds.show(
//            activity,
//            "Rewarded_Android",
//            UnityAdsShowOptions(),
//            object : IUnityAdsShowListener {
//                override fun onUnityAdsShowFailure(
//                    placementId: String?,
//                    error: UnityAds.UnityAdsShowError?,
//                    message: String?
//                ) {
//                    Log.d(TAG, "onUnityAdsShowFailure:  $message")
//                    AdsManager.isAdShowing = false
//                    isRewardedAdsLoaded = false
//                    loadRewardedAd()
//                    GameSound.play()?.startMusic()
//                }
//
//                override fun onUnityAdsShowStart(placementId: String?) {
//                    Log.d(TAG, "onUnityAdsShowStart: ")
//                    AdsManager.isAdShowing = true
//                    GameSound.play()?.pauseMusic()
//                }
//
//                override fun onUnityAdsShowClick(placementId: String?) {
//                    Log.d(TAG, "onUnityAdsShowClick: ")
//                }
//
//                override fun onUnityAdsShowComplete(
//                    placementId: String?,
//                    state: UnityAds.UnityAdsShowCompletionState?
//                ) {
//                    Log.d(TAG, "onUnityAdsShowComplete: ")
//                    AdsManager.isAdShowing = false
//                    if (state?.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED) == true) {
//                        // Reward the user for watching the ad to completion
//                        adsListener?.onAdRewarded()
//                    } else {
//                        // Do not reward the user for skipping the ad
//                        adsListener?.onAdClose()
//                    }
//                    isRewardedAdsLoaded = false
//                    loadRewardedAd()
//                    GameSound.play()?.startMusic()
//
//                }
//            }
//        )
//
//    }
//    //endregion
//
//}

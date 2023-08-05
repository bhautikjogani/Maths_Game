package GameModule.AdsUtiles

import AdsUtiles.BannerAdModel

abstract class BannerAdListener {

    open fun onBannerAdLoaded(bannerAdModel: BannerAdModel) {}

    open fun onBannerAdError() {}

}
package GameModule.AdsUtiles

abstract class AdsListener {

    open fun onAdClose() {}

    open fun onShowedAdClose() {}

    open fun onAdRewarded() {}

}
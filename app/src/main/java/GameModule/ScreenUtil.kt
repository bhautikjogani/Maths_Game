package GameModule

import android.content.res.Resources
import android.util.Log
import java.nio.channels.FileLock

object ScreenUtil {

    var screenWidth = 1080
    var screenHeight = 1920
    var notchSize = 0
    var screenRatio = 406
    var density = 1f


    init {
        density = Resources.getSystem().displayMetrics.density
        Log.d("_JB_ScreenUtil", " density --> $density ")
    }


    //    Portrait Mode Game
    fun getSize(w: Int): Int {
        return if (screenWidth == 0) w else screenWidth * w / screenRatio
    }

    fun getSizeDensity(size: Int): Int {
        return if (screenWidth == 0 || size <= 0) size
        else screenWidth * (size / density).toInt() / screenRatio
    }

    fun getDP(size: Int): Int {
        return if (size == 0) 0 else (size / density).toInt()
    }

    fun getDP(size: Float): Float {
        return if (size == 0f) 0f else (size / density)
    }

    fun getDP2(size: Int): Int {
        return if (size <= 0) size else (size / density).toInt()
    }

    fun getMarginDensity(size: Int): Int {
        return if (screenWidth == 0 || size == 0) size
        else screenWidth * (size / density).toInt() / screenRatio
    }

    fun getSize(w: Float): Float {
        return if (screenWidth == 0) w else screenWidth.toFloat() * w / screenRatio.toFloat()
    }

    fun getSizeDensity(size: Float): Float {
        return if (screenWidth == 0 || size == 0f) size
        else screenWidth * (size / density) / screenRatio
    }


//    //    Landscape Mode Game
//    fun getSize(h: Int): Int {
//        return if (screenHeight == 0) h else screenHeight * h / screenRatio
//    }
//
//    fun getSize(h: Float): Float {
//        return if (screenHeight == 0) h else screenHeight.toFloat() * h / screenRatio.toFloat()
//    }

    fun getLandscapeScreenRatio(): Int {
        val temp: Int
        val screenRatio = screenWidth.toFloat() / screenHeight.toFloat()
        val screenSizeRatio = 360f //404.0f
        var retVal = screenSizeRatio
        if (screenRatio < 1.0f) {
            temp = 231
        } else if (screenRatio > 1.12) {
            if (screenRatio.toDouble() in 1.12..1.23) {
                temp = 189
            } else if (screenRatio.toDouble() in 1.23..1.34) {
                temp = 168
            } else if (screenRatio.toDouble() in 1.34..1.45) {
                temp = 147
            } else if (screenRatio.toDouble() in 1.45..1.56) {
                temp = 126
            } else if (screenRatio.toDouble() in 1.56..1.67) {
                temp = 105
            } else if (screenRatio.toDouble() in 1.67..1.77) {
                temp = 84
            } else if (screenRatio.toDouble() < 1.77 || screenRatio.toDouble() > 1.89) {
                if (screenRatio.toDouble() >= 1.89) {
                    retVal = screenSizeRatio + 42.toFloat()
                }
                return retVal.toInt()
            } else {
                temp = 63
            }
        } else {
            temp = 210
        }
        retVal = screenSizeRatio + temp.toFloat()
        return retVal.toInt()
    }

    fun getPortraitScreenRatio(): Int {
        val temp: Int
        val screenRatio = screenHeight.toFloat() / screenWidth.toFloat()
        val screenSizeRatio = 404f * .95f
        var retVal = screenSizeRatio
        if (screenRatio < 1.0f) {
            temp = 231
        } else if (screenRatio > 1.12) {
            if (screenRatio.toDouble() in 1.12..1.23) {
                temp = 189
            } else if (screenRatio.toDouble() in 1.23..1.34) {
                temp = 168
            } else if (screenRatio.toDouble() in 1.34..1.45) {
                temp = 147
            } else if (screenRatio.toDouble() in 1.45..1.56) {
                temp = 126
            } else if (screenRatio.toDouble() in 1.56..1.67) {
                temp = 105
            } else if (screenRatio.toDouble() in 1.67..1.77) {
                temp = 84
            } else if (screenRatio.toDouble() < 1.77 || screenRatio.toDouble() > 1.89) {
                if (screenRatio.toDouble() >= 1.89) {
                    retVal = screenSizeRatio + 42.toFloat()
                }
                return retVal.toInt()
            } else {
                temp = 63
            }
        } else {
            temp = 210
        }
        retVal = screenSizeRatio + temp.toFloat()
        return retVal.toInt()
    }

    fun getPortraitScreenRatiooooo(): Int {
        val temp: Int
        val screenRatio = 1920 / 1080
        val screenSizeRatio = 404f
        var retVal = screenSizeRatio
        if (screenRatio < 1.0f) {
            temp = 231
        } else if (screenRatio > 1.12) {
            if (screenRatio.toDouble() in 1.12..1.23) {
                temp = 189
            } else if (screenRatio.toDouble() in 1.23..1.34) {
                temp = 168
            } else if (screenRatio.toDouble() in 1.34..1.45) {
                temp = 147
            } else if (screenRatio.toDouble() in 1.45..1.56) {
                temp = 126
            } else if (screenRatio.toDouble() in 1.56..1.67) {
                temp = 105
            } else if (screenRatio.toDouble() in 1.67..1.77) {
                temp = 84
            } else if (screenRatio.toDouble() < 1.77 || screenRatio.toDouble() > 1.89) {
                if (screenRatio.toDouble() >= 1.89) {
                    retVal = screenSizeRatio + 42.toFloat()
                }
                return retVal.toInt()
            } else {
                temp = 63
            }
        } else {
            temp = 210
        }
        retVal = screenSizeRatio + temp.toFloat()
        return retVal.toInt()
    }

}

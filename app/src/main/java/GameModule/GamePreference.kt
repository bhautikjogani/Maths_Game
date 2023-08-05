package GameModule

import GameModule.Models.SharedPreferenceLiveData
import android.content.Context
import android.content.SharedPreferences
import com.globtech.zone.multiplication.table.kids.maths.game.R
import kotlin.math.max

object GamePreference {

    lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun initialize(context: Context) {
        preferences = context.getSharedPreferences("myGame", Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.apply()
    }

    //region: Boolean
    fun getBoolean(
        key: String,
        defValue: Boolean = key == PrefKey.isMusic || key == PrefKey.isSound || key == PrefKey.isNotify || key == PrefKey.isVibration
    ): Boolean {
        return preferences.getBoolean(
            key,
            defValue
        )
    }

    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).commit()
    }
    //endregion


    //region: String
    fun getString(key: String): String {
        var defVal = ""
//        if (key == PrefKey.userName) defVal = "YOU"
        return preferences.getString(key, defVal).toString()
    }

    fun setString(key: String, value: String) {
        editor.putString(key, value).commit()
    }
    //endregion


    //region: Integer
    fun getInteger(key: String): Int {
        val defVal = if (key == PrefKey.freeSpin) 3 else 0
        return preferences.getInt(key, defVal)
    }

    fun addInteger(key: String, value: Int, leaderBoardLogin: LeaderBoardLogin? = null) {
        setInteger(key, getInteger(key) + value, leaderBoardLogin)
    }

    fun setInteger(key: String, value: Int, leaderBoardLogin: LeaderBoardLogin? = null) {
        leaderBoardLogin?.submitScore(value.toLong())
        editor.putInt(key, value).commit()
    }
    //endregion


    //region: Float
    fun getFloat(key: String, defVal: Float = 0f): Float {
        return preferences.getFloat(key, if (key == PrefKey.userLevel) 1f else defVal)
    }

    fun addFloat(key: String, value: Float) {
        setFloat(key, getFloat(key) + value)
    }

    fun setFloat(key: String, value: Float) {
        if (key == PrefKey.userLevel && getFloat(key).toInt() != value.toInt()) {
            setBoolean(PrefKey.isShowLevelUpPopup, true)
        }
        editor.putFloat(key, value).commit()
    }
    //endregion


    //region: Long
    fun getLong(key: String, defVal: Long = if (key == PrefKey.userCoins) 5000 else 0): Long {
        return preferences.getLong(key, defVal)
    }

    fun addLong(key: String, value: Long, leaderBoardLogin: LeaderBoardLogin? = null) {
        setLong(key, getLong(key) + value, leaderBoardLogin)
    }

    fun setLong(key: String, value: Long, leaderBoardLogin: LeaderBoardLogin? = null) {
        leaderBoardLogin?.submitScore(value)
        editor.putLong(key, if (key == PrefKey.userCoins) max(0, value) else value).commit()
    }
    //endregion


    //region: Ads Preference

    private val intAdsInterval = "intI"
    private val isShowUIntAdKey = "unityI"
    private val isShowURewardedAdKey = "unityR"

    /* Interval */
    fun getIntInterval(): Int {
        return preferences.getInt(intAdsInterval, 1) // change to 0
    }

    fun setIntInterval(interval: Int): Boolean {
        val isChanges = interval != getIntInterval()
        editor.putInt(intAdsInterval, interval).commit()
        return isChanges
    }

    /* AdMOb */
    fun getAdMobInterstitialAdID(): String {
        return preferences.getString("admob_I", "").toString()
    }

    fun setAdMobInterstitialAdID(id: String): Boolean {
        val returnVal = getAdMobInterstitialAdID() == id
        editor.putString("admob_I", id).commit()
        return returnVal
    }

    fun getAdMobRewardedAdID(): String {
        return preferences.getString("admob_R", "").toString()
    }

    fun setAdMobRewardedAdID(id: String): Boolean {
        val returnVal = getAdMobRewardedAdID() == id
        editor.putString("admob_R", id).commit()
        return returnVal
    }

    fun getAdMobRewardedIntAdID(): String {
        return preferences.getString("admob_RI", "").toString()
    }

    fun setAdMobRewardedIntAdID(id: String): Boolean {
        val returnVal = getAdMobRewardedIntAdID() == id
        editor.putString("admob_RI", id).commit()
        return returnVal
    }

    fun getAdMobBannerAdID(): String {
        return preferences.getString("admob_B", "").toString()
    }

    fun setAdMobBannerAdID(id: String): Boolean {
        val returnVal = getAdMobBannerAdID() == id
        editor.putString("admob_B", id).commit()
        return returnVal
    }

    fun getAdMobAppOpenAdID(): String {
        return preferences.getString("admob_AO", "").toString()
    }

    fun setAdMobAppOpenAdID(id: String): Boolean {
        val returnVal = getAdMobAppOpenAdID() == id
        editor.putString("admob_AO", id).commit()
        return returnVal
    }

    /* Unity Ads */
    fun getUnityAppId(context: Context): String {
        return context.resources.getString(R.string.ID_UnityGameID)
    }

    fun isShowUnityInt(): Boolean {
        return preferences.getBoolean(isShowUIntAdKey, true)
    }

    fun setShowUnityInt(isShow: Boolean): Boolean {
        val isChanges = isShow != isShowUnityInt()
        editor.putBoolean(isShowUIntAdKey, isShow).commit()
        return isChanges
    }

    fun isShowUnityRewardedAd(): Boolean {
        return preferences.getBoolean(isShowURewardedAdKey, true)
    }

    fun setShowUnityRewarded(isShow: Boolean): Boolean {
        val isChanges = isShow != isShowUnityRewardedAd()
        editor.putBoolean(isShowURewardedAdKey, isShow).commit()
        return isChanges
    }

    //endregion


    //region: Live Data

    fun stringLiveData(
        key: String
    ): SharedPreferenceLiveData<String> {
        return SharedPreferenceLiveData(key, getString(key))
    }

    fun intLiveData(
        key: String
    ): SharedPreferenceLiveData<Int> {
        return SharedPreferenceLiveData(key, getInteger(key))
    }

    fun longLiveData(
        key: String
    ): SharedPreferenceLiveData<Long> {
        return SharedPreferenceLiveData(key, getLong(key))
    }

    //endregion

}

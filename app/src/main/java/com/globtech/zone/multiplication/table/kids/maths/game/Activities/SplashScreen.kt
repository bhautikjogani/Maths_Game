package com.globtech.zone.multiplication.table.kids.maths.game.Activities

import GameModule.Base.BaseActivity
import GameModule.GamePreference
import GameModule.HandlerUtils.GameHandlerClass
import GameModule.PrefKey
import GameModule.ScreenUtil
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.multidex.BuildConfig
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivitySplashBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class SplashScreen : BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    private val handler = GameHandlerClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GamePreference.setInteger(PrefKey.PidID, android.os.Process.myPid())
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDefaultScreenSize()
        onUpdateVersion()
        initFirebase()

        binding.root.post {
            setScreesSize(
                width = binding.root.width, height = binding.root.height
            )
        }

        binding.frmPreSplash.alpha = 1f
        binding.frmSplash.visibility = View.VISIBLE
        binding.frmPreSplash.visibility = View.VISIBLE

        binding.frmPreSplash.animate().alpha(0f).setStartDelay(1400)
            .setDuration(600).withEndAction {
                handler.postDelayedClass({
                    goToHomeScreen()
                }, 1500)
            }.start()

        binding.frmText.animate().scaleX(.4f).scaleY(.4f).alpha(0f).setStartDelay(1200)
            .setDuration(600).start()

    }

    private fun initFirebase() {

        FirebaseApp.initializeApp(this)

        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)

        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)

//        val userID = "${android.os.Build.MODEL} - ${GamePreference.getString(PrefKey.userName)}"
//
//        Firebase.crashlytics.setUserId(userID)
//        Firebase.analytics.setUserId(userID)
//        Firebase.analytics.setUserProperty(
//            "coins", GamePreference.getLong(PrefKey.userCoins).toString()
//        )


    }

    private fun setDefaultScreenSize() {
        val display: Display =
            (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        setScreesSize(metrics.widthPixels, metrics.heightPixels)
    }

    private fun setScreesSize(width: Int, height: Int) {
        ScreenUtil.screenWidth = width
        ScreenUtil.screenHeight = height
        ScreenUtil.screenRatio = ScreenUtil.getPortraitScreenRatio()
        Log.d(
            TAG,
            "setScreesSize:  --->  width=$width  |  height=$height  |  ratio=${ScreenUtil.screenRatio}"
        )
        val loc = intArrayOf(0, 0)
        window.decorView.getLocationOnScreen(loc)
        ScreenUtil.notchSize = loc[1]
    }

    private fun onUpdateVersion() {
        if (GamePreference.getInteger(PrefKey.Version) != BuildConfig.VERSION_CODE) {
            GamePreference.setInteger(PrefKey.Version, BuildConfig.VERSION_CODE)
            GamePreference.setBoolean(PrefKey.saveGame, false)
//            if (GamePreference.getString(PrefKey.userName).isEmpty()) {
//                GamePreference.setString(PrefKey.userName, "YOU")
//            }
//            if (GamePreference.getString(PrefKey.userProfile).isEmpty()) {
//                val avatarList = ArrayList<String>()
//                var temp = 0
//                while (true) {
//                    temp = resources.getIdentifier(
//                        "u${avatarList.size + 1}", "drawable", packageName
//                    )
//                    if (temp == 0) break
//                    avatarList.add("u${avatarList.size + 1}")
//                }
//                GamePreference.setString(
//                    PrefKey.userProfile, avatarList.random()
//                )
//            }
        }
    }

    private fun goToHomeScreen() {
        handler.pause()
        handler.removeCallbacksAndMessages(null)
        startActivity(Intent(this@SplashScreen, HomeScreen::class.java))
        finish()
    }

}

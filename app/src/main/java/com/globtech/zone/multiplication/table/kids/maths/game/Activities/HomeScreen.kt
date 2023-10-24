package com.globtech.zone.multiplication.table.kids.maths.game.Activities

import GameModule.AdsUtiles.AdsManager
import GameModule.AdsUtiles.AppOpenManager
import GameModule.Base.BaseActivity
import GameModule.GamePreference
import GameModule.GameSound
import GameModule.PrefKey
import GameModule.Utils
import Utility.GameNotification
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.globtech.zone.multiplication.table.kids.maths.game.BuildConfig
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Conformation
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_RemoveAds
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Setting
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivityHomeScreenBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.Calendar

class HomeScreen : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GamePreference.addInteger(PrefKey.session, 1)
        if (GamePreference.getInteger(PrefKey.session) >= 2) {
            binding.root.post {
                setFirebasePushNotification()
            }
        }

        AdsManager().initialize(this@HomeScreen)
        setEnableAdsConfig()
        setNotification()
        Utils.showAskForRating(this@HomeScreen, resources.getString(R.string.RateUsMessage))
        GameSound().initialize(
            activity = this,
            clickSound = R.raw.click,
            backgroundMusics = arrayListOf(R.raw.bg),
            soundList = arrayListOf(R.raw.wrong_ans, R.raw.right_ans, R.raw.timer),
            tapSoundsList = arrayListOf()
        )

        GameSound.play()?.clickSound()

        binding.clickHandler = this

    }

    private fun setEnableAdsConfig() {

        if (BuildConfig.DEBUG) {
            GamePreference.setAdMobAppOpenAdID("")
            GamePreference.setAdMobBannerAdID("")
            GamePreference.setAdMobInterstitialAdID("")
            GamePreference.setAdMobRewardedAdID("")
            GamePreference.setAdMobRewardedIntAdID("")
            GamePreference.setIntInterval(1)
            GamePreference.setShowUnityInt(false)
            GamePreference.setShowUnityRewarded(false)

            GamePreference.setAdMobAppOpenAdID("ca-app-pub-3940256099942544/3419835294")
            GamePreference.setAdMobBannerAdID("ca-app-pub-3940256099942544/6300978111")
            GamePreference.setAdMobInterstitialAdID("ca-app-pub-3940256099942544/1033173712")
            GamePreference.setAdMobRewardedAdID("ca-app-pub-3940256099942544/5224354917")
            GamePreference.setAdMobRewardedIntAdID("ca-app-pub-3940256099942544/5354046379")
//            GamePreference.setIntInterval(1)

            AdsManager.show()?.loadAds()
            return
        }

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            Log.d(TAG, "setEnableAdsConfig:    isSuccessful   --->   ${task.isSuccessful}")
            if (task.isSuccessful) {

                if (GamePreference.setAdMobAppOpenAdID(remoteConfig.getString("admob_AppOpen")))
                    AppOpenManager.instance?.loadAppOpenAd()

                var isChanges = false

                if (GamePreference.setAdMobBannerAdID(remoteConfig.getString("admob_Banner")))
                    isChanges = true
                if (GamePreference.setAdMobInterstitialAdID(remoteConfig.getString("admob_Int")))
                    isChanges = true
                if (GamePreference.setAdMobRewardedAdID(remoteConfig.getString("admob_Rewarded")))
                    isChanges = true
                if (GamePreference.setAdMobRewardedIntAdID(remoteConfig.getString("admob_RewardInt")))
                    isChanges = true
                if (GamePreference.setIntInterval(remoteConfig.getLong("int_interval").toInt()))
                    isChanges = true

                /* OneSignal */
                GamePreference.setOneSignalId(remoteConfig.getString("oneSignalId"))

                if (isChanges) AdsManager.show()?.loadAds()

            }
        }
    }

    private fun setFirebasePushNotification() {
        askNotificationPermission()
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//        })
    }

    /* Firebase Push Notification */
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d(TAG, "requestPermissionLauncher:  isGranted = $isGranted")

//        if (isGranted.not() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            PopupAlert(this@HomeScreen)
//                .setTitle("Notification Permission")
//                .setMessage("Notification permission is required, Please allow notification permission from setting")
//                .setNegativeButton("No thanks", null)
//                .setPositiveButton("Ok", object : ActionEndCallback {
//                    override fun onEnd() {
//                        try {
//                            val settingsIntent: Intent =
//                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                                    .putExtra(
//                                        Settings.EXTRA_CHANNEL_ID,
//                                        getString(R.string.channel_id)
//                                    )
//                            startActivity(settingsIntent)
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                }).show()
//        }

    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.channel_id),
                "General Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "General Notifications send by system"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            Log.d(
                TAG,
                "askNotificationPermission:  areNotificationsEnabled = ${notificationManager.areNotificationsEnabled()}"
            )
            notificationManager.createNotificationChannel(channel)
        }

        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                Log.d(
                    TAG,
                    "askNotificationPermission:  -->  FCM SDK (and your app) can post notifications."
                )
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // display an educational UI explaining to the user the features that will be enabled
                // by them granting the POST_NOTIFICATION permission. This UI should provide the user
                // "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                // If the user selects "No thanks," allow the user to continue without notifications.
                Log.d(TAG, "askNotificationPermission:  --->= shouldShowRequestPermissionRationale")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                Log.d(TAG, "askNotificationPermission:   -->  Directly ask for the permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Notification
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setNotification() {
        val broadcast = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                this, 0, Intent(
                    this, GameNotification::class.java
                ), PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                this, 0, Intent(
                    this, GameNotification::class.java
                ), 0
            )
        }

        val updateTime9 = Calendar.getInstance()
        updateTime9.add(Calendar.DAY_OF_YEAR, 1)
        updateTime9[Calendar.HOUR] = 9
        updateTime9[Calendar.MINUTE] = 0
        updateTime9[Calendar.SECOND] = 0
        updateTime9[Calendar.AM_PM] = Calendar.AM
        val alarms9 = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarms9.setRepeating(
            AlarmManager.RTC, updateTime9.timeInMillis, AlarmManager.INTERVAL_DAY, broadcast
        )

        val updateTime4 = Calendar.getInstance()
        updateTime4.add(Calendar.DAY_OF_YEAR, 1)
        updateTime4[Calendar.HOUR] = 4
        updateTime4[Calendar.MINUTE] = 0
        updateTime4[Calendar.SECOND] = 0
        updateTime4[Calendar.AM_PM] = Calendar.PM
        val alarms4 = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarms4.setRepeating(
            AlarmManager.RTC, //RTC
            updateTime4.timeInMillis, AlarmManager.INTERVAL_DAY, broadcast
        )

    }

    private fun selection(gameType: GameType) {
        startActivity(
            Intent(this@HomeScreen, SelectionActivity::class.java)
//            Intent(this@HomeScreen, SelectionActivity_old::class.java)
                .putExtra("gameType", gameType)
        )
    }

    override fun onBackPressed() {
        Popup_Conformation(this@HomeScreen)
            .setDialogTitle("ALERT")
            .setDialogMsg("ARE YOU SURE?\nYOU WANT TO EXIT?")
            .setButtonLeft("EXIT") {
                GameSound.play()?.destroy()
                AdsManager.show()?.destroy()
                AppOpenManager.instance?.destroy()
                finishAffinity()
            }
            .setButtonRight("KEEP")
    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnSetting -> {
                Popup_Setting(this@HomeScreen, isFromPlaying = false)
            }

            binding.btnReport, binding.btnLanguage -> { //temporary replace language to report
                startActivity(Intent(this@HomeScreen, ReportActivity::class.java))
            }

            binding.btnRateUS -> {
                Utils.showRateUs(this@HomeScreen)
            }

            binding.btnShare -> {
                Utils.showShareGame(
                    activity = this@HomeScreen,
                    appName = getRString(R.string.app_name),
                    shareBody = getRString(R.string.shareBody)
                )
            }

            binding.btnRemoveAds -> {
                Popup_RemoveAds(this@HomeScreen)
            }

//            binding.btnLanguage -> {
//               Popup_Language(this@HomeScreen)
//            }

//            binding.btnMixedOperations -> selection(GameType.MixedOperations)
            binding.btnAddition -> selection(GameType.Addition)
            binding.btnSubtraction -> selection(GameType.Subtraction)
            binding.btnMultiplication -> selection(GameType.Multiplication)
            binding.btnDivision -> selection(GameType.Division)
            binding.btnSquareRoot -> selection(GameType.SquareRoot)
            binding.btnDecimal -> selection(GameType.Decimal)

        }

    }

}

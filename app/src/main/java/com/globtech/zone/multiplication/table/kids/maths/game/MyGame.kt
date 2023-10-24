package com.globtech.zone.multiplication.table.kids.maths.game

import GameModule.AdsUtiles.AppOpenManager
import GameModule.GamePreference
import GameModule.GameSound
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel

class MyGame : MultiDexApplication(), LifecycleObserver {

    companion object {
        val MainTAG: String = "_JB_"
    }

    private val TAG: String = MainTAG + javaClass.simpleName

    override fun onCreate() {
        super.onCreate()

        GamePreference.initialize(this)
        AppOpenManager(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        setupOneSignal()

    }

    private fun setupOneSignal() {
        if (GamePreference.getOneSignalId().isEmpty()) return
        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, GamePreference.getOneSignalId())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    /** LifecycleObserver methods  */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        GameSound.play()?.startMusic()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Log.d(TAG, "onPause: ")
        GameSound.play()?.pauseMusic()
    }

}

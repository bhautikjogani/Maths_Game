package com.globtech.zone.multiplication.table.kids.maths.game.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.globtech.zone.multiplication.table.kids.maths.game.R


class RoutingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen visible for this Activity
        splashScreen.setKeepOnScreenCondition { true }
        startSomeNextActivity()
        finish()
    }

    private fun startSomeNextActivity() {
        startActivity(Intent(this, SplashScreen::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}
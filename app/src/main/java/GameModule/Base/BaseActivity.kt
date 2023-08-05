package GameModule.Base

import GameModule.GamePreference
import GameModule.GameSound
import GameModule.PrefKey
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.SplashScreen
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame
import com.globtech.zone.multiplication.table.kids.maths.game.R

open class BaseActivity : AppCompatActivity()  {

    open val TAG: String = MyGame.MainTAG + this.javaClass.simpleName
    var mLastClickTime = 0L
    val mClickDelay = 800L

    val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { _ ->
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(checkAppWasKilled())return

        screen(window)

    }

    fun isDoubleClick(isClickSound: Boolean = true): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < mClickDelay) return true
        mLastClickTime = SystemClock.elapsedRealtime()
        if (isClickSound) GameSound.play()?.clickSound()
        return false
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        freeMemory()
    }

    private fun freeMemory() {
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }

    fun screen(window: Window) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        val decorView = window.decorView
        decorView.systemUiVisibility = flags
        decorView.setOnSystemUiVisibilityChangeListener {
            // if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            decorView.systemUiVisibility = flags
            // }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }

    fun checkAppWasKilled(): Boolean {
        if (GamePreference.getInteger(PrefKey.PidID) == 0 ||
            GamePreference.getInteger(PrefKey.PidID) == android.os.Process.myPid()
        ) return false

        Log.d(TAG, "CheckAppWasKilled: ------------------------>   ")
        finishAffinity()
        startActivity(Intent(this, SplashScreen::class.java))
        return true
    }

    override fun onBackPressed() {
        // do nothing...
//        finish()
    }

    fun transitionAnim() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        transitionAnim()
    }

    override fun finish() {
        super.finish()
        transitionAnim()
    }

    fun getRString(resInt: Int): String {
        return resources.getString(resInt).replace("\\n", "\n")
    }

    fun getLayoutId(idText: String): Int {
        return resources.getIdentifier(idText, "id", packageName)
    }

    fun getDrawableId(idText: String): Int {
        return resources.getIdentifier(idText, "drawable", packageName)
    }

}

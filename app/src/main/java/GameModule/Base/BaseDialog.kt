package GameModule.Base

import GameModule.GameSound
import GameModule.ScreenUtil
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.animation.addListener
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame
import com.globtech.zone.multiplication.table.kids.maths.game.R

open class BaseDialog (
    val activity: Activity, var layoutView: View
) : Dialog(activity, R.style.dialogTheme) {

    val TAG: String = MyGame.MainTAG + this.javaClass.simpleName
    var mLastClickTime = 0L
    val mClickDelay = (activity as BaseActivity).mClickDelay

    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(layoutView)
        this.setCancelable(false)
        window?.setDimAmount(0f)
//        window?.attributes?.windowAnimations = R.style.UpDownAnim
        screen(window!!)
    }

    override fun show() {
        if (!activity.isFinishing || !isShowing) {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            super.show()
            window?.decorView?.systemUiVisibility = activity.window.decorView.systemUiVisibility
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            dialogTransitionAnim()
        }
    }

    override fun dismiss() {
        ValueAnimator.ofObject(
            ArgbEvaluator(), activity.resources.getColor(R.color.black80), Color.TRANSPARENT
        ).apply {
            this.duration = 500
            this.addUpdateListener {
                if (it.animatedValue is Int) {
                    val color = it.animatedValue as Int
                    layoutView.setBackgroundColor(color)
                }
            }
            this.addListener(onEnd = { if (this@BaseDialog.isShowing) super.dismiss() })
            this.start()
        }

        layoutView.findViewById<ViewGroup>(R.id.frmDialog).apply {
            this.animate().translationY(ScreenUtil.screenHeight.toFloat())
                .setInterpolator(AnticipateInterpolator(1.5f))
                .setDuration(500).start()
        }

    }

    private fun dialogTransitionAnim() {
        ValueAnimator.ofObject(
            ArgbEvaluator(), Color.TRANSPARENT, activity.resources.getColor(R.color.black80)
        ).apply {
            this.duration = 500
//            this.startDelay = 500
            this.addUpdateListener {
                if (it.animatedValue is Int) {
                    val color = it.animatedValue as Int
                    layoutView.setBackgroundColor(color)
                }
            }
            this.start()
        }

        layoutView.findViewById<ViewGroup>(R.id.frmDialog).apply {
            this.translationY = ScreenUtil.screenHeight.toFloat()
            this.animate().translationY(0f)
                .setInterpolator(OvershootInterpolator(1.5f))
                .setDuration(500).start()
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window?.decorView?.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.STATUS_BAR_HIDDEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window?.attributes?.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            }
        }
    }

    private fun screen(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val flags =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.STATUS_BAR_HIDDEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            val decorView = window.decorView
            decorView.systemUiVisibility = flags
            decorView.setOnSystemUiVisibilityChangeListener {
                //if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.systemUiVisibility = flags
                //}
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }

    fun transitionAnim() {
        activity.overridePendingTransition(
            R.anim.fade_in, R.anim.fade_out
        )
    }

    fun isDoubleClick(isClickSound: Boolean = true): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < mClickDelay) return true
        mLastClickTime = SystemClock.elapsedRealtime()
        if (isClickSound) GameSound.play()?.clickSound()
        return false
    }

    fun showToast(s: String) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    fun getString(resourceID: Int): String {
        return activity.resources.getString(resourceID).replace("\\n", "\n")
    }

    fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }

    fun getResource(): Resources {
        return activity.resources
    }

    fun getLayoutId(idText: String): Int {
        return activity.resources.getIdentifier(idText, "id", activity.packageName)
    }

    fun getDrawableId(idText: String): Int {
        return getResource().getIdentifier(idText, "drawable", activity.packageName)
    }

}

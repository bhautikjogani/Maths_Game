package GameModule

import GameModule.Base.BaseDialog
import android.app.Activity
import android.os.Build
import android.os.SystemClock
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class PopupAlert(activity: Activity) : BaseDialog(
    activity,
    LayoutInflater.from(activity).inflate(R.layout.dialog_alert, null, false)
) {

    init {

        findViewById<MaterialButton>(R.id.btnNeutral).visibility = View.GONE
        findViewById<MaterialButton>(R.id.btnNegative).visibility = View.GONE
        findViewById<MaterialButton>(R.id.btnPositive).visibility = View.GONE
        findViewById<TextView>(R.id.tvMessage).visibility = View.GONE
        findViewById<TextView>(R.id.tvTitle).visibility = View.GONE
        findViewById<ImageView>(R.id.ivIcon).visibility = View.GONE

        setLayout()

    }

    private fun setLayout() {

        var clp: ConstraintLayout.LayoutParams

        findViewById<MaterialCardView>(R.id.frmDialog).apply {
            clp = this.layoutParams.get()
            clp.width = ScreenUtil.getSize(380)
            this.radius = ScreenUtil.getSize(5f)
            this.maxCardElevation = ScreenUtil.getSize(5f)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.elevation = ScreenUtil.getSize(5f)
            }
            this.cardElevation = ScreenUtil.getSize(5f)
        }

        findViewById<MaterialCardView>(R.id.frmDialog).setPadding(ScreenUtil.getSize(15))

        findViewById<TextView>(R.id.tvTitle).setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSize(24f)
        )

        findViewById<TextView>(R.id.tvMessage).setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSize(16f)
        )
        (findViewById<TextView>(R.id.tvMessage).layoutParams.get()).topMargin =
            ScreenUtil.getSize(15)

        findViewById<MaterialButton>(R.id.btnNeutral).setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSize(15f)
        )

        findViewById<MaterialButton>(R.id.btnNegative).setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSize(15f)
        )

        findViewById<MaterialButton>(R.id.btnPositive).setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSize(15f)
        )
        (findViewById<MaterialButton>(R.id.btnPositive).layoutParams.get()).topMargin =
            ScreenUtil.getSize(25)

        findViewById<ImageView>(R.id.ivIcon).apply {
            this.setPadding(ScreenUtil.getSize(5))
        }

    }

    fun setIcon(drawableId: Int): PopupAlert {
        findViewById<ImageView>(R.id.ivIcon).apply {
            this.visibility = View.VISIBLE
            this.setImageResource(drawableId)
        }
        return this
    }

    fun setTitle(title: String): PopupAlert {
        findViewById<TextView>(R.id.tvTitle).apply {
            this.text = title
            this.visibility = View.VISIBLE
        }
        return this
    }

    fun setMessage(msg: String): PopupAlert {
        findViewById<TextView>(R.id.tvMessage).apply {
            this.text = msg
            this.visibility = View.VISIBLE
        }
        return this
    }

    fun setNeutralButton(text: String, callback: ActionEndCallback?): PopupAlert {
        findViewById<TextView>(R.id.btnNeutral).apply {
            this.visibility = View.VISIBLE
            this.text = text
            this.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < mClickDelay) return@setOnClickListener
                mLastClickTime = SystemClock.elapsedRealtime()
                dismiss()
                callback?.onEnd()
            }
        }
        return this
    }

    fun setNegativeButton(text: String, callback: ActionEndCallback?): PopupAlert {
        findViewById<TextView>(R.id.btnNegative).apply {
            this.visibility = View.VISIBLE
            this.text = text
            this.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < mClickDelay) return@setOnClickListener
                mLastClickTime = SystemClock.elapsedRealtime()
                dismiss()
                callback?.onEnd()
            }
        }
        return this
    }

    fun setPositiveButton(text: String, callback: ActionEndCallback?): PopupAlert {
        findViewById<TextView>(R.id.btnPositive).apply {
            this.visibility = View.VISIBLE
            this.text = text
            this.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime < mClickDelay) return@setOnClickListener
                mLastClickTime = SystemClock.elapsedRealtime()
                dismiss()
                callback?.onEnd()
            }
        }
        return this
    }

}
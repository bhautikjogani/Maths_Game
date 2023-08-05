package GameModule.CustomView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import me.grantland.widget.AutofitTextView

@SuppressLint("CustomViewStyleable")
class MyConstrainLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val TAG = "_JB_" + javaClass.simpleName
    var isResponsive = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        super.setLayoutParams(params)
        if (!isResponsive) {
            allResponsive(this@MyConstrainLayout)
            Log.d(TAG, "setLayoutParams:   ------>   end ")
        }
        isResponsive = true
        Log.d(TAG, "setLayoutParams: ")
    }

    private fun allResponsive(viewGroup: ViewGroup) {
        viewGroup.responsive()

        for (i in 0 until viewGroup.childCount) {
            val it = viewGroup.getChildAt(i)

            if (it is AdapterView<*>) continue

            if (it is ViewGroup && it !is RecyclerView) {
                allResponsive(it)
            } else {
                when (it) {
                    is MaterialButton -> {
                        it.responsiveMaterialButton()
                    }
                    is AutofitTextView -> {
                        it.responsiveAutofitTextView()
                    }
                    is OutlinedTextView -> {
                        it.responsiveOutlinedTextView()
                    }
                    is TextView -> {
                        it.responsiveTextView()
                    }
                }
                it.responsive()
            }
        }

    }

}

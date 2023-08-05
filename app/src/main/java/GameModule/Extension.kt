package GameModule

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout


fun Long.format(isComaFormat: Boolean = false): String {
    return Utils.coinFormat(this, isComaFormat)
}

fun ViewGroup.LayoutParams.get(): ConstraintLayout.LayoutParams {
    return this as ConstraintLayout.LayoutParams
}

fun ViewGroup.LayoutParams.getLP(): LinearLayout.LayoutParams {
    return this as LinearLayout.LayoutParams
}

fun ViewGroup.LayoutParams.getFP(): FrameLayout.LayoutParams {
    return this as FrameLayout.LayoutParams
}

fun String.toIntArray(delimiters: String): IntArray {
    if (this.trim().isEmpty()) return intArrayOf()
    val array = this.split(delimiters)
    val intArray = IntArray(array.size) { 0 }
    array.forEachIndexed { index, s ->
//        Log.d("_JB_", "toIntArray:  ---->   ${s}")
        intArray[index] = s.trim().toInt()
    }
    return intArray
}

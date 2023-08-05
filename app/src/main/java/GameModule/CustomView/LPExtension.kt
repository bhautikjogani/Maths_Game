package GameModule.CustomView

import GameModule.ScreenUtil
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import me.grantland.widget.AutofitTextView

fun ViewGroup.LayoutParams.getMLP(): ViewGroup.MarginLayoutParams {
    return this as ViewGroup.MarginLayoutParams
}

fun View.responsive() {
    val lp = this.layoutParams
    if (lp.width <= 0) {
        this.minimumWidth = ScreenUtil.getSizeDensity(this.minimumWidth)
        this.minimumHeight = ScreenUtil.getSizeDensity(this.minimumHeight)
        lp.height = ScreenUtil.getSizeDensity(lp.height)
        lp.getMLP().bottomMargin = ScreenUtil.getMarginDensity(lp.getMLP().bottomMargin)
        lp.getMLP().leftMargin = ScreenUtil.getMarginDensity(lp.getMLP().leftMargin)
        lp.getMLP().topMargin = ScreenUtil.getMarginDensity(lp.getMLP().topMargin)
        lp.getMLP().rightMargin = ScreenUtil.getMarginDensity(lp.getMLP().rightMargin)
        this.setPadding(
            ScreenUtil.getMarginDensity(this.paddingLeft),
            ScreenUtil.getMarginDensity(this.paddingTop),
            ScreenUtil.getMarginDensity(this.paddingRight),
            ScreenUtil.getMarginDensity(this.paddingBottom)
        )
    } else {
        val w = ScreenUtil.getDP2(lp.width)
        lp.width = ScreenUtil.getSize(w)
        if (lp.height > 0) lp.height = lp.width * ScreenUtil.getDP2(lp.height) / w
        this.minimumWidth = lp.width * ScreenUtil.getDP(this.minimumWidth) / w
        this.minimumHeight = lp.width * ScreenUtil.getDP(this.minimumHeight) / w
        lp.getMLP().bottomMargin = lp.width * ScreenUtil.getDP(lp.getMLP().bottomMargin) / w
        lp.getMLP().leftMargin = lp.width * ScreenUtil.getDP(lp.getMLP().leftMargin) / w
        lp.getMLP().topMargin = lp.width * ScreenUtil.getDP(lp.getMLP().topMargin) / w
        lp.getMLP().rightMargin = lp.width * ScreenUtil.getDP(lp.getMLP().rightMargin) / w
        this.setPadding(
            lp.width * ScreenUtil.getDP(this.paddingLeft) / w,
            lp.width * ScreenUtil.getDP(this.paddingTop) / w,
            lp.width * ScreenUtil.getDP(this.paddingRight) / w,
            lp.width * ScreenUtil.getDP(this.paddingBottom) / w
        )
    }
}

fun TextView.responsiveTextView() {
    val lp = this.layoutParams
    if (lp.width <= 0) {
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSizeDensity(this.textSize)
        )
    } else {
        val w = ScreenUtil.getDP2(lp.width)
        val wNew = ScreenUtil.getSize(w)
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            wNew * ScreenUtil.getDP(this.textSize) / w
        )
    }
}

fun MaterialButton.responsiveMaterialButton() {
    val lp = this.layoutParams
    if (lp.width <= 0) {
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSizeDensity(this.textSize)
        )
        this.cornerRadius = ScreenUtil.getSizeDensity(this.cornerRadius)
    } else {
        val w = ScreenUtil.getDP2(lp.width)
        val wNew = ScreenUtil.getSize(w)
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            wNew * ScreenUtil.getDP(this.textSize) / w
        )
        this.cornerRadius = wNew * ScreenUtil.getDP(this.cornerRadius) / w
    }
}

fun OutlinedTextView.responsiveOutlinedTextView() {
    val lp = this.layoutParams
    if (lp.width <= 0) {
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSizeDensity(this.textSize)
        )
        this.setStrokeWidth(ScreenUtil.getSizeDensity(this.getStrokeWidth()))
    } else {
        val w = ScreenUtil.getDP2(lp.width)
        val wNew = ScreenUtil.getSize(w)
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            wNew * ScreenUtil.getDP(this.textSize) / w
        )
        this.setStrokeWidth(wNew * ScreenUtil.getDP(this.getStrokeWidth()) / w)
    }
}

fun AutofitTextView.responsiveAutofitTextView() {
    val lp = this.layoutParams
    if (lp.width <= 0) {
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSizeDensity(this.textSize)
        )
        this.setMinTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            ScreenUtil.getSizeDensity(this.textSize)
        )
    } else {
        val w = ScreenUtil.getDP2(lp.width)
        val wNew = ScreenUtil.getSize(w)
        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            wNew * ScreenUtil.getDP(this.textSize) / w
        )
        this.setMinTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            wNew * ScreenUtil.getDP(this.minTextSize) / w
        )
    }

}

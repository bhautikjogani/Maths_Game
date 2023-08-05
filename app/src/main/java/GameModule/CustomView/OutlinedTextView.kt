package GameModule.CustomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.globtech.zone.multiplication.table.kids.maths.game.R
import me.grantland.widget.AutofitHelper


class OutlinedTextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatTextView(context!!, attrs, defStyleAttr) {
    /**/
    private val strokePaint = Paint()
    private val disablePaint = Paint()
    var mHelper: AutofitHelper? = null
    private var strokeWidth = 0f

    init {
        init(this, attrs)
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        textSize = textSize
        if (mHelper != null) {
            mHelper!!.setTextSize(unit, size)
            mHelper!!.setMinTextSize(unit, size * .4f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawStrokeIfNeeded(this, canvas)
        super.onDraw(canvas)
        drawDisableIfNeeded(this, canvas)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        invalidate()
    }

    fun init(textView: TextView, attrs: AttributeSet?) {
        val array = textView.context.obtainStyledAttributes(attrs, R.styleable.OutlinedTextView)
        strokeWidth = array.getDimension(R.styleable.OutlinedTextView_strokeWidth, 0.0f)
        val strokeColor =
            array.getColor(R.styleable.OutlinedTextView_strokeColor, Color.TRANSPARENT)
        val isEnableAutoFitText =
            array.getBoolean(R.styleable.OutlinedTextView_isEnableAutoFitText, false)
        array.recycle()
        if (isEnableAutoFitText) {
            mHelper = AutofitHelper.create(this, attrs)
            isSingleLine = true
        }
        strokePaint.color = strokeColor
        strokePaint.textAlign = Paint.Align.LEFT
        strokePaint.textSize = textView.textSize
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth
        strokePaint.isAntiAlias = true
        strokePaint.typeface = textView.typeface
        disablePaint.color = resources.getColor(R.color.Checkcolor)
        disablePaint.textAlign = Paint.Align.LEFT
        disablePaint.textSize = textView.textSize
        disablePaint.style = Paint.Style.STROKE
        disablePaint.strokeWidth = strokeWidth
        disablePaint.isAntiAlias = true
        disablePaint.typeface = textView.typeface
        if (mHelper != null) {
            mHelper!!.textSize = textView.textSize
            mHelper!!.minTextSize = textView.textSize * .4f
        }
    }

    fun getStrokeWidth(): Float {
        return strokeWidth
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        strokePaint.strokeWidth = strokeWidth
        invalidate()
    }

    fun setStrokeColor(strokeColor: Int) {
        strokePaint.color = strokeColor
        invalidate()
    }

    override fun setTextSize(textSize: Float) {
        strokePaint.textSize = textSize
    }

    fun drawStrokeIfNeeded(textView: TextView, canvas: Canvas) {
        if (strokePaint.strokeWidth > 0 && strokePaint.color != Color.TRANSPARENT) {
            drawStroke(textView, canvas)
        }
    }

    fun drawDisableIfNeeded(textView: TextView, canvas: Canvas) {
        if (!isEnabled) {
            drawDisable(textView, canvas)
        }
    }

    private fun drawStroke(textView: TextView, canvas: Canvas) {
        strokePaint.typeface = textView.typeface
        strokePaint.letterSpacing = letterSpacing
        disablePaint.typeface = textView.typeface
        disablePaint.letterSpacing = letterSpacing
        val layout = textView.layout
        val lineCount = textView.lineCount
        val layoutHeight = layout.height
        val viewWidth = textView.width
        val viewHeight = textView.height
        val paddingLeft = textView.compoundPaddingLeft
        val paddingRight = textView.compoundPaddingRight
        val paddingTop = textView.compoundPaddingTop
        val paddingBottom = textView.compoundPaddingBottom
        val spaceHeight = Math.max(viewHeight - paddingTop - paddingBottom - layoutHeight, 0)
        val verticalGravity = getVerticalGravity(textView)
        val clipTop: Int
        val clipBottom: Int
        if (verticalGravity == Gravity.BOTTOM) {
            clipBottom =
                if (viewHeight > layoutHeight) viewHeight + paddingTop else layoutHeight + paddingTop
            val yLayoutStart = viewHeight - paddingBottom - layoutHeight
            clipTop =
                if (yLayoutStart > paddingTop) yLayoutStart else (paddingTop shl 1) - yLayoutStart
        } else {
            clipBottom = viewHeight - paddingBottom
            clipTop = paddingTop
        }
        if (clipTop > clipBottom) {
            return
        }
        if (paddingLeft > viewWidth - paddingRight) {
            return
        }
        canvas.save()
        canvas.clipRect(paddingLeft, clipTop, viewWidth - paddingRight, clipBottom)
        val originalText = textView.text.toString()
        val start = if (verticalGravity != Gravity.BOTTOM) 0 else lineCount - 1
        val add = if (verticalGravity != Gravity.BOTTOM) 1 else -1
        var i = start
        while (getLoopCondition(i, lineCount, verticalGravity)) {
            val text = originalText.substring(layout.getLineStart(i), layout.getLineEnd(i))
            val x = layout.getLineLeft(i).toInt() + paddingLeft
            var y = layout.getLineBaseline(i) + paddingTop
            when (verticalGravity) {
                Gravity.BOTTOM -> y += spaceHeight
                Gravity.CENTER_VERTICAL -> y += spaceHeight shr 1
                else -> {}
            }
            if (verticalGravity != Gravity.BOTTOM) {
                if (y - textView.lineHeight >= clipBottom) {
                    break
                }
            } else {
                if (y + textView.lineHeight <= clipTop) {
                    break
                }
            }
            canvas.drawText(text, x.toFloat(), y.toFloat(), strokePaint)
            if (!isEnabled) canvas.drawText(text, x.toFloat(), y.toFloat(), disablePaint)
            i += add
        }
        canvas.restore()
    }

    private fun drawDisable(textView: TextView, canvas: Canvas) {
        disablePaint.typeface = textView.typeface
        disablePaint.letterSpacing = letterSpacing
        val layout = textView.layout
        val lineCount = textView.lineCount
        val layoutHeight = layout.height
        val viewWidth = textView.width
        val viewHeight = textView.height
        val paddingLeft = textView.compoundPaddingLeft
        val paddingRight = textView.compoundPaddingRight
        val paddingTop = textView.compoundPaddingTop
        val paddingBottom = textView.compoundPaddingBottom
        val spaceHeight = Math.max(viewHeight - paddingTop - paddingBottom - layoutHeight, 0)
        val verticalGravity = getVerticalGravity(textView)
        val clipTop: Int
        val clipBottom: Int
        if (verticalGravity == Gravity.BOTTOM) {
            clipBottom =
                if (viewHeight > layoutHeight) viewHeight + paddingTop else layoutHeight + paddingTop
            val yLayoutStart = viewHeight - paddingBottom - layoutHeight
            clipTop =
                if (yLayoutStart > paddingTop) yLayoutStart else (paddingTop shl 1) - yLayoutStart
        } else {
            clipBottom = viewHeight - paddingBottom
            clipTop = paddingTop
        }
        if (clipTop > clipBottom) {
            return
        }
        if (paddingLeft > viewWidth - paddingRight) {
            return
        }
        canvas.save()
        canvas.clipRect(paddingLeft, clipTop, viewWidth - paddingRight, clipBottom)
        val originalText = textView.text.toString()
        val start = if (verticalGravity != Gravity.BOTTOM) 0 else lineCount - 1
        val add = if (verticalGravity != Gravity.BOTTOM) 1 else -1
        var i = start
        while (getLoopCondition(i, lineCount, verticalGravity)) {
            val text = originalText.substring(layout.getLineStart(i), layout.getLineEnd(i))
            val x = layout.getLineLeft(i).toInt() + paddingLeft
            var y = layout.getLineBaseline(i) + paddingTop
            when (verticalGravity) {
                Gravity.BOTTOM -> y += spaceHeight
                Gravity.CENTER_VERTICAL -> y += (spaceHeight shr 1)
                else -> {}
            }
            if (verticalGravity != Gravity.BOTTOM) {
                if (y - textView.lineHeight >= clipBottom) {
                    break
                }
            } else {
                if (y + textView.lineHeight <= clipTop) {
                    break
                }
            }
            if (!isEnabled) canvas.drawText(text, x.toFloat(), y.toFloat(), disablePaint)
            i += add
        }
        canvas.restore()
    }

    private fun getVerticalGravity(textView: TextView): Int {
        return if (textView.gravity and Gravity.TOP == Gravity.TOP) {
            Gravity.TOP
        } else if (textView.gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
            Gravity.BOTTOM
        } else {
            Gravity.CENTER_VERTICAL
        }
    }

    private fun getLoopCondition(idx: Int, count: Int, verticalGravity: Int): Boolean {
        return if (verticalGravity == Gravity.BOTTOM) {
            idx >= 0
        } else {
            idx < count
        }
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(" $text ", type)
    }
}
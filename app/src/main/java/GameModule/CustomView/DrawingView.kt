package GameModule.CustomView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var bgColor = Color.parseColor("#13573A")
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mPath: Path = Path()
    private var mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)
    private var clearPaint: Paint = Paint().apply {
//        this.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        this.isAntiAlias = true
        this.isDither = true
        this.color = bgColor
        this.style = Paint.Style.STROKE
        this.strokeJoin = Paint.Join.MITER
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = 30f
    }
    private var mPaint: Paint = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.color = Color.WHITE
        this.style = Paint.Style.STROKE
        this.strokeJoin = Paint.Join.MITER
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = 15f
    }
    private var currentPaint = mPaint

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
//        mCanvas?.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(bgColor)
        mBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mBitmapPaint)
            canvas.drawPath(mPath, currentPaint)
        }
    }

    private var mX = 0f
    private var mY: Float = 0f
    private val TOUCH_TOLERANCE = 4f

    private fun touch_start(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touch_move(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy: Float = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touch_up() {
        mPath.lineTo(mX, mY)
        // commit the path to our offscreen
        mCanvas?.drawPath(mPath, currentPaint)
        // kill this so we don't double draw
        mPath.reset()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touch_start(x, y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                touch_move(x, y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                touch_up()
                invalidate()
            }
        }
        return true
    }

    fun setSize(value: Float) {
        currentPaint.strokeWidth = value
        invalidate()
    }

    fun clear() {
        mPath.reset()
        invalidate()
    }

    fun setPaintDraw(isPaint: Boolean) {
        currentPaint = if (isPaint) mPaint else clearPaint
    }

    fun getPaintSize(): Float {
        return mPaint.strokeWidth
    }

    fun getClearSize(): Float {
        return clearPaint.strokeWidth
    }

    fun isEraseMode(): Boolean {
        return currentPaint == clearPaint
    }

}
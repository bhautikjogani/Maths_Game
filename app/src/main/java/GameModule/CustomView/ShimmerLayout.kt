package GameModule.CustomView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import com.globtech.zone.multiplication.table.kids.maths.game.R

class ShimmerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private var maskOffsetX = 0
    private var maskRect: Rect? = null
    private var gradientTexturePaint: Paint? = null
    private var maskAnimator: ValueAnimator? = null
    private var localMaskBitmap: Bitmap? = null
    private var maskBitmap: Bitmap? = null
    private var canvasForShimmerMask: Canvas? = null
    private var isAnimationReversed = false
    private var isAnimationStarted = false
    private var autoStart = false
    private var shimmerAnimationDuration = 0
    private var shimmerColor = 0
    private var shimmerAngle = 0
    private var maskWidth = 0f
    private var gradientCenterColorWidth = 0f
    private var startAnimationPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null
    override fun onDetachedFromWindow() {
        resetShimmering()
        super.onDetachedFromWindow()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (!isAnimationStarted || width <= 0 || height <= 0) {
            super.dispatchDraw(canvas)
        } else {
            dispatchDrawShimmer(canvas)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            if (autoStart) {
                startShimmerAnimation(startDelay, delay)
            }
        } else {
            stopShimmerAnimation()
        }
    }

    var delay: Long = 2000
    var startDelay: Long = 0

    init {
        setWillNotDraw(false)
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ShimmerLayout,
            0, 0
        )
        try {
            shimmerAngle =
                a.getInteger(R.styleable.ShimmerLayout_shimmer_angle, DEFAULT_ANGLE.toInt())
            shimmerAnimationDuration = a.getInteger(
                R.styleable.ShimmerLayout_shimmer_animation_duration,
                DEFAULT_ANIMATION_DURATION
            )
            shimmerColor =
                a.getColor(R.styleable.ShimmerLayout_shimmer_color, getColor(R.color.shimmer_color))
            autoStart = a.getBoolean(R.styleable.ShimmerLayout_shimmer_auto_start, false)
            maskWidth = a.getFloat(R.styleable.ShimmerLayout_shimmer_mask_width, 0.5f)
            gradientCenterColorWidth =
                a.getFloat(R.styleable.ShimmerLayout_shimmer_gradient_center_color_width, 0.1f)
            isAnimationReversed =
                a.getBoolean(R.styleable.ShimmerLayout_shimmer_reverse_animation, false)
        } finally {
            a.recycle()
        }
        setMaskWidth(maskWidth)
        setGradientCenterColorWidth(gradientCenterColorWidth)
        setShimmerAngle(shimmerAngle)
        if (autoStart && visibility == VISIBLE) {
            startShimmerAnimation(startDelay, delay)
        }
    }

    fun startShimmerAnimation(startDelay: Long, delay: Long) {
        if (isAnimationStarted) {
            return
        }
        this.startDelay = startDelay
        this.delay = delay
        stopShimmerAnimation()
        if (width == 0) {
            startAnimationPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    startShimmerAnimation(startDelay, delay)
                    return true
                }
            }
            viewTreeObserver.addOnPreDrawListener(startAnimationPreDrawListener)
            return
        }
        val animator = shimmerAnimation
        animator!!.start()
        isAnimationStarted = true
    }

    fun stopShimmerAnimation() {
        if (startAnimationPreDrawListener != null) {
            viewTreeObserver.removeOnPreDrawListener(startAnimationPreDrawListener)
        }
        resetShimmering()
    }

    fun setShimmerColor(shimmerColor: Int) {
        this.shimmerColor = shimmerColor
        resetIfStarted()
    }

    fun setShimmerAnimationDuration(durationMillis: Int) {
        shimmerAnimationDuration = durationMillis
        resetIfStarted()
    }

    fun setAnimationReversed(animationReversed: Boolean) {
        isAnimationReversed = animationReversed
        resetIfStarted()
    }

    /**
     * Set the angle of the shimmer effect in clockwise direction in degrees.
     * The angle must be between {@value #MIN_ANGLE_VALUE} and {@value #MAX_ANGLE_VALUE}.
     *
     * @param angle The angle to be set
     */
    fun setShimmerAngle(angle: Int) {
        require(!(angle < MIN_ANGLE_VALUE || MAX_ANGLE_VALUE < angle)) {
            String.format(
                "shimmerAngle value must be between %d and %d",
                MIN_ANGLE_VALUE,
                MAX_ANGLE_VALUE
            )
        }
        shimmerAngle = angle
        resetIfStarted()
    }

    /**
     * Sets the width of the shimmer line to a value higher than 0 to less or equal to 1.
     * 1 means the width of the shimmer line is equal to half of the width of the ShimmerLayout.
     * The default value is 0.5.
     *
     * @param maskWidth The width of the shimmer line.
     */
    fun setMaskWidth(maskWidth: Float) {
        require(!(maskWidth <= MIN_MASK_WIDTH_VALUE || MAX_MASK_WIDTH_VALUE < maskWidth)) {
            String.format(
                "maskWidth value must be higher than %d and less or equal to %d",
                MIN_MASK_WIDTH_VALUE,
                MAX_MASK_WIDTH_VALUE
            )
        }
        this.maskWidth = maskWidth
        resetIfStarted()
    }

    /**
     * Sets the width of the center gradient color to a value higher than 0 to less than 1.
     * 0.99 means that the whole shimmer line will have this color with a little transparent edges.
     * The default value is 0.1.
     *
     * @param gradientCenterColorWidth The width of the center gradient color.
     */
    fun setGradientCenterColorWidth(gradientCenterColorWidth: Float) {
        require(
            !(gradientCenterColorWidth <= MIN_GRADIENT_CENTER_COLOR_WIDTH_VALUE
                    || MAX_GRADIENT_CENTER_COLOR_WIDTH_VALUE <= gradientCenterColorWidth)
        ) {
            String.format(
                "gradientCenterColorWidth value must be higher than %d and less than %d",
                MIN_GRADIENT_CENTER_COLOR_WIDTH_VALUE,
                MAX_GRADIENT_CENTER_COLOR_WIDTH_VALUE
            )
        }
        this.gradientCenterColorWidth = gradientCenterColorWidth
        resetIfStarted()
    }

    private fun resetIfStarted() {
        if (isAnimationStarted) {
            resetShimmering()
            startShimmerAnimation(startDelay, delay)
        }
    }

    private fun dispatchDrawShimmer(canvas: Canvas) {
        super.dispatchDraw(canvas)
        localMaskBitmap = getMaskBitmap()
        if (localMaskBitmap == null) {
            return
        }
        if (canvasForShimmerMask == null) {
            canvasForShimmerMask = Canvas(localMaskBitmap!!)
        }
        canvasForShimmerMask!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvasForShimmerMask!!.save()
        canvasForShimmerMask!!.translate(-maskOffsetX.toFloat(), 0f)
        super.dispatchDraw(canvasForShimmerMask)
        canvasForShimmerMask!!.restore()
        drawShimmer(canvas)
        localMaskBitmap = null
    }

    private fun drawShimmer(destinationCanvas: Canvas) {
        createShimmerPaint()
        destinationCanvas.save()
        destinationCanvas.translate(maskOffsetX.toFloat(), 0f)
        destinationCanvas.drawRect(
            maskRect!!.left.toFloat(),
            0f,
            maskRect!!.width().toFloat(),
            maskRect!!.height().toFloat(),
            gradientTexturePaint!!
        )
        destinationCanvas.restore()
    }

    private fun resetShimmering() {
        if (maskAnimator != null) {
            maskAnimator!!.end()
            maskAnimator!!.removeAllUpdateListeners()
            maskAnimator!!.removeAllListeners()
        }
        maskAnimator = null
        gradientTexturePaint = null
        isAnimationStarted = false
        releaseBitMaps()
    }

    private fun releaseBitMaps() {
        canvasForShimmerMask = null
        if (maskBitmap != null) {
            maskBitmap!!.recycle()
            maskBitmap = null
        }
    }

    private fun getMaskBitmap(): Bitmap? {
        if (maskBitmap == null) {
            maskBitmap = createBitmap(maskRect!!.width(), height)
        }
        return maskBitmap
    }

    private fun createShimmerPaint() {
        if (gradientTexturePaint != null) {
            return
        }
        val edgeColor = reduceColorAlphaValueToZero(shimmerColor)
        val shimmerLineWidth = width / 2f * maskWidth
        val yPosition = (if (0 <= shimmerAngle) height else 0).toFloat()
        val gradient = LinearGradient(
            0f,
            yPosition,
            Math.cos(Math.toRadians(shimmerAngle.toDouble())).toFloat() * shimmerLineWidth,
            yPosition + Math.sin(Math.toRadians(shimmerAngle.toDouble()))
                .toFloat() * shimmerLineWidth,
            intArrayOf(edgeColor, shimmerColor, shimmerColor, edgeColor),
            gradientColorDistribution,
            Shader.TileMode.CLAMP
        )
        val maskBitmapShader =
            BitmapShader(localMaskBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val composeShader = ComposeShader(gradient, maskBitmapShader, PorterDuff.Mode.DST_IN)
        gradientTexturePaint = Paint()
        gradientTexturePaint!!.isAntiAlias = true
        gradientTexturePaint!!.isDither = true
        gradientTexturePaint!!.isFilterBitmap = true
        gradientTexturePaint!!.shader = composeShader
    }

    //        maskAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    private val shimmerAnimation: Animator?
        private get() {
            if (maskAnimator != null) {
                return maskAnimator
            }
            if (maskRect == null) {
                maskRect = calculateBitmapMaskRect()
            }
            val animationToX = width
            val animationFromX: Int
            animationFromX = if (width > maskRect!!.width()) {
                -animationToX
            } else {
                -maskRect!!.width()
            }
            val shimmerBitmapWidth = maskRect!!.width()
            val shimmerAnimationFullLength = animationToX - animationFromX
            maskAnimator = if (isAnimationReversed) ValueAnimator.ofInt(
                shimmerAnimationFullLength,
                0
            ) else ValueAnimator.ofInt(0, shimmerAnimationFullLength)
            maskAnimator?.setStartDelay(startDelay)
            maskAnimator?.setDuration(shimmerAnimationDuration.toLong())
            //        maskAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            maskOffsetX = animationFromX
            if (maskOffsetX + shimmerBitmapWidth >= 0) {
                invalidate()
            }
            maskAnimator?.addUpdateListener(AnimatorUpdateListener { animation ->
                maskOffsetX = animationFromX + animation.animatedValue as Int
                if (maskOffsetX + shimmerBitmapWidth >= 0) {
                    invalidate()
                }
            })
            maskAnimator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (maskAnimator == null) return
                    if (!isAnimationStarted) return
                    maskAnimator!!.startDelay = delay
                    maskAnimator!!.start()
                }
            })
            return maskAnimator
        }

    private fun createBitmap(width: Int, height: Int): Bitmap? {
        return try {
            Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        } catch (e: OutOfMemoryError) {
            System.gc()
            null
        }
    }

    private fun getColor(id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(id)
        } else {
            resources.getColor(id)
        }
    }

    private fun reduceColorAlphaValueToZero(actualColor: Int): Int {
        return Color.argb(
            0,
            Color.red(actualColor),
            Color.green(actualColor),
            Color.blue(actualColor)
        )
    }

    private fun calculateBitmapMaskRect(): Rect {
        return Rect(0, 0, calculateMaskWidth(), height)
    }

    private fun calculateMaskWidth(): Int {
        val shimmerLineBottomWidth =
            width / 2 * maskWidth / Math.cos(Math.toRadians(Math.abs(shimmerAngle).toDouble()))
        val shimmerLineRemainingTopWidth =
            height * Math.tan(Math.toRadians(Math.abs(shimmerAngle).toDouble()))
        return (shimmerLineBottomWidth + shimmerLineRemainingTopWidth).toInt()
    }

    private val gradientColorDistribution: FloatArray
        private get() {
            val colorDistribution = FloatArray(4)
            colorDistribution[0] = 0f
            colorDistribution[3] = 1f
            colorDistribution[1] = 0.5f - gradientCenterColorWidth / 2f
            colorDistribution[2] = 0.5f + gradientCenterColorWidth / 2f
            return colorDistribution
        }

    fun pause() {
        try {
            if (maskAnimator != null && maskAnimator!!.isRunning) maskAnimator!!.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resume() {
        try {
            if (maskAnimator != null) maskAnimator!!.resume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val DEFAULT_ANIMATION_DURATION = 1500
        private const val DEFAULT_ANGLE: Byte = 20
        private const val MIN_ANGLE_VALUE: Byte = -45
        private const val MAX_ANGLE_VALUE: Byte = 45
        private const val MIN_MASK_WIDTH_VALUE: Byte = 0
        private const val MAX_MASK_WIDTH_VALUE: Byte = 1
        private const val MIN_GRADIENT_CENTER_COLOR_WIDTH_VALUE: Byte = 0
        private const val MAX_GRADIENT_CENTER_COLOR_WIDTH_VALUE: Byte = 1
    }
}
package com.udacity.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import com.udacity.R
import com.udacity.models.ButtonState
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var customBackgroundColor = 0
    private var customDownloadColor = 0
    private var customTextColor = 0

    private var buttonPaint = Paint()
    private var downloadPaint = Paint()
    private var textPaint = Paint()
    private var arcPaint = Paint()

    private val rectWidth = resources.getDimension(R.dimen.rectWidth)
    private val rectHeight = resources.getDimension(R.dimen.rectHeight)

    private val clippingRectangle = RectF(0f, 0f, rectWidth, rectHeight)
    private val loadingRectangle = RectF(0f, 0f, 0f, rectHeight)
    private var loadingArc =
        RectF(rectWidth - 150f, (rectHeight / 2) - 30f, rectWidth - 90f, (rectHeight / 2) + 30f)

    private var downloadPercentage = 0f
    private var buttonText = context.getString(R.string.button_name)

    private lateinit var animator: ValueAnimator

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonText = context.getString(R.string.button_loading)
                textPaint.apply {
                    color = Color.WHITE
                }
                loadingAnimation(20000)
            }
            ButtonState.Loading -> {
                animator.cancel()
                loadingAnimation(2500, true)
            }
            ButtonState.Completed -> {
                downloadPercentage = 0f
                buttonText = context.getString(R.string.button_name)
                isEnabled = true
            }
        }
    }

    private fun loadingAnimation(
        customDuration: Long,
        isCompleted: Boolean = false
    ) {
        animator =
            ValueAnimator.ofFloat(downloadPercentage, rectWidth)
                .apply {
                    addUpdateListener {
                        downloadPercentage = animatedValue as Float
                        invalidate()
                    }
                    doOnEnd {
                        if (isCompleted) {
                            buttonState = ButtonState.Completed
                        }
                    }
                    duration = customDuration
                    isEnabled = false
                    start()
                }
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            customBackgroundColor = getColor(R.styleable.LoadingButton_customBackgroundColor, 0)
            customDownloadColor = getColor(R.styleable.LoadingButton_customDownloadColor, 0)
            customTextColor = getColor(R.styleable.LoadingButton_customTextColor, 0)
        }
        buttonPaint = Paint().apply {
            isAntiAlias = true
            color = customBackgroundColor
        }
        downloadPaint = Paint().apply {
            isAntiAlias = true
            color = customDownloadColor
        }
        textPaint = Paint().apply {
            isAntiAlias = true
            color = customTextColor
            textAlign = Paint.Align.CENTER
            textSize = resources.getDimension(R.dimen.buttonTextSize)
        }
        arcPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val path = Path().apply {
            addRoundRect(clippingRectangle, 10f, 10f, Path.Direction.CCW)
        }
        canvas?.apply {
            clipPath(path)
            drawColor(customBackgroundColor)

            loadingRectangle.right = downloadPercentage
            drawRoundRect(loadingRectangle, 10f, 10f, downloadPaint)

            val angle = downloadPercentage * 360f / rectWidth
            drawArc(loadingArc, 0f, angle, true, arcPaint)

            drawText(buttonText, rectWidth / 2, (rectHeight / 2) + 20, textPaint)

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun stateClicked() {
        buttonState = ButtonState.Clicked
    }

    fun stateDownloaded() {
        buttonState = ButtonState.Loading
    }
}
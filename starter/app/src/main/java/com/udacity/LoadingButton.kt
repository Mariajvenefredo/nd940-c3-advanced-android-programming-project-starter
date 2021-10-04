package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
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

    private val rectWidth = resources.getDimension(R.dimen.rectWidth)
    private val rectHeight = resources.getDimension(R.dimen.rectHeight)

    private val clippingRectangle = RectF(0f, 0f, rectWidth, rectHeight)
    private val loadingRectangle = RectF(0f, 0f, 0f, rectHeight)

    private val valueAnimator = ValueAnimator.ofFloat(0f, rectWidth)
    private var downloadPercentage = 0f
    private var buttonText = context.getString(R.string.button_name)

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
                loadingAnimation(2500)
            }
            ButtonState.Completed -> {

            }
        }
    }

    private fun loadingAnimation(customDuration: Long) {
        valueAnimator.apply {
            addUpdateListener {
                downloadPercentage = animatedValue as Float
                invalidate()
            }
            duration = customDuration
            start()
            isEnabled = false
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
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val path = Path().apply {
            addRoundRect(clippingRectangle, 10f, 10f, Path.Direction.CCW)
        }
        canvas?.clipPath(path)
        canvas?.drawColor(customBackgroundColor)

        loadingRectangle.right = downloadPercentage
        canvas?.drawRoundRect(loadingRectangle, 10f, 10f, downloadPaint)
        canvas?.drawText(buttonText, rectWidth / 2, (rectHeight / 2) + 20, textPaint)
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
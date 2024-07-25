package tech.unfaehig_industries.tooz.araction.tile_menu

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.view.View
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.*
import java.time.Instant

class TileButton : View {

    private lateinit var positionRect: RectF
    val baseX: Float get() = positionRect.left
    val baseY: Float get() = positionRect.top
    private lateinit var boundingRect: RectF
    private val rectInsetHighlight: Float = 5f
    var label: String = ""
    private var labelCoordinates: Pair<Float, Float> = Pair(0f, 0f)
    private val labelSize: Float =25f
    var children: ArrayList<TileButton> = ArrayList()
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var hoverJob: Job
    private lateinit var callback: () -> Unit

    constructor(context: Context) : super(context)

    constructor(context: Context, _positionRect: RectF, _boundingRect: RectF, _label: String, _callback: () -> Unit, _children: ArrayList<TileButton>, fillColor: Int, labelColor: Int) : super(context) {
        boundingRect = RectF(_boundingRect)
        left = boundingRect.left.toInt()
        right = boundingRect.right.toInt()
        top = boundingRect.top.toInt()
        bottom = boundingRect.bottom.toInt()

        positionRect = RectF(_positionRect)
        translationX = baseX
        translationY = baseY

        label = _label
        callback = _callback

        children = _children

        fillPaint.apply { color= fillColor; style= Paint.Style.FILL }
        this.labelCoordinates = Pair(boundingRect.left+10f, boundingRect.centerY()+(labelSize/2))
        val labelTypeface: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        labelPaint.apply { color= labelColor; typeface= labelTypeface; textSize= labelSize }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.run {
            this.drawRect(boundingRect, fillPaint)
            this.drawText(label, labelCoordinates.first, labelCoordinates.second, labelPaint)
        }
    }

    override fun onHoverChanged(hovered: Boolean) {
        super.onHoverChanged(hovered)

        if (hovered) {
            animateHover()
        }
        else {
            cancelHover()
        }
    }

    private fun animateHover(durationInSeconds: Long = 2L) {
        boundingRect.inset(-rectInsetHighlight, -rectInsetHighlight)

        @OptIn(DelicateCoroutinesApi::class)
        hoverJob = GlobalScope.launch {
            val endTime = Instant.now().plusSeconds(durationInSeconds)
            val delay = 100L
            var percent = 0f
            // Calculate steps from 60%, because the full width of the boundingRect is used, but the RadialGradient fills the button at about 60% already
            // This ensures that the button is full, at about the same time as the while loop has ended
            val step: Float = 0.6f / ((durationInSeconds * 1000) / delay)

            while (Instant.now().isBefore(endTime)) {
                fillPaint.shader = RadialGradient(boundingRect.centerX(), boundingRect.centerY(), boundingRect.width(), intArrayOf(ColorUtils.blendARGB(fillPaint.color, Color.BLACK, 0.6f), fillPaint.color), floatArrayOf((percent+step).coerceAtLeast(0f), percent), Shader.TileMode.CLAMP)
                invalidate()
                percent += step
                delay(delay)
            }

            if(isHovered) {
                callback()
            }
        }
    }

    private fun cancelHover() {
        hoverJob.cancel("hover leave")

        boundingRect.inset(rectInsetHighlight, rectInsetHighlight)
        fillPaint.shader = null
        invalidate()
    }

    fun isInCenter(screen: RectF): Boolean {
        return this.translationX <= screen.centerX() &&
                this.translationX + this.boundingRect.width() >= screen.centerX() &&
                this.translationY <= screen.centerY() &&
                this.translationY + this.boundingRect.height() >= screen.centerY()
    }
}
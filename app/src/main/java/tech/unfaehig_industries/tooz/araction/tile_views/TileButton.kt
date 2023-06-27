package tech.unfaehig_industries.tooz.araction.tile_views

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
    private val labelSize: Float = 60f
    var children: ArrayList<TileButton> = ArrayList()
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var hoverJob: Job

    constructor(context: Context) : super(context)

    constructor(context: Context, positionRect: RectF, boundingRect: RectF, label: String, children: ArrayList<TileButton>, fillColor: Int, labelColor: Int) : super(context) {
        this.boundingRect = RectF(boundingRect)
        this.left = boundingRect.left.toInt()
        this.right = boundingRect.right.toInt()
        this.top = boundingRect.top.toInt()
        this.bottom = boundingRect.bottom.toInt()

        this.positionRect = RectF(positionRect)
        this.translationX = baseX
        this.translationY = baseY

        this.label = label

        this.children = children

        this.fillPaint.apply { color= fillColor; style= Paint.Style.FILL }
        val labelTypeface: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        this.labelPaint.apply { color= labelColor; typeface= labelTypeface; textSize= labelSize }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            this.drawRect(boundingRect, fillPaint)
            this.drawText(label, boundingRect.centerX(), boundingRect.centerY(), labelPaint)
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

    private fun animateHover(durationInSeconds: Long = 3L) {
        boundingRect.inset(-rectInsetHighlight, -rectInsetHighlight)

        @OptIn(DelicateCoroutinesApi::class)
        hoverJob = GlobalScope.launch {
            val startTime = Instant.now().plusSeconds(durationInSeconds)
            val delay = 100L
            var percent = 0f
            val step: Float = (1f / durationInSeconds) / (1000 / delay)

            while (Instant.now().isBefore(startTime)) {
                fillPaint.shader = RadialGradient(boundingRect.centerX(), boundingRect.centerY(), boundingRect.width(), intArrayOf(ColorUtils.blendARGB(fillPaint.color, Color.BLACK, 0.6f), fillPaint.color), floatArrayOf(percent, (percent+0.1f).coerceAtMost(1f)), Shader.TileMode.CLAMP)
                invalidate()
                percent += step
                delay(delay)
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
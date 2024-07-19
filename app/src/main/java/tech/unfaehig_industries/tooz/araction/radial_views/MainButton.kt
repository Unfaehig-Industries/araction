package tech.unfaehig_industries.tooz.araction.radial_views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.*
import java.time.Instant

class MainButton : RadialMenuButton {

    private var radialBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var radius: Float = 5f
    private val radiusHighlight: Float = 1f
    private var fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var label: String = ""
    private val labelSize: Float = 50f
    private var labelCoordinates: Pair<Float, Float> = Pair(0f, 0f)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var hoverJob: Job
    private lateinit var callback: () -> Unit

    constructor(context: Context) : super(context)

    constructor(context: Context, radialBoundingRect: RectF, radius: Float, fillColor: Int, label: String, _callback: () -> Unit) : super(context) {
        this.radialBoundingRect = radialBoundingRect
        this.radius = radius
        this.fillPaint.apply { color= fillColor; style= Paint.Style.FILL }
        this.label = label
        this.labelCoordinates = Pair((radialBoundingRect.centerX()-(labelSize/2)-5f), (radialBoundingRect.centerY()+(labelSize/2)-5f))
        val labelTypeface: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        this.labelPaint.apply { color= Color.BLACK ; typeface= labelTypeface; textSize= labelSize }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.run {
            this.drawCircle(radialBoundingRect.centerX(), radialBoundingRect.centerY(), radius, fillPaint)
            this.drawText(label, labelCoordinates.first, labelCoordinates.second, labelPaint)
        }
    }

    override fun animateHover(durationInSeconds: Long) {
        radius += radiusHighlight

        @OptIn(DelicateCoroutinesApi::class)
        hoverJob = GlobalScope.launch {
            val startTime = Instant.now().plusSeconds(durationInSeconds)
            val delay = 100L
            var innerPercent = 0f
            val step: Float = (1f / durationInSeconds) / (1000 / delay)

            while (Instant.now().isBefore(startTime)) {
                fillPaint.shader = RadialGradient(radialBoundingRect.centerX(), radialBoundingRect.centerY(), radius, intArrayOf(ColorUtils.blendARGB(fillPaint.color, Color.BLACK, 0.6f), fillPaint.color), floatArrayOf(innerPercent, (innerPercent+0.1f).coerceAtMost(1f) ), Shader.TileMode.CLAMP)
                invalidate()
                innerPercent += step
                delay(delay)
            }
        }
    }

    override fun cancelHover() {
        hoverJob.cancel("hover leave")
        radius -= radiusHighlight
        fillPaint.shader = null
        invalidate()
    }
}
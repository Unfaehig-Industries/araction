package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.*
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.*
import java.time.Instant

class MainButton : RadialMenuButton {

    private var radialBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var radius: Float = 5f
    private val radiusHighlight: Float = 5f
    private var fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var hoverJob: Job

    constructor(context: Context) : super(context)

    constructor(context: Context, radialBoundingRect: RectF, radius: Float, fillColor: Int) : super(context) {
        this.radialBoundingRect = radialBoundingRect
        this.radius = radius
        this.fillPaint.apply { color= fillColor; style= Paint.Style.FILL }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            this.drawCircle(radialBoundingRect.centerX(), radialBoundingRect.centerY(), radius, fillPaint)
        }
    }

    override fun animateHover(durationInSeconds: Long) {
        radius += radiusHighlight

        @OptIn(DelicateCoroutinesApi::class)
        hoverJob = GlobalScope.launch {
            val startTime = Instant.now().plusSeconds(durationInSeconds)
            val delay: Long = 100L
            var percent: Float = 0f
            val step: Float = (1f / durationInSeconds) / (1000 / delay)

            while (Instant.now().isBefore(startTime)) {
                fillPaint.shader = RadialGradient(radialBoundingRect.centerX(), radialBoundingRect.centerY(), radius, intArrayOf(ColorUtils.blendARGB(fillPaint.color, Color.BLACK, 0.7f), fillPaint.color), floatArrayOf(percent, 1f), Shader.TileMode.CLAMP)
                invalidate()
                percent += step
                delay(delay)
            }
        }
    }

    override fun cancelHover() {
        hoverJob.cancel("hover leave")
        radius -= radiusHighlight
        fillPaint.shader = null
    }
}
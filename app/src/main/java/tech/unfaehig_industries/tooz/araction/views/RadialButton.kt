package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class RadialButton : View {

    private var radialBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var radialInnerBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var startDegrees: Float = 0f
    private var lengthDegrees: Float = 90f
    private var label: String = ""
    private var labelCoordinates: Pair<Float, Float> = Pair(0f, 0f)
    private val labelSize: Float = 60f
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, radialBoundingRect: RectF, radialInnerBoundingRect: RectF, start_degrees: Float, length_degrees: Float, label: String, fillColor: Int, background: Paint) : super(context) {
        this.radialBoundingRect = radialBoundingRect
        this.radialInnerBoundingRect = radialInnerBoundingRect
        this.startDegrees = start_degrees
        this.lengthDegrees = length_degrees

        this.label = label
        this.labelCoordinates = calculateLabelCoordinates()

        this.fillPaint.apply { color= fillColor; style= Paint.Style.FILL }
        this.backgroundPaint.apply { color= background.color }
        val labelTypeface: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        this.labelPaint.apply { color= background.color ; typeface= labelTypeface; textSize= labelSize }
    }

    private fun calculateLabelCoordinates(): Pair<Float, Float> {
        val radius: Float = ( radialInnerBoundingRect.width() + ( ( radialBoundingRect.width() - radialInnerBoundingRect.width() ) / 2 ) ) / 2
        val angleInDegrees: Float = startDegrees + ( lengthDegrees / 2 )
        val angleInRadians: Double = angleInDegrees * ( Math.PI / 180 )

        var x: Double = radius * cos(angleInRadians)
        var y: Double = radius * sin(angleInRadians)
        x += radialBoundingRect.centerX()
        y += radialBoundingRect.centerY()
        x -= labelSize / 4
        y += labelSize / 4

        return Pair(x.toFloat(), y.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            this.drawArc(radialBoundingRect, startDegrees, lengthDegrees, true, fillPaint)
            this.drawArc(radialInnerBoundingRect, startDegrees, lengthDegrees, true, backgroundPaint)
            this.drawText(label, labelCoordinates.first, labelCoordinates.second, labelPaint)
        }
    }
}
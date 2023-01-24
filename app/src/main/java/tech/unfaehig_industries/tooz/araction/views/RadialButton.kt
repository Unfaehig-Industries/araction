package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class RadialButton : View {

    private var radialBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var radialInnerBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var startDegrees: Float = 0f
    private var lengthDegrees: Float = 90f
    private var fill: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var background: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, radialBoundingRect: RectF, radialInnerBoundingRect: RectF, start_degrees: Float, length_degrees: Float, fillColor: Int, background: Paint) : super(context) {
        this.radialBoundingRect = radialBoundingRect
        this.radialInnerBoundingRect = radialInnerBoundingRect
        this.startDegrees = start_degrees
        this.lengthDegrees = length_degrees
        this.fill.apply { color= fillColor; style= Paint.Style.FILL }
        this.background = background
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            this.drawArc(radialBoundingRect, startDegrees, lengthDegrees, true, fill)
            this.drawArc(radialInnerBoundingRect, startDegrees, lengthDegrees, true, background)
        }
    }
}
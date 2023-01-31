package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.*
import android.view.View

class MainButton : View {

    private var radialBoundingRect: RectF = RectF(0f,0f,100f,100f)
    var radius: Float = 5f
    var fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val radiusHighlight: Float = 5f

    constructor(context: Context) : super(context) {
    }

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

    fun onHover(percent: Int) {
        radius += radiusHighlight

        val gradientRadius: Float = radius * percent
        fillPaint.shader = RadialGradient(radialBoundingRect.centerX(), radialBoundingRect.centerY(), gradientRadius, Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP)
    }

    fun onHoverLeave() {
        radius -= radiusHighlight
        fillPaint.shader = null
    }
}
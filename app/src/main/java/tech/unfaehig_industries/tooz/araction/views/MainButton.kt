package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class MainButton : View {

    private var radialBoundingRect: RectF = RectF(0f,0f,100f,100f)
    private var radius: Float = 5f
    private var fill: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, radialBoundingRect: RectF, radius: Float, fillColor: Int) : super(context) {
        this.radialBoundingRect = radialBoundingRect
        this.radius = radius
        this.fill.apply { color= fillColor; style= Paint.Style.FILL }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            this.drawCircle(radialBoundingRect.centerX(), radialBoundingRect.centerY(), radius, fill)
        }
    }
}
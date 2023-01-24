package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import tech.unfaehig_industries.tooz.araction.R
import java.util.*

class RadialMenu : ViewGroup {

    private var mainButton: ShapeDrawable = ShapeDrawable()
    private var radialButtons: Array<ShapeDrawable> = arrayOf()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val background: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var widthToozScreen = 390f
    private var heightToozScreen = 528f
    private var radialOuterPadding: Float = (heightToozScreen-widthToozScreen)/2
    private var radialBoundingRect: RectF = RectF(0f, radialOuterPadding, widthToozScreen, widthToozScreen+radialOuterPadding)
    private var radialInnerPadding: Float = widthToozScreen/4
    private var radialInnerBoundingRect: RectF = RectF(radialInnerPadding, radialOuterPadding+radialInnerPadding, widthToozScreen-radialInnerPadding, widthToozScreen+radialOuterPadding-radialInnerPadding)
    private var radius = widthToozScreen / 8
    private var circleColor : Int = Color.YELLOW
    private var borderColor : Int = Color.BLACK
    private var borderWidth : Float = 2F

    constructor(context:Context) : super(context) {
        init(null)
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context:Context, attrs:AttributeSet, defStyleAttr:Int, defStyleRes:Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attr: AttributeSet?) {
        mainButton = ShapeDrawable()
        radialButtons = arrayOf()
        val typedArray = context.theme.obtainStyledAttributes(attr, R.styleable.RadialMenu, 0, 0)
        circleColor = typedArray.getColor(R.styleable.RadialMenu_circleColor, Color.YELLOW)
        borderColor = typedArray.getColor(R.styleable.RadialMenu_borderColor, Color.BLACK)
        borderWidth = typedArray.getFloat(R.styleable.RadialMenu_circleBorderWidth, 2F)
    }

    override fun onDraw(canvas: Canvas?){
        paint.apply { color = circleColor; style = Paint.Style.FILL }
        background.apply { color = borderColor; style = Paint.Style.FILL }
        canvas?.run {

            //drawing the radialButtons
            addRadialButtons(canvas, arrayOf("a", "b", "c", "a", "b", "c", "a", "b"))

            //drawing the mainButton
            paint.color = Color.RED
            this.drawCircle(widthToozScreen/2, heightToozScreen/2, radius, paint)
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        TODO("Not yet implemented")
    }

    private fun addRadialButtons(canvas: Canvas, data: Array<String>) {
        val count: Int = data.size
        val length: Float = 360f / count
        val rnd = Random()
        background.apply { color = borderColor; style = Paint.Style.FILL }

        for (i in 0 .. count) {
            paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            //drawRadialButton(canvas, i*length, length, paint, background)
            val radialButton: RadialButton = RadialButton(context, radialBoundingRect, radialInnerBoundingRect, i*length, length, paint, background)
            this.addView(radialButton, this.layoutParams)
        }
    }

    private fun drawRadialButton(canvas: Canvas, start_degrees: Float, length_degrees: Float, fill: Paint, background: Paint) {
        canvas.drawArc(radialBoundingRect, start_degrees, length_degrees, true, fill)
        canvas.drawArc(radialInnerBoundingRect, start_degrees, length_degrees, true, background)
    }
}
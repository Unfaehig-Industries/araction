package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import tech.unfaehig_industries.tooz.araction.R
import java.util.Vector

class RadialMenu : View {

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
            drawRadialButton(this, 0f, 85f, paint, background)
            paint.color = Color.YELLOW
            drawRadialButton(this, 95f, 80f, paint, background)
            paint.color = Color.BLUE
            drawRadialButton(this, 185f, 80f, paint, background)
            paint.color = Color.GREEN
            drawRadialButton(this, 275f, 80f, paint, background)

            //drawing the mainButton
            paint.color = Color.RED
            this.drawCircle(widthToozScreen/2, heightToozScreen/2, radius, paint)
        }
    }

    fun drawRadialButton(canvas: Canvas, start_degrees: Float, length_degrees: Float, fill: Paint, background: Paint) {
        canvas.drawArc(radialBoundingRect, start_degrees, length_degrees, true, fill)
        canvas.drawArc(radialInnerBoundingRect, start_degrees, length_degrees, true, background)
    }
}
package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import tech.unfaehig_industries.tooz.araction.R

class RadialMenu : View {

    private var mainButton: ShapeDrawable = ShapeDrawable()
    private var radialButtons: Array<ShapeDrawable> = arrayOf()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var halfWidth = 0
    private var halfHeight = 0
    private var radius = 0
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
        //drawing the circle
        paint.apply { color = circleColor; style = Paint.Style.FILL }
        canvas?.drawCircle(halfWidth.toFloat(), halfHeight.toFloat(), radius.toFloat(), paint)
        //drawing circle border
        paint.apply { color = borderColor; style = Paint.Style.STROKE; strokeWidth = borderWidth}
        canvas?.drawCircle(halfWidth.toFloat(), halfHeight.toFloat(), radius.toFloat(), paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        halfHeight = measuredHeight / 2
        halfWidth = measuredWidth / 2
        radius = halfHeight.coerceAtMost(halfWidth) - borderWidth.toInt()
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}
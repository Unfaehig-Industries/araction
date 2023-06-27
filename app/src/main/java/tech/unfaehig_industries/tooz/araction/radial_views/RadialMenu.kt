package tech.unfaehig_industries.tooz.araction.radial_views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import tech.unfaehig_industries.tooz.araction.R
import java.util.*
import kotlin.collections.ArrayList

class RadialMenu : RelativeLayout {

    private lateinit var mainButton: MainButton
    private lateinit var radialButtons: ArrayList<RadialButton>
    private var hoveredButton: RadialMenuButton? = null
    private var mainColor: Int = Color.CYAN
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var widthToozScreen = 390f
    private var heightToozScreen = 528f
    // With the prerequisite that the screen is higher than wide
    private var radialOuterPadding: Float = (heightToozScreen-widthToozScreen)/2
    private var radialBoundingRect: RectF = RectF(0f, radialOuterPadding, widthToozScreen, widthToozScreen+radialOuterPadding)
    private var radialInnerPadding: Float = widthToozScreen/4
    private var radialInnerBoundingRect: RectF = RectF(radialInnerPadding, radialOuterPadding+radialInnerPadding, widthToozScreen-radialInnerPadding, widthToozScreen+radialOuterPadding-radialInnerPadding)
    private var mainButtonRadius = widthToozScreen / 8

    constructor(context:Context) : super(context) {
        init(null)
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attr: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attr, R.styleable.ToozMenuStyleable, 0, 0)

        mainColor = typedArray.getColor(R.styleable.ToozMenuStyleable_mainColor, Color.CYAN)

        val backgroundColor: Int = typedArray.getColor(R.styleable.ToozMenuStyleable_backgroundColor, Color.BLACK)
        backgroundPaint.apply { color= backgroundColor; style= Paint.Style.FILL }

        addRadialButtons(arrayOf("a", "b", "c", "d"))
        addMainButton()
    }

    private fun addMainButton() {
        mainButton = MainButton(context, radialBoundingRect, mainButtonRadius, mainColor)
        this.addView(mainButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun addRadialButtons(data: Array<String>) {
        radialButtons = ArrayList(data.size)
        val length: Float = 360f / data.size
        val rnd = Random()

        for (i in 0 until(data.size)) {

            val fillColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            val radialButton = RadialButton(context, radialBoundingRect, radialInnerBoundingRect, i*length, length, data[i], fillColor, backgroundPaint)
            radialButtons.add(radialButton)

            this.addView(radialButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    fun highlightButton(angle: Double, distance: Double) {
        if (distance <= (radialInnerBoundingRect.width() / 2) ) {
            if (hoveredButton != mainButton) {
                hoveredButton?.isHovered = false
                mainButton.isHovered = true

                hoveredButton = mainButton
            }

            return
        }

        if (angle.isNaN()) {
            return
        }

        var degrees: Double = ( angle * (180 / Math.PI) ) - 90
        degrees = degrees.mod(360f)

        for (button in radialButtons) {
            if (button.isOnButton(degrees)) {
                if (hoveredButton != button) {
                    hoveredButton?.isHovered = false
                    button.isHovered = true

                    hoveredButton = button
                }

                break
            }
        }
    }
}
package tech.unfaehig_industries.tooz.araction.radial_views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import tech.unfaehig_industries.tooz.araction.R
import kotlin.collections.ArrayList

class RadialMenu : RelativeLayout {

    private lateinit var mainButton: MainButton
    private lateinit var radialButtons: ArrayList<RadialButton>
    private var hoveredButton: RadialMenuButton? = null
    private var mainColor: Int = Color.CYAN
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var screenRect: RectF
    // With the prerequisite that the screen is higher than wide
    private var radialOuterPadding: Float = (screenRect.height()-screenRect.width())/2
    private var radialBoundingRect: RectF = RectF(0f, radialOuterPadding, screenRect.width(), screenRect.width()+radialOuterPadding)
    private var radialInnerPadding: Float = screenRect.width()/4
    private var radialInnerBoundingRect: RectF = RectF(radialInnerPadding, radialOuterPadding+radialInnerPadding, screenRect.width()-radialInnerPadding, screenRect.width()+radialOuterPadding-radialInnerPadding)
    private var mainButtonRadius = screenRect.width() / 8

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
    }

    fun populate(
        mainButtonColor: Int = Color.RED,
        radialButtons: ArrayList<RadialData>,
        screen: RectF = RectF(0f, 0f, 390f, 528f)
    ) {
        screenRect = RectF(screen)
        addMainButton(mainButtonColor)
        addRadialButtons(radialButtons)
    }

    private fun addMainButton(color: Int) {
        mainButton = MainButton(context, radialBoundingRect, mainButtonRadius, color)
        this.addView(mainButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun addRadialButtons(radialData: ArrayList<RadialData>) {
        radialButtons = ArrayList(radialData.size)
        val length: Float = 360f / radialData.size

        radialData.forEachIndexed { i, radial ->
            val callback: Any = radial.callback ?: {}
            val radialButton = RadialButton(context, radialBoundingRect, radialInnerBoundingRect, i*length, length, radial.title, callback, radial.color, backgroundPaint)
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
class RadialData(val title: String, val color: Int, val callback: Any?, val children: List<RadialData>)
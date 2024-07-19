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
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var screenRect: RectF
    private lateinit var radialOuterPadding: Float
    private lateinit var radialBoundingRect: RectF
    private lateinit var radialInnerPadding: Float
    private lateinit var radialInnerBoundingRect: RectF
    private lateinit var mainButtonRadius: Float

    constructor(context:Context) : super(context) {
        init(null)
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attr: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attr, R.styleable.ToozMenuStyleable, 0, 0)

        val backgroundColor: Int = typedArray.getColor(R.styleable.ToozMenuStyleable_backgroundColor, Color.BLACK)
        backgroundPaint.apply { color= backgroundColor; style= Paint.Style.FILL }
    }

    open fun populate(
        main: RadialButtonData,
        radials: List<RadialButtonData>,
        screen: RectF = RectF(0f, 0f, 390f, 528f),
    ) {
        screenRect = RectF(screen)

        if(screenRect.height >= screenRect.width) {
            val longSide = screenRect.height
            val shortSide = screen.width
        }
        else {
            val longSide = screenRect.width
            val shortSide = screen.height
        }

        radialOuterPadding = (longSide - shortSide) / 2
        radialBoundingRect  = RectF(0f, radialOuterPadding, shortSide, shortSide + radialOuterPadding)
        radialInnerPadding = shortSide / 4
        pradialInnerBoundingRect = RectF(radialInnerPadding, radialOuterPadding + radialInnerPadding, shortSide - radialInnerPadding, shortSide + radialOuterPadding - radialInnerPadding)
        mainButtonRadius = shortSide / 8

        addMainButton(main)
        tileButtons = addRadialButtons(radials)
        hoveredButton = mainButton
    }

    private fun addMainButton(data: RadialButtonData) {
        mainButton = MainButton(context, radialBoundingRect, mainButtonRadius, data.color, data.title, data.callback)
        this.addView(mainButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun addRadialButtons(data: Array<RadialButtonData>) {
        radialButtons = ArrayList(data.size)
        val length: Float = 360f / data.size

        for (i in 0 until(data.size)) {

            val buttonData = data[i]
            val radialButton = RadialButton(context, radialBoundingRect, radialInnerBoundingRect, i*length, length, buttonData.color, backgroundPaint, buttonData.title, buttonData.callback)
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

class RadialButtonData(val title: String, val color: Int, val callback: () -> Unit)
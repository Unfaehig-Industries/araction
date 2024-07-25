package tech.unfaehig_industries.tooz.araction.radial_menu

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import tech.unfaehig_industries.tooz.araction.R
import timber.log.Timber
import kotlin.collections.ArrayList

class RadialMenu : RelativeLayout {

    private lateinit var mainButton: MainButton
    private lateinit var radialButtons: ArrayList<RadialButton>
    private var hoveredButton: RadialMenuButton? = null
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var screenRect: RectF
    private var radialOuterPadding: Float = 0F
    private lateinit var radialBoundingRect: RectF
    private var radialInnerPadding: Float = 0F
    private lateinit var radialInnerBoundingRect: RectF
    private var mainButtonRadius: Float = 0F

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

    fun populate(
        main: RadialButtonData,
        radials: Array<RadialButtonData>,
        screen: RectF = RectF(0f, 0f, 390f, 528f),
    ) {
        screenRect = RectF(screen)

        val longSide: Float
        val shortSide: Float

        if(screenRect.height() >= screenRect.width()) {
            longSide = screenRect.height()
            shortSide = screenRect.width()
        }
        else {
            longSide = screenRect.width()
            shortSide = screenRect.height()
        }

        radialOuterPadding = (longSide - shortSide) / 2
        radialBoundingRect  = RectF(0f, radialOuterPadding, shortSide, shortSide + radialOuterPadding)
        radialInnerPadding = shortSide / 4
        radialInnerBoundingRect = RectF(radialInnerPadding, radialOuterPadding + radialInnerPadding, shortSide - radialInnerPadding, shortSide + radialOuterPadding - radialInnerPadding)
        mainButtonRadius = shortSide / 8

        addMainButton(main)
        addRadialButtons(radials)
        hoveredButton = mainButton
    }

    fun replaceContent(data: RadialMenuData) {
        replaceMainButton(data.main)
        replaceRadialButtons(data.radials)
        invalidate()
        Timber.d("replaced Menu")
    }

    private fun addMainButton(data: RadialButtonData) {

        mainButton = MainButton(
            context,
            radialBoundingRect,
            mainButtonRadius,
            data.color,
            data.title,

        )

        if(data is RadialActionButtonData) {
            mainButton.callback = data.callback
        }
        if(data is RadialSubMenuButtonData) {
            mainButton.submenu = data.submenu
        }

        this.addView(mainButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun replaceMainButton(data: RadialButtonData) {
        mainButton.updateContent(data.color, data.title)

        if(data is RadialActionButtonData) {
            mainButton.callback = data.callback
            mainButton.submenu = null
        }
        if(data is RadialSubMenuButtonData) {
            mainButton.callback = null
            mainButton.submenu = data.submenu
        }
    }

    private fun addRadialButtons(data: Array<RadialButtonData>) {
        radialButtons = ArrayList(data.size)
        val length: Float = 360f / data.size

        for (i in 0 until(data.size)) {

            val buttonData = data[i]
            val radialButton = RadialButton(
                context,
                radialBoundingRect,
                radialInnerBoundingRect,
                i * length,
                length,
                buttonData.color,
                backgroundPaint,
                buttonData.title
            )

            if(buttonData is RadialActionButtonData) {
                radialButton.callback = buttonData.callback
            }
            if(buttonData is RadialSubMenuButtonData) {
                radialButton.submenu = buttonData.submenu
            }

            radialButtons.add(radialButton)
            this.addView(radialButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    private fun replaceRadialButtons(data: Array<RadialButtonData>) {
        context.mainExecutor.execute {
            run {
                for (radialButton in radialButtons) {
                    this.removeView(radialButton)
                }

                addRadialButtons(data)
            }
        }
        invalidate()
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

    fun stopHoverJob() {
        hoveredButton?.cancelHover()
    }
}

class RadialMenuData(val main: RadialButtonData, val radials: Array<RadialButtonData>)

abstract class RadialButtonData(val title: String, val color: Int)
class RadialActionButtonData(title: String, color: Int, val callback: () -> Unit = {}) : RadialButtonData(title, color)
class RadialSubMenuButtonData(title: String, color: Int, val submenu: RadialMenuData) : RadialButtonData(title, color)

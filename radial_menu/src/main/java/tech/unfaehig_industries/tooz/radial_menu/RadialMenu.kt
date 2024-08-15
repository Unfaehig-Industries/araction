package tech.unfaehig_industries.tooz.radial_menu

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import kotlin.collections.ArrayList

class RadialMenu : RelativeLayout {

    private lateinit var menuData: RadialMenuData
    private var currentIndex = ""
    private var hoveredButton: RadialMenuButton? = null

    private lateinit var mainButton: MainButton
    private lateinit var radialButtons: ArrayList<RadialButton>

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
        data: RadialMenuData,
        screen: RectF = RectF(0f, 0f, 390f, 528f),
    ) {
        menuData = data
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

        addMainButton(data.main)
        addRadialButtons(data.radials)
    }

    fun loadNewMenu(index: Int, data: RadialMenuData) {
        currentIndex = currentIndex.plus("$index-")
        updateMenuData(data)
    }

    fun loadLastMenu() {
        val data = searchForMenuData(currentIndex, menuData)

        if(currentIndex.length > 2) {
            currentIndex = currentIndex.substring(0, currentIndex.lastIndexOf("-") - 1)
        } else {
            currentIndex = ""
        }
        updateMenuData(data)
    }

    private fun searchForMenuData(indexString: String, data: RadialMenuData): RadialMenuData {
        if(indexString.length <= 2) {
            return data
        }

        val index = indexString.first().digitToInt()
        val newIndexString = indexString.substring(indexString.indexOf("-") + 1, indexString.length)

        if(index == -1) {
            if(data.main is RadialSubmenuButtonData) {
                return searchForMenuData(newIndexString, data.main.submenu)
            } else {
                throw Error("Error in indexing")
            }
        }

        val buttonData = data.radials[index]
        if(buttonData is RadialSubmenuButtonData) {
            return searchForMenuData(newIndexString, buttonData.submenu)
        } else {
            throw Error("Error in indexing")
        }
    }

    private fun updateMenuData(data: RadialMenuData) {
        updateMainButton(data.main)
        replaceRadialButtons(data.radials)
        hoveredButton = null
        invalidate()
    }

    private fun addMainButton(data: RadialButtonData) {

        mainButton = MainButton(
            context,
            -1,
            data,
            radialBoundingRect,
            mainButtonRadius,
        )

        this.addView(mainButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun updateMainButton(data: RadialButtonData) {
        context.mainExecutor.execute {
            run {
                this.removeView(mainButton)
                addMainButton(data)
            }
        }
        invalidate()
    }

    private fun addRadialButtons(data: Array<RadialButtonData>) {
        radialButtons = ArrayList(data.size)
        val length: Float = 360f / data.size

        for (i in 0 until(data.size)) {

            val buttonData = data[i]
            val radialButton = RadialButton(
                context,
                i,
                buttonData,
                radialBoundingRect,
                radialInnerBoundingRect,
                i * length,
                length,
                backgroundPaint
            )

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
class RadialSubmenuButtonData(title: String, color: Int, val submenu: RadialMenuData) : RadialButtonData(title, color)
class RadialBackButtonData(title: String, color: Int) : RadialButtonData(title, color)
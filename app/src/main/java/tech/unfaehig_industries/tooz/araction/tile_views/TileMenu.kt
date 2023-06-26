package tech.unfaehig_industries.tooz.araction.tile_views

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import tech.unfaehig_industries.tooz.araction.R
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.math.cos
import kotlin.math.sin

class TileMenu : RelativeLayout {

    private lateinit var tileButtons: ArrayList<TileButton>
    private var hoveredButton: TileButton? = null
    private var mainColor: Int = Color.CYAN
    private var backgroundColor: Int = Color.BLACK
    private val screen = RectF(0f, 0f, 390f, 528f)
    private val buttonRect = RectF(0f, 0f, (screen.width() / 3.5f), (screen.height() / 5))
    private val VIEWMOVEMENTFACTOR: Float = 2f

    constructor(context:Context) : super(context) {
        init(null)
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attr: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attr, R.styleable.ToozMenuStyleable, 0, 0)

        mainColor = typedArray.getColor(R.styleable.ToozMenuStyleable_mainColor, Color.CYAN)
        backgroundColor = typedArray.getColor(R.styleable.ToozMenuStyleable_backgroundColor, Color.BLACK)

        val menuMap = LinkedHashMap<String, Array<String>>()
        menuMap["1"] = arrayOf("a", "b", "c", "d")
        menuMap["2"] = arrayOf("a", "b", "c", "d")
        menuMap["3"] = arrayOf("a", "b", "c", "d")
        menuMap["4"] = arrayOf("a", "b", "c", "d")
        menuMap["5"] = arrayOf("a", "b", "c", "d")
        tileButtons = addTileButtons(menuMap)
    }

    private fun addTileButtons(data: LinkedHashMap<String, Array<String>>): ArrayList<TileButton> {
        val buttonsArray = ArrayList<TileButton>(data.size)
        val boundingRect = RectF(buttonRect)
        val horizontalSpacing: Float = screen.width() / 2.5f
        boundingRect.offsetTo((horizontalSpacing / 3), screen.centerY() - (buttonRect.height() / 2))

        data.forEach { (label, children) ->
            val childrenButtons = addTileButtons(children, boundingRect)

            val tileButton: TileButton = createTileButton(label, childrenButtons, boundingRect)

            buttonsArray.add(tileButton)
            buttonsArray.addAll(childrenButtons)
            this.addView(tileButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

            boundingRect.offsetTo(boundingRect.left + horizontalSpacing, boundingRect.top)
        }

        return buttonsArray
    }

    private fun addTileButtons(data: Array<String>, originRect: RectF = RectF()): ArrayList<TileButton> {
        val buttonsArray = ArrayList<TileButton>(data.size)
        val boundingRect = RectF(originRect)
        val verticalSpacing: Float = (screen.height() / 4)

        if (boundingRect.isEmpty) {
            boundingRect.offsetTo(screen.centerX() - (buttonRect.width() / 2), screen.centerY() - (buttonRect.height() / 2))
        }

        for (i in 0 until(data.size)) {
            boundingRect.offsetTo(boundingRect.left, boundingRect.top + verticalSpacing)

            val tileButton = createTileButton(data[i], ArrayList(), boundingRect)

            buttonsArray.add(tileButton)
            this.addView(tileButton, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }

        return buttonsArray
    }

    private fun createTileButton(label: String, children: ArrayList<TileButton>, positionRect: RectF): TileButton {
        val rnd = Random()
        val fillColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        return TileButton(context, positionRect, RectF(buttonRect), label, children, fillColor, backgroundColor)
    }

    fun moveView(angle: Double, distance: Double) {
        if (angle.isNaN()) {
            return
        }

        val distX: Float = (distance * sin(angle)).toFloat()
        val distY: Float = (distance * cos(angle)).toFloat()

        tileButtons.forEach { button: TileButton ->
            button.animate().translationX(button.baseX + distX * VIEWMOVEMENTFACTOR)
            button.animate().translationY(button.baseY + distY * VIEWMOVEMENTFACTOR)
        }

        highlightButton()
    }

    private fun highlightButton() {
        val buttons = ArrayList<TileButton>()

        tileButtons.forEach { button -> buttons.add(button) }

        for (button in buttons) {
            if (button.isOnButton(this, screen, buttonRect)) {
                if (hoveredButton != button) {
                    hoveredButton?.isHovered = false
                    button.isHovered = true

                    hoveredButton = button
                    Timber.d(button.label)
                }

                break
            }
        }
    }
}
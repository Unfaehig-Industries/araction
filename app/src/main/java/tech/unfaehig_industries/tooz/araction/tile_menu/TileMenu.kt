package tech.unfaehig_industries.tooz.araction.tile_menu

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import tech.unfaehig_industries.tooz.araction.R
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin

open class TileMenu : RelativeLayout {

    protected var tileButtons: ArrayList<TileButton> = ArrayList()
    private var hoveredButton: TileButton? = null

    private var mainColor: Int = Color.CYAN
    private var backgroundColor: Int = Color.BLACK
    private lateinit var screenRect: RectF
    protected lateinit var buttonRect: RectF
    protected var sensitivityX: Float = 4f
    protected var sensitivityY: Float = 4f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ToozMenuStyleable, 0, 0)

        mainColor = typedArray.getColor(R.styleable.ToozMenuStyleable_mainColor, Color.CYAN)
        backgroundColor = typedArray.getColor(R.styleable.ToozMenuStyleable_backgroundColor, Color.BLACK)
    }

    open fun populate(
        tiles: Array<TileButtonData>,
        screen: RectF = RectF(0f, 0f, 390f, 528f),
        _sensitivityX: Float = 4f,
        _sensitivityY: Float = 4f,
        direction: Direction = Direction.HORIZONTAL
    ) {
        screenRect = RectF(screen)
        buttonRect = RectF(0f, 0f, 200f, (screen.height() / 5))
        sensitivityX = _sensitivityX
        sensitivityY = _sensitivityY

        val boundingRect = RectF(buttonRect)

        boundingRect.offsetTo(screenRect.centerX() - (buttonRect.width() / 2), screenRect.centerY() - (buttonRect.height() / 2))
        layoutTileButtons(tiles, direction, boundingRect)
        hoveredButton = tileButtons.first()
    }

    private fun layoutTileButtons(tiles: Array<TileButtonData>, direction: Direction, boundingRect: RectF, level: Int = 0): ArrayList<TileButton> {
        val buttonsArray = ArrayList<TileButton>(tiles.size)

        val otherDirection = when (direction) {
            Direction.HORIZONTAL -> Direction.VERTICAL
            Direction.VERTICAL -> Direction.HORIZONTAL
        }

        val buttonHidden = level > 1

        for((index, tile) in tiles.withIndex()) {
            if (index > 0 || level > 0) {
                offsetTile(direction, boundingRect)
            }

            val children = layoutTileButtons(tile.children, otherDirection, RectF(boundingRect), level+1)

            val button = addTileButton(boundingRect, tile, children, buttonHidden)
            buttonsArray.add(button)
        }

        return buttonsArray
    }

    private fun offsetTile(direction: Direction, boundingRect: RectF): RectF {
        val horizontalSpacing: Float = boundingRect.width() + 40f
        val verticalSpacing: Float = boundingRect.height() + 20f

        when (direction) {
            Direction.HORIZONTAL -> {
                boundingRect.offsetTo(boundingRect.left + horizontalSpacing, boundingRect.top)
            }

            Direction.VERTICAL -> {
                boundingRect.offsetTo(boundingRect.left, boundingRect.top + verticalSpacing)
            }
        }

        return boundingRect
    }

    private fun addTileButton(boundingRect: RectF, data: TileButtonData, children: ArrayList<TileButton>, hidden: Boolean): TileButton {
        val tileButton = TileButton(
            context,
            boundingRect,
            RectF(buttonRect),
            data.title,
            children,
            data.callback,
            data.tileColor,
            backgroundColor
        )

        if(hidden) {
            tileButton.visibility = View.INVISIBLE
        }

        this.addView(
            tileButton,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )

        tileButtons.add(tileButton)
        return tileButton
    }

    open fun moveView(angle: Double, distance: Double) {
        if (angle.isNaN()) {
            return
        }

        val distX: Float = -(distance * sin(angle)).toFloat() * sensitivityX
        val distY: Float = (distance * cos(angle)).toFloat() * sensitivityY

        tileButtons.forEach { button: TileButton ->
            button.animate().translationX(button.baseX + distX)
            button.animate().translationY(button.baseY + distY)
        }

        highlightButton()
    }

    protected fun highlightButton() {
        if (hoveredButton?.isInCenter(screenRect) == true) {
            return
        }

        hoveredButton?.isHovered = false
        hoveredButton = null

        for (button in tileButtons) {
            if (button.isInCenter(screenRect)) {
                if (hoveredButton != button) {
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

class TileButtonData(val title: String, val tileColor: Int, val callback: () -> Unit, val children: Array<TileButtonData>)

enum class Direction {
    HORIZONTAL, VERTICAL
}
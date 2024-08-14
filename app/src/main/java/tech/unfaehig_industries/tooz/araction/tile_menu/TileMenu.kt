package tech.unfaehig_industries.tooz.araction.tile_menu

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import tech.unfaehig_industries.tooz.araction.R
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin

open class TileMenu : RelativeLayout {

    protected lateinit var tileButtons: TileGroup
    private var hoveredButton: TileButton? = null

    private var mainColor: Int = Color.CYAN
    private var backgroundColor: Int = Color.BLACK
    private lateinit var screenRect: RectF
    protected lateinit var buttonRect: RectF
    protected var sensitivityX: Float = 4f
    protected var sensitivityY: Float = 4f

    private var distX = 0f
    private var distY = 0f

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
    }

    private fun layoutTileButtons(tiles: Array<TileButtonData>, mainTileDirection: Direction, boundingRect: RectF, ancestors: Array<TileButton> = arrayOf()) {

        val childTileDirection = when (mainTileDirection) {
            Direction.HORIZONTAL -> Direction.VERTICAL
            Direction.VERTICAL -> Direction.HORIZONTAL
        }

        val siblings: ArrayList<TileGroup> = arrayListOf()
        var tileBoundingRect = RectF(boundingRect)

        for((index, tile) in tiles.withIndex()) {
            if (index > 0) {
                tileBoundingRect = offsetTile(mainTileDirection, tileBoundingRect)
            }

            val children: ArrayList<TileGroup> = arrayListOf()

            if (tile is TileSubmenuButtonData) {
                var childBoundingRect = RectF(tileBoundingRect)

                for (child in tile.submenu) {
                    childBoundingRect = offsetTile(childTileDirection, childBoundingRect)

                    val childButton = addTileButton(childBoundingRect, child)
                    children.add(TileGroup(childButton, null))
                }
            }

            val parent = addTileButton(tileBoundingRect, tile, false)
            siblings.add(TileGroup(parent, children))
        }

        val first = siblings.first().parent
        siblings.first().children?.let { siblings.addAll(it) }
        siblings.removeFirst()

        tileButtons = TileGroup(first, siblings)
    }

    private fun offsetTile(direction: Direction, boundingRect: RectF): RectF {
        val horizontalSpacing: Float = boundingRect.width() + 40f
        val verticalSpacing: Float = boundingRect.height() + 20f

        val offsetBoundingRect = RectF(boundingRect)
        when (direction) {
            Direction.HORIZONTAL -> {
                offsetBoundingRect.offsetTo(boundingRect.left + horizontalSpacing, boundingRect.top)
            }

            Direction.VERTICAL -> {
                offsetBoundingRect.offsetTo(boundingRect.left, boundingRect.top + verticalSpacing)
            }
        }

        return offsetBoundingRect
    }

    private fun addTileButton(positionRect: RectF, data: TileButtonData, actionable: Boolean = true): TileButton {
        val tileButton = TileButton(
            context,
            data,
            positionRect,
            RectF(buttonRect),
            backgroundColor,
            actionable
        )

        this.addView(
            tileButton,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )

        val animator = tileButton.animate()
        animator.setInterpolator(LinearInterpolator())
        animator.setStartDelay(0)
        animator.setDuration(0)
        animator.translationX(tileButton.baseX + distX)
        animator.translationY(tileButton.baseY + distY)
        animator.setDuration(100)

        return tileButton
    }

    open fun moveView(angle: Double, distance: Double) {
        if (angle.isNaN()) {
            return
        }

        distX = -(distance * sin(angle)).toFloat() * sensitivityX
        distY = (distance * cos(angle)).toFloat() * sensitivityY

        for (button in tileButtons.descendants) {
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

        for (button in tileButtons.descendants) {
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

    fun loadSubMenu(triggeredButton: TileButton, submenu: Array<TileButtonData>) {
        context.mainExecutor.execute {
            run {
                //TODO: Remove all tiles except for ancestors from view

                triggeredButton.actionable = false

                val positionRect = offsetTile(Direction.HORIZONTAL, triggeredButton.positionRect)
                //TODO: Layout new tiles
                layoutTileButtons(submenu, Direction.HORIZONTAL, positionRect)
                invalidate()
            }
        }
    }

    //TODO: Add function to add buttons back, that were deleted by a submenu button
}

open class TileButtonData(val label: String, val tileColor: Int)
class TileActionButtonData(label: String, tileColor: Int, val callback: () -> Unit): TileButtonData(label, tileColor)
class TileSubmenuButtonData (label: String, tileColor: Int, val submenu: Array<TileButtonData>): TileButtonData(label, tileColor)

enum class Direction {
    HORIZONTAL, VERTICAL
}
package tech.unfaehig_industries.tooz.araction.tile_locked_menu

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import tech.unfaehig_industries.tooz.araction.tile_menu.Direction
import tech.unfaehig_industries.tooz.araction.tile_menu.TileButtonData
import tech.unfaehig_industries.tooz.araction.tile_menu.TileMenu
import kotlin.math.cos
import kotlin.math.sin

class TileLockedMenu : TileMenu {

    private var lastDistX: Float = 0F
    private val menuRect: RectF = RectF(0f, 0f, 0f, 0f)
    private var lockedMovement: Boolean = false
    private var offsetX: Float = 0f
    private var coordinateXTranslation: Float = 0f
    private var coordinateYTranslation: Float = 0f


    constructor(context:Context) : super(context)

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs)

    private fun calculateCurrentMenuDimensions() {
        menuRect.set(tileButtons.first().positionRect)

        for (button in tileButtons) {

            if (button.translationX < menuRect.left) {
                menuRect.left = button.translationX
            }

            if (button.translationX > menuRect.right) {
                menuRect.right = button.translationX
            }

            if (button.translationY < menuRect.top) {
                menuRect.top = button.translationY
            }

            if (button.translationY > menuRect.bottom) {
                menuRect.bottom = button.translationY
            }
        }

        coordinateXTranslation = menuRect.left
        coordinateYTranslation = menuRect.top
    }


    override fun layoutTileButtons(tiles: Array<TileButtonData>, mainTileDirection: Direction, boundingRect: RectF) {
        super.layoutTileButtons(tiles, mainTileDirection, boundingRect)

        // Calculate menu dimensions, everytime a new menu is laid out
        calculateCurrentMenuDimensions()
    }

    override fun moveView(angle: Double, distance: Double) {
        // Android seems to send a couple sensor readings with NaN at the beginning
        if (angle.isNaN()) return

        var distX: Float = (distance * sin(angle)).toFloat() * sensitivityX
        var distY: Float = -(distance * cos(angle)).toFloat() * sensitivityY

        // Translate distY into screen coordinates
        val translatedY = distY + coordinateYTranslation

        // Enforce that in all rows, but the top row, no horizontal movement is possible
        if (translatedY > menuRect.top + buttonRect.height() ) {
            distX = lastDistX

            lockedMovement = true
        }
        else if (lockedMovement) {
            // Subtract current horizontal position, to move them to move them to zero
            offsetX -= distX
            // Add at which horizontal position the user was locked, to move them there
            offsetX += lastDistX

            lockedMovement = false
        }

        lastDistX = distX
        // Add offset, to account for horizontal movement while the user was locked into a column
        distX += offsetX

        // Translate distX into screen coordinates
        val translatedX = distX + coordinateXTranslation

        if(!lockedMovement) {
            // Make sure one can't go left of the first column
            if (translatedX < menuRect.left) {
                distX = menuRect.left - coordinateXTranslation
            }

            // Make sure one can't go right of the last column
            if (translatedX > menuRect.right) {
                distX = menuRect.right - coordinateXTranslation
            }
        }

        // Make sure one can't go above the home row
        if (translatedY < menuRect.top) {
            distY = menuRect.top - coordinateYTranslation
        }

        // Make sure one can't go under the last row
        if (translatedY > menuRect.bottom ) {
            distY = menuRect.bottom - coordinateYTranslation
        }

        for(button in tileButtons) {
            button.animate().translationX(button.baseX - distX)
            button.animate().translationY(button.baseY - distY)
        }

        highlightButton()
    }
}
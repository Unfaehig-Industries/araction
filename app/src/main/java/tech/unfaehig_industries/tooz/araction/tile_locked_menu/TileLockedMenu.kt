package tech.unfaehig_industries.tooz.araction.tile_locked_menu

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import tech.unfaehig_industries.tooz.araction.tile_menu.TileButton
import tech.unfaehig_industries.tooz.araction.tile_menu.TileButtonData
import tech.unfaehig_industries.tooz.araction.tile_menu.TileMenu
import kotlin.math.cos
import kotlin.math.sin

class TileLockedMenu : TileMenu {

    private var lastDistX: Float = 0F
    private val menuRect: RectF = RectF(0f, 0f, 0f, 0f)
    private var lockedMovement: Boolean = false
    private var translationX: Float = 0f

    constructor(context:Context) : super(context) {
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
    }

    override fun populate(
        tiles: Array<TileButtonData>,
        screen: RectF,
        _sensitivityX: Float,
        _sensitivityY: Float
    ) {
        super.populate(tiles, screen, _sensitivityX, _sensitivityY)

        for (button in tileButtons) {

            if (button.translationX > menuRect.right) {
                menuRect.right = button.translationX - buttonRect.width()
            }

            if (button.translationY > menuRect.bottom) {
                menuRect.bottom = button.translationY  - buttonRect.height()
            }
        }
    }

    override fun moveView(angle: Double, distance: Double) {
        if (angle.isNaN()) {
            return
        }

        var distX: Float = (distance * sin(angle)).toFloat() * sensitivityX
        var distY: Float = -(distance * cos(angle)).toFloat() * sensitivityY

        // Enforce that in all rows, but the home row, no horizontal movement is possible
        // (y=0 is at center of home row)
        if (distY >= ( buttonRect.height() / 2) ) {
            distX = lastDistX
            lockedMovement = true
        } else if (lockedMovement) {
            lockedMovement = false
            translationX += distX - lastDistX
        }

        lastDistX = distX
        distX -= translationX

        // Make sure one can't go left of the first column (x=0 is at center of first column)
        if (distX < ( -buttonRect.width() / 2) ) {
            distX = ( -buttonRect.width() / 2)
        }

        // Make sure one can't go right of the last column (x=0 is at center of first column)
        if (distX > menuRect.right ) {
            distX = menuRect.right
        }

        // Make sure one can't go above the home row (y=0 is at center of home row)
        if (distY < -( buttonRect.height() / 2) ) {
            distY = -( buttonRect.height() / 2)
        }

        // Make sure one can't go under the last row (y=0 is at center of home row)
        if (distY > menuRect.bottom ) {
            distY = menuRect.bottom
        }

        tileButtons.forEach { button: TileButton ->
            button.animate().translationX(button.baseX - distX)
            button.animate().translationY(button.baseY - distY)
        }

        highlightButton()
    }
}
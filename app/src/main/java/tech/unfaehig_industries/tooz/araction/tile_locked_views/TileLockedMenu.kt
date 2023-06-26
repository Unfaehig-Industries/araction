package tech.unfaehig_industries.tooz.araction.tile_locked_views

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import tech.unfaehig_industries.tooz.araction.tile_views.TileButton
import tech.unfaehig_industries.tooz.araction.tile_views.TileMenu
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

class TileLockedMenu : TileMenu {

    private var lastDistX: Float = 0F
    private val menuRect: RectF = RectF(0f, 0f, screen.width(), screen.height())

    constructor(context:Context) : super(context) {
        setBoundaries()
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        setBoundaries()
    }

    private fun setBoundaries() {
        for (button in tileButtons) {

            if (button.translationX > menuRect.right) {
                menuRect.right = button.translationX - ( buttonRect.width() / 2 )
            }

            if (button.translationY > menuRect.bottom) {
                menuRect.bottom = button.translationY  - ( buttonRect.height() / 2 )
            }
        }
    }

    override fun moveView(angle: Double, distance: Double) {
        if (angle.isNaN()) {
            return
        }

        var distX: Float = (distance * sin(angle)).toFloat() * viewMovementFactorX
        var distY: Float = -(distance * cos(angle)).toFloat() * viewMovementFactorY

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

        // Enforce that in all rows, but the base row, no horizontal movement is possible
        if (distY >= ( buttonRect.height() / 2) ) {
            distX = lastDistX
        }

        lastDistX = distX

        tileButtons.forEach { button: TileButton ->
            button.animate().translationX(button.baseX - distX)
            button.animate().translationY(button.baseY - distY)
        }

        highlightButton()
    }
}
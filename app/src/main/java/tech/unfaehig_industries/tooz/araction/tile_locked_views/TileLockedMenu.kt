package tech.unfaehig_industries.tooz.araction.tile_locked_views

import android.content.Context
import android.util.AttributeSet
import tech.unfaehig_industries.tooz.araction.tile_views.TileButton
import tech.unfaehig_industries.tooz.araction.tile_views.TileMenu
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

class TileLockedMenu : TileMenu {

    private var lastDistX: Float = 0F

    constructor(context:Context) : super(context) {
        init(null)
    }

    constructor(context:Context, attrs:AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    override fun moveView(angle: Double, distance: Double) {
        if (angle.isNaN()) {
            return
        }

        var distX: Float = (distance * sin(angle)).toFloat() * viewMovementFactorX
        var distY: Float = (distance * cos(angle)).toFloat() * viewMovementFactorY

        // Make sure one can't go above the home row (y=0 is at center of home row
        if (distY > ( buttonRect.height() / 2) ) {
            distY = ( buttonRect.height() / 2)
        }

        // Enforce that in all rows, but the base row, no horizontal movement is possible
        Timber.d("distY: $distY threshold: ${-(screen.height() / 5)}")
        if (distY <= -( buttonRect.height() / 2) ) {
            distX = lastDistX
        }

        lastDistX = distX

        tileButtons.forEach { button: TileButton ->
            button.animate().translationX(button.baseX + distX)
            button.animate().translationY(button.baseY + distY)
        }

        highlightButton()
    }
}
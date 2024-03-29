package tech.unfaehig_industries.tooz.araction.radial_views

import android.content.Context
import android.view.View

abstract class RadialMenuButton(context: Context) : View(context) {

    override fun onHoverChanged(hovered: Boolean) {
        super.onHoverChanged(hovered)

        if (hovered) {
            animateHover()
        }
        else {
            cancelHover()
        }
    }

    abstract fun animateHover(durationInSeconds: Long = 3L)
    abstract fun cancelHover()
}
package tech.unfaehig_industries.tooz.araction.radial_menu

import android.content.Context
import android.view.View

abstract class RadialMenuButton(context: Context) : View(context) {

    var callback: (() -> Unit)? = null
    var submenu: RadialMenuData? = null

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
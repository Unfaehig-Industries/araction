package tech.unfaehig_industries.tooz.radial_menu

import android.content.Context
import android.view.View

abstract class RadialMenuButton(context: Context, private val index: Int = -2) : View(context) {

    private var callback: (() -> Unit)? = null
    private var submenu: RadialMenuData? = null
    private var backButton: Boolean = false

    override fun onHoverChanged(hovered: Boolean) {
        super.onHoverChanged(hovered)

        if (hovered) {
            animateHover()
        }
        else {
            cancelHover()
        }
    }

    protected fun setAction(data: RadialButtonData) {
        if(data is RadialActionButtonData) {
            callback = data.callback
            submenu = null
            backButton = false
        }
        if(data is RadialSubmenuButtonData) {
            callback = null
            submenu = data.submenu
            backButton = false
        }
        if (data is RadialBackButtonData) {
            callback = null
            submenu = null
            backButton = true
        }
    }

    protected fun takeAction() {
        if(backButton) {
            (parent as RadialMenu).loadLastMenu()
            return
        }

        callback?.let { it() }

        submenu?.let {
            if (parent is RadialMenu) {
                (parent as RadialMenu).loadNewMenu(index, it)
            }
        }
    }

    abstract fun animateHover(durationInSeconds: Long = 3L)
    abstract fun cancelHover()
}
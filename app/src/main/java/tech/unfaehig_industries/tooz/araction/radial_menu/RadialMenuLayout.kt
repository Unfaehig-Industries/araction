package tech.unfaehig_industries.tooz.araction.radial_menu

import android.content.Context
import android.view.LayoutInflater
import tech.unfaehig_industries.tooz.araction.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.SafeSensorReading
import tech.unfaehig_industries.tooz.araction.databinding.RadialMenuLayoutBinding
import tech.unfaehig_industries.tooz.araction.radial_views.RadialMenu
import tooz.bto.toozifier.Toozifier

class RadialMenuLayout (toozifier: Toozifier) : BaseToozifierLayout(toozifier) {

    // These are views that are displayed in the glasses
    private var radialMenuView: RadialMenuLayoutBinding? = null
    val radialMenu: RadialMenu get() = radialMenuView!!.radialMenu

    override fun sendFrame() {
        sendBlankFrame()
    }

    override fun sendFrame (reading: SafeSensorReading) {
        radialMenuView?.run {
            layoutView = this.root
        }
    }

    override fun sendBlankFrame() {
        radialMenuView?.run {
            layoutView = this.root
        }
    }

    override fun inflateView(context: Context) {
        radialMenuView = RadialMenuLayoutBinding.inflate(LayoutInflater.from(context))
        sendBlankFrame()
    }
}
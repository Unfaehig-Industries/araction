package tech.unfaehig_industries.tooz.araction.tile_menu

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import tech.unfaehig_industries.tooz.araction.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.databinding.TileMenuLayoutBinding
import tech.unfaehig_industries.tooz.araction.tile_views.TileMenu
import tooz.bto.toozifier.Toozifier

class TileMenuLayout (toozifier: Toozifier) : BaseToozifierLayout(toozifier) {

    // These are views that are displayed in the glasses
    private var tileMenuView: TileMenuLayoutBinding? = null
    val tileMenu: TileMenu get() = tileMenuView!!.tileMenu
    val actionText: TextView get() = tileMenuView!!.actionText

    override fun setLayout() {
        tileMenuView?.run {
            layoutView = this.root
        }
    }

    override fun inflateView(context: Context) {
        tileMenuView = TileMenuLayoutBinding.inflate(LayoutInflater.from(context))
        setLayout()
    }
}
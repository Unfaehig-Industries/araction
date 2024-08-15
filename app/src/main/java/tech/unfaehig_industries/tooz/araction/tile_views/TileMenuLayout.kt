package tech.unfaehig_industries.tooz.araction.tile_views

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import tech.unfaehig_industries.tooz.tooz_base_views.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.databinding.TileMenuLayoutBinding
import tech.unfaehig_industries.tooz.tile_menu.tile_menu.TileMenu
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
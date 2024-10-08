package tech.unfaehig_industries.tooz.araction.tile_locked_views

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import tech.unfaehig_industries.tooz.tooz_base_application.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.databinding.TileLockedMenuLayoutBinding
import tech.unfaehig_industries.tooz.tile_menu.tile_locked_menu.TileLockedMenu
import tooz.bto.toozifier.Toozifier

class TileLockedMenuLayout (toozifier: Toozifier) : BaseToozifierLayout(toozifier) {

    // These are views that are displayed in the glasses
    private var tileLockedMenuView: TileLockedMenuLayoutBinding? = null
    val tileLockedMenu: TileLockedMenu get() = tileLockedMenuView!!.tileLockedMenu
    val actionText: TextView get() = tileLockedMenuView!!.actionText

    override fun setLayout() {
        tileLockedMenuView?.run {
            layoutView = this.root
        }
    }

    override fun inflateView(context: Context) {
        tileLockedMenuView = TileLockedMenuLayoutBinding.inflate(LayoutInflater.from(context))
        setLayout()
    }
}
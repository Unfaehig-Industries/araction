package tech.unfaehig_industries.tooz.araction.direction

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.databinding.DirectionLayoutBinding
import tooz.bto.toozifier.Toozifier

class DirectionLayout (private val toozifier: Toozifier) {

    // These are views that are displayed in the glasses
    private var directionView: DirectionLayoutBinding? = null
    // These are the views that are displayed in this view
    private var arrow : ImageView? = null

    fun sendFrame () {
        //needs a float
        arrow?.setImageResource(R.drawable.arrow_96)
        arrow?.rotation = arrow?.rotation!!.plus(5F)
        directionView?.run {
            toozifier.sendFrame(this.root)
        }
    }

    fun sendRootFrame () {
        //needs a float
        arrow?.setImageResource(R.drawable.location_96)
        arrow?.rotation = 0F
        directionView?.run {
            toozifier.sendFrame(this.root)
        }
    }

    fun inflateSensorView(context: Context) {
        directionView = DirectionLayoutBinding.inflate(LayoutInflater.from(context))
        arrow = directionView?.arrow
    }
}
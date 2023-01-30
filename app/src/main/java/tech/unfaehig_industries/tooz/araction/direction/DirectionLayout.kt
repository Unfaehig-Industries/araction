package tech.unfaehig_industries.tooz.araction.direction

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import tech.unfaehig_industries.tooz.araction.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.SafeSensorReading
import tech.unfaehig_industries.tooz.araction.databinding.DirectionLayoutBinding
import tooz.bto.toozifier.Toozifier

class DirectionLayout (toozifier: Toozifier): BaseToozifierLayout(toozifier) {

    // These are views that are displayed in the glasses
    private var directionView: DirectionLayoutBinding? = null
    // These are the views that are displayed in this view
    private var arrow : ImageView? = null

    override fun sendFrame () {
        sendBlankFrame()
    }

    override fun sendFrame (reading: SafeSensorReading) {
        //needs a float
        reading.data["x"]?.run {
            arrow?.rotation = this.toFloat()
        }

        directionView?.run {
            layoutView = this.root
        }
    }

    fun setArrowImage() {
        arrow?.setImageResource(R.drawable.arrow_96)
    }

    override fun sendBlankFrame() {
        //needs a float
        arrow?.setImageResource(R.drawable.location_96)
        arrow?.rotation = 0F
        directionView?.run {
            layoutView = this.root
        }
    }

    override fun inflateView(context: Context) {
        directionView = DirectionLayoutBinding.inflate(LayoutInflater.from(context))
        arrow = directionView?.arrow
    }
}
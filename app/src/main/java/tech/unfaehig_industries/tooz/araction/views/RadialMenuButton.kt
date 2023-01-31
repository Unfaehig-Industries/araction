package tech.unfaehig_industries.tooz.araction.views

import android.content.Context
import android.view.View

abstract class RadialMenuButton(context: Context) : View(context) {
    abstract fun onHover(durationInSeconds: Long = 3L)
    abstract fun onHoverLeave()
}
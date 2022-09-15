package tech.unfaehig_industries.tooz.araction

import androidx.fragment.app.Fragment

open class BaseToozifierFragment : Fragment() {

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val SENSOR_EVENT = "Sensor event:"
        const val BUTTON_EVENT = "Button event:"
    }

    protected val toozifier = BaseApplication.getBaseApplication().toozifier

    protected val sensorReadingInterval = 150
}
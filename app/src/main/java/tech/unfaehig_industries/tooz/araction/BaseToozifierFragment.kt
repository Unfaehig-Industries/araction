package tech.unfaehig_industries.tooz.araction

import androidx.fragment.app.Fragment
import tech.unfaehig_industries.tooz.araction.positional_tracking.CursorEventManager
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

abstract class BaseToozifierFragment : Fragment() {

    protected val toozifier = BaseApplication.getBaseApplication().toozifier
    protected lateinit var cursorEventManager : CursorEventManager
    protected abstract val layout: BaseToozifierLayout

    protected abstract val dataSensors: Array<Sensor>
    protected val sensorReadingInterval = 150

    override fun onResume() {
        super.onResume()
        //registerToozer()
        cursorEventManager.start()
        layout.resumeJob()
    }

    override fun onPause() {
        super.onPause()
        //deregisterToozer()
        cursorEventManager.stop()
        layout.pauseJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        layout.cancelJob()
    }

    private fun registerToozer() {
        toozifier.addListener(sensorDataListener)
        toozifier.addListener(buttonEventListener)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private fun deregisterToozer() {
        dataSensors.forEach { sensor ->
            toozifier.deregisterFromSensorData(sensor)
        }
        toozifier.removeListener(sensorDataListener)
        toozifier.removeListener(buttonEventListener)
        toozifier.deregister()
    }

    protected abstract val registrationListener: RegistrationListener
    protected abstract val sensorDataListener : SensorDataListener
    protected abstract val buttonEventListener : ButtonEventListener

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val SENSOR_EVENT = "Sensor event:"
        const val BUTTON_EVENT = "Button event:"
    }
}
package tech.unfaehig_industries.tooz.tooz_base_views

import androidx.fragment.app.Fragment
import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

abstract class BaseToozifierFragment : Fragment() {

    protected val toozifier = BaseApplication.getBaseApplication().toozifier
    protected lateinit var trackingEventManager: TrackingEventManager
    protected abstract val layout: BaseToozifierLayout

    protected abstract val dataSensors: Array<Sensor>
    protected val sensorReadingInterval = 150

    override fun onResume() {
        super.onResume()
        registerToozer()
        trackingEventManager.start()
        layout.resumeJob()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
        trackingEventManager.stop()
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

    protected open val registrationListener: RegistrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
        }

        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("$TOOZ_EVENT onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
        }
    }
    protected open val sensorDataListener : SensorDataListener = object : SensorDataListener {

        override fun onSensorDataRegistered() {
            Timber.d("$SENSOR_EVENT onSensorDataRegistered")
        }

        override fun onSensorDataDeregistered(sensor: Sensor) {
            Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
        }

        override fun onSensorDataReceived(sensorReading: SensorReading) {
            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")
        }

        override fun onSensorError(sensor: Sensor, errorCause: ErrorCause) {
            Timber.d("$SENSOR_EVENT onSensorError sensor: $sensor errorCause: $errorCause")
        }

        override fun onSensorListReceived(sensors: List<Sensor>) {
            Timber.d("$SENSOR_EVENT onSensorListReceived sensors:\n\n")
            sensors.forEach {
                Timber.d("$SENSOR_EVENT \tsensor: $it")
            }
        }
    }
    protected open val buttonEventListener : ButtonEventListener = object : ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
        }
    }

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val SENSOR_EVENT = "Sensor event:"
        const val BUTTON_EVENT = "Button event:"
    }
}
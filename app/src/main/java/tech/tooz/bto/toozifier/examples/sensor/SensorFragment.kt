package tech.tooz.bto.toozifier.examples.sensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentSensorBinding
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class SensorFragment : BaseToozifierFragment(), SensorDataListener {

    private var binding: FragmentSensorBinding? = null

    private val registrationListener = object : RegistrationListener {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSensorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToozer()
    }

    override fun onSensorDataDeregistered(sensor: Sensor) {
        Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
    }

    override fun onSensorDataReceived(sensorReading: ToozServiceMessage.Sensor.SensorReading) {
        Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading: $sensorReading")
    }

    override fun onSensorDataRegistered() {
        Timber.d("$SENSOR_EVENT onSensorDataRegistered")
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

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        deregisterToozer()
    }

    private fun registerToozer() {
        toozifier.addListener(this)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private fun deregisterToozer() {
        toozifier.deregister()
        toozifier.removeListener(this)
    }
}
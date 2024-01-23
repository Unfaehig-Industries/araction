package tech.unfaehig_industries.tooz.araction.frame_rate

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tech.unfaehig_industries.tooz.araction.BaseApplication
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment.Companion.BUTTON_EVENT
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment.Companion.SENSOR_EVENT
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment.Companion.TOOZ_EVENT
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.databinding.DirectionFragmentBinding
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class FrameRateFragment : Fragment() {

    private val toozifier = BaseApplication.getBaseApplication().toozifier

    // The binding contains the views that are part of this fragment
    private var _binding: DirectionFragmentBinding? = null
    private val binding get() = _binding!!

    private val layout: FrameRateLayout = FrameRateLayout(toozifier)

    private val dataSensors: Array<Sensor> = arrayOf(Sensor.geomagRotation)
    var interval = 250

    override fun onResume() {
        super.onResume()
        registerToozer()
        layout.resumeJob()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
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

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")

            dataSensors.forEach { sensor ->
                toozifier.registerForSensorData(
                    Pair(sensor, interval)
                )
            }
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

    private val sensorDataListener = object : SensorDataListener {

        override fun onSensorDataRegistered() {
            Timber.d("$SENSOR_EVENT onSensorDataRegistered")
        }

        override fun onSensorDataDeregistered(sensor: Sensor) {
            Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
        }

        override fun onSensorDataReceived(sensorReading: SensorReading) {
            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")

            when(sensorReading.name) {
                "geomagRotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.GeomagRotation? = sensorReading.reading.geomagRotation
                    Timber.d("Frame rate: ${sensorDataReading?.x}")
                    layout.updateFrame()
                }
            }
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

    private val buttonEventListener = object: ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
            interval -= 5
            layout.delay -= 5

            layout.setInterval()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DirectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout.inflateView(requireContext())
    }
}
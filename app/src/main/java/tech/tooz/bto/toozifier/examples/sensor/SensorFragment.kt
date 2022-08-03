package tech.tooz.bto.toozifier.examples.sensor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentSensorBinding
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.common.ToozServiceMessage
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class SensorFragment : BaseToozifierFragment() {

    companion object {
        private const val SENSOR_READING_INTERVAL = 100
    }

    private val sensor = Sensor.acceleration

    // The binding contains the views that are part of this fragment
    private var binding: FragmentSensorBinding? = null

    // These are views that are displayed in the glasses
    private var sensorDataView: View? = null
    // These are the text fields that are displayed in this view
    private var xText : TextView? = null
    private var yText : TextView? = null
    private var zText : TextView? = null

    override fun onResume() {
        super.onResume()
        registerToozer()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
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
        toozifier.deregister()
        toozifier.removeListener(sensorDataListener)
        toozifier.removeListener(buttonEventListener)
        toozifier.deregisterFromSensorData(Sensor.acceleration)
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
            toozifier.registerForSensorData(
                Pair(sensor, SENSOR_READING_INTERVAL)
            )
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

        override fun onSensorDataDeregistered(sensor: Sensor) {
            Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
        }

        override fun onSensorDataReceived(sensorReading: ToozServiceMessage.Sensor.SensorReading) {
            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")
            sensorReading.reading.acceleration?.apply {
                Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: $x $y $z")

                xText?.text = x.toString()
                yText?.text = y.toString()
                zText?.text = z.toString()

                toozifier.updateCard(
                    binding!!.recyclerViewScrollByHeadMotion,
                    sensorDataView!!,
                    Constants.FRAME_TIME_TO_LIVE_FOREVER
                )
            }
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
    }

    private val buttonEventListener = object : ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSensorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        inflateSensorView()
    }

    private fun setupRecyclerView() {
        binding?.recyclerViewScrollByHeadMotion?.let {
            val layoutManager = LinearLayoutManager(requireContext())
            it.layoutManager = layoutManager
            it.adapter = ScrollByHeadMotionAdapter()

            val dividerItemDecoration = DividerItemDecoration(
                it.context,
                layoutManager.orientation
            )
            it.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun inflateSensorView() {
        sensorDataView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_sensor, null, false)
        xText = sensorDataView?.findViewById(R.id.sensor_x)
        yText = sensorDataView?.findViewById(R.id.sensor_y)
        zText = sensorDataView?.findViewById(R.id.sensor_z)
    }
}
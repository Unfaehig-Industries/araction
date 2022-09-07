package tech.tooz.bto.toozifier.examples.sensor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentSensorBinding
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class SensorFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: FragmentSensorBinding? = null
    private val binding get() = _binding!!

    private val sensorData: SensorData = SensorData(toozifier)
    private var adapter: ScrollByHeadMotionAdapter? = null

    private var dataSensors: Array<Sensor> = arrayOf(Sensor.acceleration, Sensor.gyroscope, Sensor.rotation, Sensor.gameRotation, Sensor.geomagRotation, Sensor.light, Sensor.temperature, Sensor.magneticField)
    private var activeSensor = 0
    private val SENSOR_READING_INTERVAL = 100

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
                Pair(dataSensors[activeSensor], SENSOR_READING_INTERVAL)
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

        override fun onSensorDataReceived(sensorReading: SensorReading) {
            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")

            when(sensorReading.name) {
                "acceleration" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.Acceleration? = sensorReading.reading.acceleration
                    tripleSensorData(sensorReading.name, sensorDataReading?.x, sensorDataReading?.y, sensorDataReading?.z)
                }
                "gyroscope" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.Gyroscope? = sensorReading.reading.gyroscope
                    tripleSensorData(sensorReading.name, sensorDataReading?.x, sensorDataReading?.y, sensorDataReading?.z)
                }
                "rotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.Rotation? = sensorReading.reading.rotation
                    tripleSensorData(sensorReading.name, sensorDataReading?.x, sensorDataReading?.y, sensorDataReading?.z)
                }
                "gameRotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.GameRotation? = sensorReading.reading.gameRotation
                    tripleSensorData(sensorReading.name, sensorDataReading?.x, sensorDataReading?.y, sensorDataReading?.z)
                }
                "geomagRotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.GeomagRotation? = sensorReading.reading.geomagRotation
                    tripleSensorData(sensorReading.name, sensorDataReading?.x, sensorDataReading?.y, sensorDataReading?.z)
                }
                "light" -> {
                    val sensorDataReading: Double? = sensorReading.reading.light
                    singleSensorData(sensorReading.name, sensorDataReading)
                }
                "temperature" -> {
                    val sensorDataReading: Double? = sensorReading.reading.temperature
                    singleSensorData(sensorReading.name, sensorDataReading)
                }
                "magneticField" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.MagneticField? = sensorReading.reading.magneticField
                    tripleSensorData(sensorReading.name, sensorDataReading?.x, sensorDataReading?.y, sensorDataReading?.z)
                }
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
            cycleActiveSensor()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScrollByHeadMotionAdapter()
        setupRecyclerView()

        // Get the view which is supposed to be shown on the glasses
        sensorData.inflateSensorView(requireContext())
    }

    private fun setupRecyclerView() {
        binding.recyclerViewScrollByHeadMotion.let {
            val layoutManager = LinearLayoutManager(requireContext())
            it.layoutManager = layoutManager
            it.adapter = adapter

            val dividerItemDecoration = DividerItemDecoration(
                it.context,
                layoutManager.orientation
            )
            it.addItemDecoration(dividerItemDecoration)
        }
    }

    private fun cycleActiveSensor() {
        sensorData.sendEmptyFrame()
        toozifier.deregisterFromSensorData(dataSensors[activeSensor])

        activeSensor += 1
        if (activeSensor >= dataSensors.size) {
            activeSensor = 0
        }

        Timber.d("Active Sensor: ${dataSensors[activeSensor]}")
        toozifier.registerForSensorData(
            Pair(dataSensors[activeSensor], SENSOR_READING_INTERVAL)
        )
    }

    fun tripleSensorData (sensor: String, x: Double?, y: Double?, z: Double?) {
        Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of $sensor: $x $y $z")
        adapter?.createItem("$sensor: $x $y $z")
        sensorData.sendFrame(sensor, x, y, z)
    }

    fun singleSensorData (sensor: String, x: Double?) {
        Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of $sensor: $x")
        adapter?.createItem("$sensor: $x")
        sensorData.sendFrame(sensor, x)
    }
}
package tech.unfaehig_industries.tooz.araction.sensor_data

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.databinding.SensorDataFragmentBinding
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener


class SensorDataFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: SensorDataFragmentBinding? = null
    private val binding get() = _binding!!

    private val sensorDataLayout: SensorDataLayout = SensorDataLayout(toozifier)
    private var adapter: LogSensorDataAdapter? = null

    private val dataSensors: Array<Sensor> = arrayOf(Sensor.acceleration, Sensor.gyroscope, Sensor.rotation, Sensor.gameRotation, Sensor.geomagRotation, Sensor.light, Sensor.temperature, Sensor.magneticField)
    private var activeSensor = 0

    private var lastTouched = 0
    private val TOUCH_COOLDOWN = 10000

    override fun onResume() {
        super.onResume()
        registerToozer()
        sensorDataLayout.resumeJob()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
        sensorDataLayout.pauseJob()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorDataLayout.cancelJob()
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
                Pair(dataSensors[activeSensor], sensorReadingInterval)
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

        override fun onSensorDataRegistered() {
            Timber.d("$SENSOR_EVENT onSensorDataRegistered")
        }

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
        _binding = SensorDataFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = LogSensorDataAdapter()
        setupRecyclerView()

        // Get the view which is supposed to be shown on the glasses
        sensorDataLayout.inflateSensorView(requireContext())
        binding.recyclerViewScrollByHeadMotion.setOnTouchListener {_, _ ->
            lastTouched = System.currentTimeMillis().toInt()
            false
        }
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
        sensorDataLayout.sendEmptyFrame()
        toozifier.deregisterFromSensorData(dataSensors[activeSensor])

        activeSensor += 1
        if (activeSensor >= dataSensors.size) {
            activeSensor = 0
        }

        Timber.d("Active Sensor: ${dataSensors[activeSensor]}")
        toozifier.registerForSensorData(
            Pair(dataSensors[activeSensor], sensorReadingInterval)
        )
    }

    fun tripleSensorData (sensor: String, x: Double?, y: Double?, z: Double?) {
        Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of $sensor: $x $y $z")
        sensorDataLayout.sendFrame(sensor, x, y, z)
        adapter?.createItem("$sensor: $x $y $z")

        if((System.currentTimeMillis().toInt() - lastTouched) > TOUCH_COOLDOWN) {
            adapter?.itemCount
            ?.let {
                binding.recyclerViewScrollByHeadMotion.smoothScrollToPosition(it)
            }
        }
    }

    fun singleSensorData (sensor: String, x: Double?) {
        Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of $sensor: $x")
        sensorDataLayout.sendFrame(sensor, x)
        adapter?.createItem("$sensor: $x")
    }
}
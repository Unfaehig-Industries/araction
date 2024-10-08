package tech.unfaehig_industries.tooz.araction.sensor_data

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Pair as AndroidPair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import tech.unfaehig_industries.tooz.tooz_base_application.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.databinding.SensorDataFragmentBinding
import tech.unfaehig_industries.tooz.phone_tracking.SensorDataCallback
import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
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

    override val layout: SensorDataLayout = SensorDataLayout(toozifier)
    private var adapter: LogSensorDataAdapter? = null

    override val dataSensors: Array<Sensor> = arrayOf(Sensor.acceleration, Sensor.gyroscope, Sensor.rotation, Sensor.gameRotation, Sensor.geomagRotation, Sensor.light, Sensor.temperature, Sensor.magneticField)
    private var activeSensor = 0

    private var lastTouched = 0
    private val TOUCH_COOLDOWN = 10000

    override val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")

            toozifier.registerForSensorData(
                AndroidPair(dataSensors[activeSensor], sensorReadingInterval)
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

    override val sensorDataListener = object : SensorDataListener {

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
                    val sensorDataReading: ToozServiceMessage.Sensor.Acceleration = sensorReading.reading.acceleration!!
                    sensorDataReading.x?.let { x ->
                        sensorDataReading.y?.let { y ->
                            sensorDataReading.z?.let { z ->
                                sendSensorData(SafeSensorReading(sensorReading.name, x, y, z))
                            }
                        }
                    }
                }
                "gyroscope" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.Gyroscope = sensorReading.reading.gyroscope!!
                    sensorDataReading.x?.let { x ->
                        sensorDataReading.y?.let { y ->
                            sensorDataReading.z?.let { z ->
                                sendSensorData(SafeSensorReading(sensorReading.name, x, y, z))
                            }
                        }
                    }
                }
                "rotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.Rotation = sensorReading.reading.rotation!!
                    sensorDataReading.w?.let { w ->
                        sensorDataReading.x?.let { x ->
                            sensorDataReading.y?.let { y ->
                                sensorDataReading.z?.let { z ->
                                    sendSensorData(SafeSensorReading(sensorReading.name, w, x, y, z))
                                }
                            }
                        }
                    }
                }
                "gameRotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.GameRotation = sensorReading.reading.gameRotation!!
                    sensorDataReading.w?.let { w ->
                        sensorDataReading.x?.let { x ->
                            sensorDataReading.y?.let { y ->
                                sensorDataReading.z?.let { z ->
                                    sendSensorData(SafeSensorReading(sensorReading.name, w, x, y, z))
                                }
                            }
                        }
                    }
                }
                "geomagRotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.GeomagRotation = sensorReading.reading.geomagRotation!!
                    sensorDataReading.w?.let { w ->
                        sensorDataReading.x?.let { x ->
                            sensorDataReading.y?.let { y ->
                                sensorDataReading.z?.let { z ->
                                    sendSensorData(SafeSensorReading(sensorReading.name, w, x, y, z))
                                }
                            }
                        }
                    }
                }
                "light" -> {
                    val sensorDataReading: Double = sensorReading.reading.light!!
                    sendSensorData(SafeSensorReading(sensorReading.name, sensorDataReading))
                }
                "temperature" -> {
                    val sensorDataReading: Double = sensorReading.reading.temperature!!
                    sendSensorData(SafeSensorReading(sensorReading.name, sensorDataReading))
                }
                "magneticField" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.MagneticField = sensorReading.reading.magneticField!!
                    sensorDataReading.x?.let { x ->
                        sensorDataReading.y?.let { y ->
                            sensorDataReading.z?.let { z ->
                                sendSensorData(SafeSensorReading(sensorReading.name, x, y, z))
                            }
                        }
                    }
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

    override val buttonEventListener = object : ButtonEventListener {
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
        layout.inflateView(requireContext())
        binding.sensorDataRecyclerView.setOnTouchListener {_, _ ->
            lastTouched = System.currentTimeMillis().toInt()
            false
        }

        // Initialize phone positional tracking
        trackingEventManager =
            TrackingEventManager(object : SensorDataCallback {
                override fun onCursorUpdate(angle: Double, dist: Double) {
                }

                override fun onAccuracyChanged(accuracy: Int) {
                    // Handle accuracy change
                }
            }, activity)
    }

    private fun setupRecyclerView() {
        binding.sensorDataRecyclerView.let {
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
        layout.resetFrame()
        toozifier.deregisterFromSensorData(dataSensors[activeSensor])

        activeSensor += 1
        if (activeSensor >= dataSensors.size) {
            activeSensor = 0
        }

        Timber.d("Active Sensor: ${dataSensors[activeSensor]}")
        toozifier.registerForSensorData(
            AndroidPair(dataSensors[activeSensor], sensorReadingInterval)
        )
    }

    fun sendSensorData (reading: SafeSensorReading) {

        Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of ".plus(reading.dataString()))
        layout.updateFrame(reading)
        adapter?.createItem(reading.dataString())

        if((System.currentTimeMillis().toInt() - lastTouched) > TOUCH_COOLDOWN) {
            adapter?.itemCount
                ?.let {
                    binding.sensorDataRecyclerView.smoothScrollToPosition(it)
                }
        }
    }
}
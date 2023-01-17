package tech.unfaehig_industries.tooz.araction.direction

//import tooz.bto.toozifier.sensors.Sensor
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.databinding.DirectionFragmentBinding
import timber.log.Timber
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import java.util.*


class DirectionFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: DirectionFragmentBinding? = null
    private val binding get() = _binding!!

//    private val dataSensors: Array<Sensor> = arrayOf(Sensor.geomagRotation)
//    private val sensorReadingInterval = 100

    private var zeroRotation: FloatArray = floatArrayOf(0f, 1f, 0f, 0f)
    private var currentRotation: FloatArray = floatArrayOf(0f, 1f, 0f, 0f)
    private var resetZeroPosition: Boolean = false

    private lateinit var sensorManager : SensorManager
    private var sensor : Sensor? = null
    private lateinit var rotationListener: SensorEventListener


//    private val sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)



    override fun onResume() {
        super.onResume()
        registerToozer()
        sensorManager.registerListener(rotationListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
        sensorManager.unregisterListener(rotationListener)
    }

    private fun registerToozer() {
//        toozifier.addListener(sensorDataListener)
        toozifier.addListener(buttonEventListener)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )


    }

    private fun deregisterToozer() {
        toozifier.deregister()
//        toozifier.removeListener(sensorDataListener)
        toozifier.removeListener(buttonEventListener)
//        toozifier.deregisterFromSensorData(Sensor.acceleration)
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")

//            dataSensors.forEach { sensor ->
//                toozifier.registerForSensorData(
//                    Pair(sensor, sensorReadingInterval)
//                )
//            }
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

//    private val sensorDataListener = object : SensorDataListener {
//
//        override fun onSensorDataRegistered() {
//            Timber.d("$SENSOR_EVENT onSensorDataRegistered")
//        }
//
//        override fun onSensorDataDeregistered(sensor: Sensor) {
//            Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
//        }
//
//        override fun onSensorDataReceived(sensorReading: SensorReading) {
//            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")
//
//            when(sensorReading.name) {
//                "geomagRotation" -> {
//                    val sensorDataReading: ToozServiceMessage.Sensor.GeomagRotation? = sensorReading.reading.geomagRotation
//
//                    sensorDataReading?.let {
//                        if (resetZeroPosition) {
//                            zeroPosition = RotationalPosition(it.x!!, it.y!!, it.z!!)
//                            resetZeroPosition = false
//                        }
//
//                        currentPosition = RotationalPosition(it.x!!, it.y!!, it.z!!)
//                        // TODO fix angle = NAN
//                        Timber.d("NEW ANGLE: ${currentPosition.calculateDirection(zeroPosition)}")
//                    }
//                }
//            }
//        }
//
//        override fun onSensorError(sensor: Sensor, errorCause: ErrorCause) {
//            Timber.d("$SENSOR_EVENT onSensorError sensor: $sensor errorCause: $errorCause")
//        }
//
//        override fun onSensorListReceived(sensors: List<Sensor>) {
//            Timber.d("$SENSOR_EVENT onSensorListReceived sensors:\n\n")
//            sensors.forEach {
//                Timber.d("$SENSOR_EVENT \tsensor: $it")
//            }
//        }
//    }

    private val buttonEventListener = object : ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
            resetZeroPosition = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DirectionFragmentBinding.inflate(inflater, container, false)


        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

        val list: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (element in list) {
            Timber.d("SENSOR DEBUG: $element")
        }

        Timber.d("SENSOR DEBUG: Sensor created")

        rotationListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
//                Timber.d("SENSOR DEBUG: Sensor changed")
                // we received a sensor event. it is a good practice to check
                // that we received the proper event
                if (event.sensor.type == TYPE_GAME_ROTATION_VECTOR) {
                    val values = event.values
//                    Timber.d("SENSOR DEBUG: Sensor reading $values")
                    // convert the rotation-vector to a 4x4 matrix. the matrix
                    // is interpreted by Open GL as the inverse of the
                    // rotation-vector, which is what we want.
                    SensorManager.getQuaternionFromVector(currentRotation, values)
//                    Timber.d("SENSOR DEBUG: $currentRotation")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle accuracy change here
            }
        }

        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetZeroPosition = true
    }
}
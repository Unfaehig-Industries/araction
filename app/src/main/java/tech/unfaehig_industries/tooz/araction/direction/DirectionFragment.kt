package tech.unfaehig_industries.tooz.araction.direction

//import tooz.bto.toozifier.sensors.Sensor
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.databinding.DirectionFragmentBinding
import timber.log.Timber
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import java.util.*
import kotlin.math.acos
import kotlin.math.sqrt


class DirectionFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: DirectionFragmentBinding? = null
    private val binding get() = _binding!!

//    private val dataSensors: Array<Sensor> = arrayOf(Sensor.geomagRotation)
//    private val sensorReadingInterval = 100

    private var zeroFacing: DoubleArray = DoubleArray(3)
    private var currentFacing: DoubleArray = DoubleArray(3)
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

                    val currentRotation = FloatArray(9)

                    SensorManager.getRotationMatrixFromVector (currentRotation, values)

                    currentFacing = rotateVectorByMatrix((currentRotation.map { it.toDouble() }).toDoubleArray(), doubleArrayOf(0.0, 1.0, 0.0))
//                    val currentUp = rotateVectorByMatrix((currentRotation.map { it.toDouble() }).toDoubleArray(), doubleArrayOf(0.0, 0.0, 1.0))

                    if (resetZeroPosition) {
                        resetZeroPosition = false
                        zeroFacing = currentFacing
                    }

                    val directionVector = doubleArrayOf(
                        zeroFacing[0] - currentFacing[0],
                        zeroFacing[1] - currentFacing[1],
                        zeroFacing[2] - currentFacing[2]
                    )

                    val angle = angleBetweenVectors(directionVector, doubleArrayOf(0.0, 0.0, 1.0))
                    val dist = sqrt(directionVector.sumOf { it * it })

//                    Timber.d("SENSOR DEBUG: x ${directionVector[0]}, y ${directionVector[1]}, z ${directionVector[2]}")
                    Timber.d("angle: $angle, distance: $dist")
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

    fun calcPointerRotation(vector: FloatArray, quaternion: FloatArray) : FloatArray? {

        require(vector.size == 3 && quaternion.size == 4) { "Matrix and vector must be 3x3 and 3D respectively" }

        val num = quaternion[1] * 2.0
        val num2 = quaternion[2] * 2.0
        val num3 = quaternion[3] * 2.0
        val num4 = quaternion[1] * num
        val num5 = quaternion[2] * num2
        val num6 = quaternion[3] * num3
        val num7 = quaternion[1] * num2
        val num8 = quaternion[1] * num3
        val num9 = quaternion[2] * num3
        val num10 = quaternion[0] * num
        val num11 = quaternion[0] * num2
        val num12 = quaternion[0] * num3
        return floatArrayOf(
            ((1.0 - (num5 + num6)) * vector[0] + (num7 - num12) * vector[1] + (num8 + num11) * vector[2]).toFloat(),
            ((num7 + num12) * vector[0] + (1.0 - (num4 + num6)) * vector[1] + (num9 - num10) * vector[2]).toFloat(),
            ((num8 - num11) * vector[0] + (num9 + num10) * vector[1] + (1.0 - (num4 + num5)) * vector[2]).toFloat()
        )
    }

    fun rotateVectorByMatrix(inputMatrix: DoubleArray, vector: DoubleArray): DoubleArray {
        // check that the matrix and vector are conformable for matrix multiplication

        require(inputMatrix.size == 9 && vector.size == 3) { "Matrix and vector must be 3x3 and 3D respectively" }

        val matrix = arrayOf(
            doubleArrayOf(inputMatrix[0], inputMatrix[1], inputMatrix[2]),
            doubleArrayOf(inputMatrix[3], inputMatrix[4], inputMatrix[5]),
            doubleArrayOf(inputMatrix[6], inputMatrix[7], inputMatrix[8])
        )

        // perform the matrix multiplication
        val result = DoubleArray(3)
        for (i in 0..2) {
            for (j in 0..2) {
                result[i] += matrix[i][j] * vector[j]
            }
        }
        return result
    }

    fun angleBetweenVectors(vector1: DoubleArray, vector2: DoubleArray): Double {
        require(vector1.size == 3 && vector2.size == 3) { "Vectors must be 3D" }
        val dotProduct = vector1.zip(vector2) { a, b -> a * b }.sum()
        val magnitude1 = sqrt(vector1.sumOf { it * it })
        val magnitude2 = sqrt(vector2.sumOf { it * it })
        return acos(dotProduct / (magnitude1 * magnitude2))
    }



}


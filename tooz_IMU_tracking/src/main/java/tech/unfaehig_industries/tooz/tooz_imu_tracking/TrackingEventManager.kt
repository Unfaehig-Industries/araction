package tech.unfaehig_industries.tooz.tooz_imu_tracking

import android.app.Activity
import android.content.ContentValues
import android.content.Context
//import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR
import android.hardware.Sensor.TYPE_ROTATION_VECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import timber.log.Timber
import kotlin.math.acos
import kotlin.math.sqrt

import tooz.bto.common.ToozServiceMessage
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.Toozifier
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener
import android.util.Pair as AndroidPair

class TrackingEventManager {
    private lateinit var callback: SensorDataCallback
    private var toozifier: Toozifier

    private var zeroFacing: DoubleArray = DoubleArray(3)
    private var currentFacing: DoubleArray = DoubleArray(3)
    private var resetZeroPosition: Boolean = false

    private val distanceScaler: Float = 200f
    private var distanceSensitivity: Float = 1f

    private val dataSensors: Array<Sensor> = arrayOf(Sensor.acceleration, Sensor.gyroscope, Sensor.rotation, Sensor.gameRotation, Sensor.geomagRotation, Sensor.light, Sensor.temperature, Sensor.magneticField)
    private var activeSensor = 0
    private val sensorReadingInterval = 40

    constructor(callback: SensorDataCallback, toozifier: Toozifier) {
        this.callback = callback
        this.toozifier = toozifier
        this.toozifier.registerForSensorData(
            AndroidPair(dataSensors[activeSensor], sensorReadingInterval)
        )
        this.toozifier.addListener(sensorDataListener)

        this.callback = callback
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

                "gameRotation" , "geomagRotation" -> {
                    val sensorDataReading: ToozServiceMessage.Sensor.GameRotation = sensorReading.reading.gameRotation!!
                    sensorDataReading.w?.let { w ->
                        sensorDataReading.x?.let { x ->
                            sensorDataReading.y?.let { y ->
                                sensorDataReading.z?.let { z ->
                                    val values = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat())

                                    val currentRotation = FloatArray(9)

                                    SensorManager.getRotationMatrixFromVector (currentRotation, values)

                                    val vectorUp = doubleArrayOf(0.0, 0.0, 1.0)
                                    val vectorDown = doubleArrayOf(0.0, 0.0, -1.0)

                                    currentFacing = rotateVectorByMatrix((currentRotation.map { it.toDouble() }).toDoubleArray(), doubleArrayOf(0.0, 1.0, 0.0))

                                    if (currentFacing[0] == 0.0 &&
                                        currentFacing[1] == 1.0 &&
                                        currentFacing[2] == 0.0) {

                                        return
                                    }

                                    if(resetZeroPosition) {
                                        zeroFacing = currentFacing
                                        resetZeroPosition = false
                                    }

                                    val vectorRight = doubleArrayOf(
                                        currentFacing[1] * vectorUp[2] - currentFacing[2] * vectorUp[1],
                                        currentFacing[2] * vectorUp[0] - currentFacing[0] * vectorUp[2],
                                        currentFacing[0] * vectorUp[1] - currentFacing[1] * vectorUp[0]
                                    )

                                    val directionVector = doubleArrayOf(
                                        zeroFacing[0] - currentFacing[0],
                                        zeroFacing[1] - currentFacing[1],
                                        zeroFacing[2] - currentFacing[2]
                                    )

                                    val angle = if (angleBetweenVectors(vectorRight, directionVector) < Math.PI/2) {
                                        2 * Math.PI - angleBetweenVectors(directionVector, vectorDown)
                                    } else {
                                        angleBetweenVectors(directionVector, vectorDown)
                                    }

                                    val dist = sqrt(directionVector.sumOf { it * it }) * distanceScaler * distanceSensitivity

                                    // angle returns the angle in radians clockwise with the circle origin on top (12 o'clock)
                                    callback.onCursorUpdate(angle, dist)
                                }
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

    private fun rotateVectorByMatrix(inputMatrix: DoubleArray, vector: DoubleArray): DoubleArray {
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

    private fun angleBetweenVectors(vector1: DoubleArray, vector2: DoubleArray): Double {
        require(vector1.size == 3 && vector2.size == 3) { "Vectors must be 3D" }
        val dotProduct = vector1.zip(vector2) { a, b -> a * b }.sum()
        val magnitude1 = sqrt(vector1.sumOf { it * it })
        val magnitude2 = sqrt(vector2.sumOf { it * it })
        return acos(dotProduct / (magnitude1 * magnitude2))
    }

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
        const val SENSOR_EVENT = "Sensor event:"
    }
}

interface SensorDataCallback {
    fun onCursorUpdate(angle: Double, dist: Double)
}
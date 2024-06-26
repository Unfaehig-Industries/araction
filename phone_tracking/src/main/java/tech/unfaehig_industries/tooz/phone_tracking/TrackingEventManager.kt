package tech.unfaehig_industries.tooz.phone_tracking

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR
import android.hardware.Sensor.TYPE_ROTATION_VECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.acos
import kotlin.math.sqrt

class TrackingEventManager : SensorEventListener {
    private var sensorManager: SensorManager
    private var sensor: Sensor
    private var callback: SensorDataCallback

    private var zeroFacing: DoubleArray = DoubleArray(3)
    private var currentFacing: DoubleArray = DoubleArray(3)
    private var resetZeroPosition: Boolean = false

    private val distanceScaler: Float = 200f
    private var distanceSensitivity: Float = 1f

    constructor(callback: SensorDataCallback, activity: Activity?) {
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(TYPE_GAME_ROTATION_VECTOR)!!
        this.callback = callback
    }

    constructor(callback: SensorDataCallback, activity: Activity?, sensitivity: Float) {
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(TYPE_GAME_ROTATION_VECTOR)!!
        this.callback = callback
        this.distanceSensitivity = sensitivity
    }

    override fun onSensorChanged(event: SensorEvent) {

        require(
            event.sensor.type == TYPE_GAME_ROTATION_VECTOR || event.sensor.type == TYPE_ROTATION_VECTOR
        ) { "Wrong sensor type received!" }

        val values = event.values

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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        callback.onAccuracyChanged(accuracy)
    }

    fun start() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        resetZeroPosition = true
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun resetZeroPosition() {
        resetZeroPosition = true
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
}

interface SensorDataCallback {
    fun onCursorUpdate(angle: Double, dist: Double)
    fun onAccuracyChanged(accuracy: Int)
}
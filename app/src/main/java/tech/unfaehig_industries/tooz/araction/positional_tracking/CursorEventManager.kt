import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR
import android.hardware.Sensor.TYPE_ROTATION_VECTOR
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import timber.log.Timber
import kotlin.math.acos
import kotlin.math.sqrt


class CursorEventManager(callback: SensorDataCallback, activity: Activity?) :
    SensorEventListener {
    private val sensorManager: SensorManager
    private val sensor: Sensor
    private val callback: SensorDataCallback

    private var zeroFacing: DoubleArray = DoubleArray(3)
    private var currentFacing: DoubleArray = DoubleArray(3)
    private var resetZeroPosition: Boolean = false

    init {
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        this.callback = callback
    }

    fun start() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        require(
            event.sensor.type == TYPE_GAME_ROTATION_VECTOR ||
            event.sensor.type == TYPE_ROTATION_VECTOR
        ) { "Wrong sensor type received!" }

        val values = event.values

        val currentRotation = FloatArray(9)

        SensorManager.getRotationMatrixFromVector (currentRotation, values)

        val vectorUp = doubleArrayOf(0.0, 0.0, 1.0)
        val vectorDown = doubleArrayOf(0.0, 0.0, -1.0)

        currentFacing = rotateVectorByMatrix((currentRotation.map { it.toDouble() }).toDoubleArray(), doubleArrayOf(0.0, 1.0, 0.0))
//        val currentUp = rotateVectorByMatrix((currentRotation.map { it.toDouble() }).toDoubleArray(), doubleArrayOf(0.0, 0.0, 1.0))

        val vectorRight = doubleArrayOf(
            currentFacing[1] * vectorUp[2] - currentFacing[2] * vectorUp[1],
            currentFacing[2] * vectorUp[0] - currentFacing[0] * vectorUp[2],
            currentFacing[0] * vectorUp[1] - currentFacing[1] * vectorUp[0]
        )

        if (resetZeroPosition &&
            currentFacing[0] != 0.0 &&
            currentFacing[1] != 1.0 &&
            currentFacing[2] != 0.0) {

            resetZeroPosition = false
            zeroFacing = currentFacing
        }

        val directionVector = doubleArrayOf(
            zeroFacing[0] - currentFacing[0],
            zeroFacing[1] - currentFacing[1],
            zeroFacing[2] - currentFacing[2]
        )



        var angle = 0.0;

        if (angleBetweenVectors(vectorRight, directionVector) < Math.PI/2) {
            angle = 2 * Math.PI - angleBetweenVectors(directionVector, vectorDown)
        }
        else {
            angle = angleBetweenVectors(directionVector, vectorDown)
        }


        val dist = sqrt(directionVector.sumOf { it * it })

        // angle returns the angle in radians clockwise with the circle origin on top (12 o'clock)
        callback.onCursorUpdate(angle, dist)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        callback.onAccuracyChanged(accuracy)
    }

    interface SensorDataCallback {
        fun onCursorUpdate(angle: Double, dist: Double)
        fun onAccuracyChanged(accuracy: Int)
    }

    fun resetZeroPosition() {
        resetZeroPosition = true
    }
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
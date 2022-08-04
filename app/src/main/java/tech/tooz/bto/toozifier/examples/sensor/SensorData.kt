package tech.tooz.bto.toozifier.examples.sensor

import android.content.Context
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.LayoutSensorBinding
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage
import tooz.bto.toozifier.Toozifier
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class SensorData (private val toozifier: Toozifier) {

    private val sensor = Sensor.acceleration
    private val SENSOR_READING_INTERVAL = 100
    // These are views that are displayed in the glasses
    private var sensorDataView: View? = null
    // These are the text fields that are displayed in this view
    private var xText : TextView? = null
    private var yText : TextView? = null
    private var zText : TextView? = null

    val listener = object : SensorDataListener {

        override fun onSensorDataDeregistered(sensor: Sensor) {
            Timber.d("${BaseToozifierFragment.SENSOR_EVENT} onSensorDataDeregistered sensor: $sensor")
        }

        override fun onSensorDataReceived(sensorReading: ToozServiceMessage.Sensor.SensorReading) {
            Timber.d("${BaseToozifierFragment.SENSOR_EVENT} onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")

            sensorReading.reading.acceleration?.apply {
                Timber.d("${BaseToozifierFragment.SENSOR_EVENT} onSensorDataReceived sensorReading of sensor: $x $y $z")

                xText?.text = x.toString()
                yText?.text = y.toString()
                zText?.text = z.toString()

                sensorDataView?.run {
                    Timber.d(sensorDataView.toString())
                    toozifier.sendFrame(this)
                }
            }
        }

        override fun onSensorDataRegistered() {
            Timber.d("${BaseToozifierFragment.SENSOR_EVENT} onSensorDataRegistered")
        }

        override fun onSensorError(sensor: Sensor, errorCause: ErrorCause) {
            Timber.d("${BaseToozifierFragment.SENSOR_EVENT} onSensorError sensor: $sensor errorCause: $errorCause")
        }

        override fun onSensorListReceived(sensors: List<Sensor>) {
            Timber.d("${BaseToozifierFragment.SENSOR_EVENT} onSensorListReceived sensors:\n\n")
            sensors.forEach {
                Timber.d("${BaseToozifierFragment.SENSOR_EVENT} \tsensor: $it")
            }
        }
    }

    fun registerForSensorData() {
        toozifier.registerForSensorData(
            Pair(sensor, SENSOR_READING_INTERVAL)
        )
    }

    fun inflateSensorView(context: Context) {
        sensorDataView = LayoutSensorBinding.inflate(LayoutInflater.from(context)).root
        xText = sensorDataView?.findViewById(R.id.sensor_x)
        yText = sensorDataView?.findViewById(R.id.sensor_y)
        zText = sensorDataView?.findViewById(R.id.sensor_z)
    }
}
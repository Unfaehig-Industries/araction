package tech.tooz.bto.toozifier.examples.sensor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.LayoutSensorBinding
import tooz.bto.toozifier.Toozifier
import tooz.bto.common.ToozServiceMessage.Sensor.Acceleration as SensorAcceleration

class SensorData (private val toozifier: Toozifier) {

    // These are views that are displayed in the glasses
    private var sensorDataView: View? = null
    // These are the text fields that are displayed in this view
    private var xText : TextView? = null
    private var yText : TextView? = null
    private var zText : TextView? = null

    fun sendFrame (data: SensorAcceleration) {
        data.apply {
            xText?.text = x?.toString()
            yText?.text = y?.toString()
            zText?.text = z?.toString()

            sensorDataView?.run {
                toozifier.sendFrame(this)
            }
        }
    }

    fun inflateSensorView(context: Context) {
        sensorDataView = LayoutSensorBinding.inflate(LayoutInflater.from(context)).root
        xText = sensorDataView?.findViewById(R.id.sensor_x)
        yText = sensorDataView?.findViewById(R.id.sensor_y)
        zText = sensorDataView?.findViewById(R.id.sensor_z)
    }
}
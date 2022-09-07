package tech.tooz.bto.toozifier.examples.sensor_data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.SensorDataLayoutBinding
import tooz.bto.toozifier.Toozifier

class SensorData (private val toozifier: Toozifier) {

    // These are views that are displayed in the glasses
    private var sensorDataView: View? = null
    // These are the text fields that are displayed in this view
    private var nameText : TextView? = null
    private var xText : TextView? = null
    private var yText : TextView? = null
    private var zText : TextView? = null

    fun sendFrame (sensor: String, x: Double?, y: Double?, z: Double?) {
        nameText?.text = sensor
        xText?.text = x?.toString()
        yText?.text = y?.toString()
        zText?.text = z?.toString()

        sensorDataView?.run {
            toozifier.sendFrame(this)
        }
    }

    fun sendFrame (sensor: String, x: Double?) {
        nameText?.text = sensor
        xText?.text = x?.toString()
        yText?.text = ""
        zText?.text = ""

        sensorDataView?.run {
            toozifier.sendFrame(this)
        }
    }

    fun sendEmptyFrame () {
        nameText?.text = ""
        xText?.text = ""
        yText?.text = ""
        zText?.text = ""

        sensorDataView?.run {
            toozifier.sendFrame(this)
        }
    }

    fun inflateSensorView(context: Context) {
        sensorDataView = SensorDataLayoutBinding.inflate(LayoutInflater.from(context)).root
        nameText = sensorDataView?.findViewById(R.id.sensor_name)
        xText = sensorDataView?.findViewById(R.id.sensor_x)
        yText = sensorDataView?.findViewById(R.id.sensor_y)
        zText = sensorDataView?.findViewById(R.id.sensor_z)
    }
}
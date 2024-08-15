package tech.unfaehig_industries.tooz.araction.sensor_data

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import tech.unfaehig_industries.tooz.tooz_base_application.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.databinding.SensorDataLayoutBinding
import tooz.bto.toozifier.Toozifier

class SensorDataLayout (toozifier: Toozifier) : BaseToozifierLayout(toozifier) {

    // These are views that are displayed in the glasses
    private var sensorDataView: SensorDataLayoutBinding? = null
    // These are the text fields that are displayed in this view
    private var nameText : TextView? = null
    private var xText : TextView? = null
    private var yText : TextView? = null
    private var zText : TextView? = null

    fun updateFrame (reading: SafeSensorReading) {
        nameText?.text = reading.sensor
        xText?.text = (reading.data["x"] as Double).roundDecimal(2)
        yText?.text = (reading.data["y"] as Double).roundDecimal(2)
        zText?.text = (reading.data["z"] as Double).roundDecimal(2)

        sensorDataView?.run {
            layoutView = this.root
        }
    }

    fun resetFrame () {
        nameText?.text = ""
        xText?.text = ""
        yText?.text = ""
        zText?.text = ""
    }

    override fun setLayout() {
        sensorDataView?.run {
            layoutView = this.root
        }
    }

    override fun inflateView(context: Context) {
        sensorDataView = SensorDataLayoutBinding.inflate(LayoutInflater.from(context))
        nameText = sensorDataView?.sensorName
        xText = sensorDataView?.sensorX
        yText = sensorDataView?.sensorY
        zText = sensorDataView?.sensorZ
    }

    private fun Double.roundDecimal(digit: Int) = "%.${digit}f".format(this)
}
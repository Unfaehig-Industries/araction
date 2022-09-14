package tech.unfaehig_industries.tooz.araction.sensor_data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import tech.unfaehig_industries.tooz.araction.BaseToozifierLayout
import tech.unfaehig_industries.tooz.araction.R
import tech.unfaehig_industries.tooz.araction.databinding.SensorDataLayoutBinding
import tech.unfaehig_industries.tooz.araction.roundDecimal
import timber.log.Timber
import tooz.bto.toozifier.Toozifier

class SensorDataLayout (toozifier: Toozifier) : BaseToozifierLayout(toozifier) {

    // These are views that are displayed in the glasses
    private var sensorDataView: SensorDataLayoutBinding? = null
    // These are the text fields that are displayed in this view
    private var nameText : TextView? = null
    private var xText : TextView? = null
    private var yText : TextView? = null
    private var zText : TextView? = null

    fun sendFrame (sensor: String, x: Double?, y: Double?, z: Double?) {
        nameText?.text = sensor
        xText?.text = x?.roundDecimal(2)
        yText?.text = y?.roundDecimal(2)
        zText?.text = z?.roundDecimal(2)

        sensorDataView?.run {
            layoutView = this.root
        }
    }

    fun sendFrame (sensor: String, x: Double?) {
        nameText?.text = sensor
        xText?.text = x?.roundDecimal(1)
        yText?.text = ""
        zText?.text = ""

        sensorDataView?.run {
            layoutView = this.root
        }
    }

    fun sendEmptyFrame () {
        nameText?.text = ""
        xText?.text = ""
        yText?.text = ""
        zText?.text = ""

        sensorDataView?.run {
            layoutView = this.root
        }
    }

    fun inflateSensorView(context: Context) {
        sensorDataView = SensorDataLayoutBinding.inflate(LayoutInflater.from(context))
        nameText = sensorDataView?.sensorName
        xText = sensorDataView?.sensorX
        yText = sensorDataView?.sensorY
        zText = sensorDataView?.sensorZ
    }
}
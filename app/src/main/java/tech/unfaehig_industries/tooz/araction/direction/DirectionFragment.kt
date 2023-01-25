package tech.unfaehig_industries.tooz.araction.direction

//import tooz.bto.toozifier.sensors.Sensor

import CursorEventManager
import CursorEventManager.SensorDataCallback
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
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
import tooz.bto.toozifier.sensors.SensorDataListener
import java.util.*
import kotlin.math.acos
import kotlin.math.sqrt


class DirectionFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: DirectionFragmentBinding? = null
    private val binding get() = _binding!!

//    private val dataSensors: Array<Sensor> = arrayOf(Sensor.geomagRotation)
//    private val sensorReadingInterval = 100


    private lateinit var sensorManager : SensorManager
    private var sensor : Sensor? = null

    private lateinit var cursorEventManager : CursorEventManager


//    private val sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)



    override fun onResume() {
        super.onResume()
        registerToozer()
        cursorEventManager.start()

    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
        cursorEventManager.stop()

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
        toozifier.removeListener(buttonEventListener)
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")

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


    private val buttonEventListener = object : ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
            cursorEventManager.resetZeroPosition()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DirectionFragmentBinding.inflate(inflater, container, false)

        cursorEventManager =
            CursorEventManager( object : SensorDataCallback {
                override fun onCursorUpdate(angle: Double, dist: Double) {
                    // Handle cursor data

                    Timber.d("angle: $angle, distance: $dist")
                }

                override fun onAccuracyChanged(accuracy: Int) {
                    // Handle accuracy change
                }
            }, activity)

        cursorEventManager.start()
        cursorEventManager.resetZeroPosition()

        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}


package tech.unfaehig_industries.tooz.araction.radial_views

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.unfaehig_industries.tooz.phone_tracking.SensorDataCallback
import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.databinding.RadialMenuFragmentBinding
import tech.unfaehig_industries.tooz.araction.radial_menu.RadialActionButtonData
import tech.unfaehig_industries.tooz.araction.radial_menu.RadialMenuData
import tech.unfaehig_industries.tooz.araction.radial_menu.RadialSubmenuButtonData
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class RadialMenuFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: RadialMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override val layout: RadialMenuLayout = RadialMenuLayout(toozifier)
    override val dataSensors: Array<Sensor> = arrayOf()

    override val registrationListener = object : RegistrationListener {

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

    override val sensorDataListener = object : SensorDataListener {

        override fun onSensorDataRegistered() {
            Timber.d("$SENSOR_EVENT onSensorDataRegistered")
        }

        override fun onSensorDataDeregistered(sensor: Sensor) {
            Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
        }

        override fun onSensorDataReceived(sensorReading: SensorReading) {
            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")
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

    override val buttonEventListener = object : ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RadialMenuFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view which is supposed to be shown on the glasses
        layout.inflateView(requireContext())

        //Initialize RadialMenu
        val main = RadialActionButtonData(String(Character.toChars(0x274C)), Color.parseColor("#808080"), {Timber.d("Callback called")})
        val main2 = RadialActionButtonData(String(Character.toChars(0x274C)), Color.parseColor("#e5e5e5"), {Timber.d("Callback called")})
        val subMenu = RadialMenuData(main2, arrayOf(
            RadialActionButtonData(String(Character.toChars(0x1F1A1)), Color.parseColor("#e5e5e5"), {Timber.d("Callback called")}),
            RadialActionButtonData(String(Character.toChars(0x1F579)), Color.parseColor("#18281a"), {Timber.d("Callback called")}),
            RadialActionButtonData(String(Character.toChars(0x1B4CE)), Color.parseColor("#355ce8"), {Timber.d("Callback called")})
        ))
        val radials = arrayOf(
            RadialSubmenuButtonData(String(Character.toChars(0x2699)), Color.parseColor("#00CCA3"), subMenu),
            RadialActionButtonData(String(Character.toChars(0x1F4A1)), Color.parseColor("#F39237"), {Timber.d("Callback called")}),
            RadialActionButtonData(String(Character.toChars(0x1F529)), Color.parseColor("#DC758F"), {Timber.d("Callback called")}),
            RadialActionButtonData(String(Character.toChars(0x1F4CE)), Color.parseColor("#008DD5"), {Timber.d("Callback called")})
        )

        binding.radialMenu.populate(main, radials)
        layout.radialMenu.populate(main, radials)

        // Initialize phone positional tracking
        trackingEventManager =
            TrackingEventManager( object : SensorDataCallback {
                override fun onCursorUpdate(angle: Double, dist: Double) {
                    // Handle cursor data
                    binding.radialMenu.highlightButton(angle, dist)
                    layout.radialMenu.highlightButton(angle, dist)
                }


                override fun onAccuracyChanged(accuracy: Int) {
                    // Handle accuracy change
                }
            }, activity)

        trackingEventManager.start()
        trackingEventManager.resetZeroPosition()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.radialMenu.stopHoverJob()
        layout.radialMenu.stopHoverJob()
    }
}
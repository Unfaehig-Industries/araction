package tech.unfaehig_industries.tooz.araction.tile_views

import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
import tech.unfaehig_industries.tooz.phone_tracking.SensorDataCallback
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.databinding.TileMenuFragmentBinding
import tech.unfaehig_industries.tooz.araction.tile_menu.TileButtonData
import timber.log.Timber
import tooz.bto.common.ToozServiceMessage.Sensor.SensorReading
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class TileMenuFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: TileMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override val layout: TileMenuLayout = TileMenuLayout(toozifier)
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
        _binding = TileMenuFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view which is supposed to be shown on the glasses
        layout.inflateView(requireContext())

        //Initialize TileMenu
        val actionTiles = arrayOf(
            TileButtonData("Vitalwerte", Color.parseColor("#00CCA3"), {updateActionText("Vitalwerte")}, arrayOf()),
            TileButtonData("Medikation", Color.parseColor("#F39237"), {updateActionText("Medikation")}, arrayOf()),
            TileButtonData("Anamnese", Color.parseColor("#DC758F"), {updateActionText("Anamnese")}, arrayOf()),
            TileButtonData("Aufenthalt", Color.parseColor("#008DD5"), {updateActionText("Aufenthalt")}, arrayOf()),
        )

        val tiles = arrayOf(
            TileButtonData("Karin Jager", Color.parseColor("#592E83"), {}, actionTiles),
            TileButtonData("Philipp Wexler", Color.parseColor("#CCC900"), {}, actionTiles),
            TileButtonData("Marcel Gärtner", Color.parseColor("#5C374C"), {}, actionTiles),
            TileButtonData("Christin Pabst", Color.parseColor("#29339B"), {}, actionTiles)
        )

        binding.tileMenu.populate(tiles)
        layout.tileMenu.populate(tiles)

        // Initialize phone positional tracking
        trackingEventManager =
            TrackingEventManager( object : SensorDataCallback {
                override fun onCursorUpdate(angle: Double, dist: Double) {
                    binding.tileMenu.moveView(angle, dist)
                    layout.tileMenu.moveView(angle, dist)
                }

                override fun onAccuracyChanged(accuracy: Int) {
                    // Handle accuracy change
                }
            }, activity)

        trackingEventManager.start()
        trackingEventManager.resetZeroPosition()
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    private fun updateActionText(text: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                binding.actionText.text = text
                layout.actionText.text = text
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.tileMenu.stopHoverJob()
        layout.tileMenu.stopHoverJob()
    }
}
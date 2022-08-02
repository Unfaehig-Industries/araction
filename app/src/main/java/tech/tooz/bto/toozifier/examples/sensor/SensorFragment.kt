package tech.tooz.bto.toozifier.examples.sensor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentSensorBinding
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.common.ToozServiceMessage
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor
import tooz.bto.toozifier.sensors.SensorDataListener

class SensorFragment : BaseToozifierFragment() {

    companion object {
        private const val SENSOR_READING_INTERVAL = 100
        private const val SCROLL_BY_PIXEL = 2000
        private const val ACCELERATION_SCROLL_THRESHOLD = 0.025
        private const val ACCELERATION_FAST_SCROLL_THRESHOLD = 0.055
        private const val FLING_VELOCITY = 5000
    }

    private val sensor = Sensor.acceleration

    // The binding contains the views that are part of this fragment
    private var binding: FragmentSensorBinding? = null

    // These are views that are displayed in the glasses
    private var scrollByHeadMotionPromptView: View? = null
    private var scrollByHeadMotionFocusView: View? = null
    private var scrollByHeadMotionFocusScrollModeTextView: AppCompatTextView? = null

    // Stores the last 5 y values for acceleration, with a reading interval of 100 ms, this means we always have the values of the last 500 ms in this array
    private val accelerationYAxisValues = DoubleArray(5)
    private var accelerationDataCounter = 0
    private var currentYAxisValue = 0.0

    private var scrollMode = ScrollMode.DOWN


    override fun onResume() {
        super.onResume()
        registerToozer()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
    }

    private fun registerToozer() {
        toozifier.addListener(sensorDataListener)
        toozifier.addListener(buttonEventListener)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private fun deregisterToozer() {
        toozifier.deregister()
        toozifier.removeListener(sensorDataListener)
        toozifier.removeListener(buttonEventListener)
        toozifier.deregisterFromSensorData(Sensor.acceleration)
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
            scrollByHeadMotionFocusScrollModeTextView?.text = scrollMode.toString()
            updateToozUi()
            toozifier.registerForSensorData(
                Pair(sensor, SENSOR_READING_INTERVAL)
            )
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

    private val sensorDataListener = object : SensorDataListener {

        override fun onSensorDataDeregistered(sensor: Sensor) {
            Timber.d("$SENSOR_EVENT onSensorDataDeregistered sensor: $sensor")
        }

        override fun onSensorDataReceived(sensorReading: ToozServiceMessage.Sensor.SensorReading) {
            Timber.d("$SENSOR_EVENT onSensorDataReceived sensorReading of sensor: ${sensorReading.name}")
            sensorReading.reading.acceleration?.apply {
                AccelerationData(x, y, z).apply {
                    if (y != null) {
                        accelerationYAxisValues[accelerationDataCounter] = y - currentYAxisValue
                        currentYAxisValue = y
                        if (accelerationDataCounter == accelerationYAxisValues.lastIndex - 1) {
                            accelerationDataCounter = 0
                        } else {
                            accelerationDataCounter++
                        }
                        val averageAcceleration = accelerationYAxisValues.average()
                        if (averageAcceleration > ACCELERATION_SCROLL_THRESHOLD && scrollMode == ScrollMode.DOWN) {
                            binding?.recyclerViewScrollByHeadMotion?.apply {
                                if (shouldScrollFast(averageAcceleration)) {
                                    fling(0, FLING_VELOCITY)
                                } else {
                                    smoothScrollBy(
                                        0,
                                        SCROLL_BY_PIXEL
                                    )
                                }
                            }
                            accelerationYAxisValues.clear()
                            // keep tooz ui alive
                            updateToozUi()
                        } else if (averageAcceleration < -(ACCELERATION_SCROLL_THRESHOLD) && scrollMode == ScrollMode.UP) {
                            binding?.recyclerViewScrollByHeadMotion?.apply {
                                if (shouldScrollFast(averageAcceleration)) {
                                    fling(0, -(FLING_VELOCITY))
                                } else {
                                    smoothScrollBy(
                                        0,
                                        -(SCROLL_BY_PIXEL)
                                    )
                                }
                            }
                            accelerationYAxisValues.clear()
                            // keep tooz ui alive
                            updateToozUi()
                        }
                    }
                }
            }
        }

        override fun onSensorDataRegistered() {
            Timber.d("$SENSOR_EVENT onSensorDataRegistered")
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

    private val buttonEventListener = object : ButtonEventListener {
        override fun onButtonEvent(button: Button) {
            Timber.d("$BUTTON_EVENT $button")
            // Check if main button was pressed
            if (button == Button.A_1S) {
                scrollMode = when (scrollMode) {
                    ScrollMode.UP -> ScrollMode.DOWN
                    ScrollMode.DOWN -> ScrollMode.UP
                }
            }
            // Update focus with view current scroll mode
            scrollByHeadMotionFocusScrollModeTextView?.text = scrollMode.toString()
            updateToozUi()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSensorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        inflateFocusView()
        inflatePromptView()
    }

    private fun updateToozUi() {
        if (scrollByHeadMotionPromptView != null && scrollByHeadMotionFocusView != null) {
            toozifier.updateCard(
                scrollByHeadMotionPromptView!!,
                scrollByHeadMotionFocusView!!,
                // check underlying implementation
                Constants.FRAME_TIME_TO_LIVE_FOREVER
            )
        }
    }

    private fun setupRecyclerView() {
        binding?.recyclerViewScrollByHeadMotion?.let {
            val layoutManager = LinearLayoutManager(requireContext())
            it.layoutManager = layoutManager
            it.adapter = ScrollByHeadMotionAdapter()

            val dividerItemDecoration = DividerItemDecoration(
                it.context,
                layoutManager.orientation
            )
            it.addItemDecoration(dividerItemDecoration)
        }
    }

    @SuppressLint("InflateParams")
    private fun inflateFocusView() {
        scrollByHeadMotionFocusView =
            LayoutInflater.from(requireContext()).inflate(R.layout.card_scroll_by_head_motion_focus, null, false)
                ?.apply {
                    scrollByHeadMotionFocusScrollModeTextView = findViewById(R.id.text_view_scroll_mode)
                }
    }

    @SuppressLint("InflateParams")
    private fun inflatePromptView() {
        scrollByHeadMotionPromptView =
            LayoutInflater.from(requireContext()).inflate(R.layout.card_scroll_by_head_motion_prompt, null, false)
    }

    private fun shouldScrollFast(averageAcceleration: Double): Boolean {
        return averageAcceleration > ACCELERATION_FAST_SCROLL_THRESHOLD
    }

    data class AccelerationData(val x: Double?, val y: Double?, val z: Double?)

    enum class ScrollMode {
        UP, DOWN
    }
}

fun DoubleArray.clear() {
    forEachIndexed { index, _ ->
        set(index, 0.0)
    }
}
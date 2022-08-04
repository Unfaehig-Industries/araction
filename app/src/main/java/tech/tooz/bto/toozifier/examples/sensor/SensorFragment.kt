package tech.tooz.bto.toozifier.examples.sensor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentSensorBinding
import timber.log.Timber
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import tooz.bto.toozifier.sensors.Sensor

class SensorFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: FragmentSensorBinding? = null
    private val binding get() = _binding!!

    private val sensorData: SensorData = SensorData(toozifier)
    private var adapter: ScrollByHeadMotionAdapter? = null

    override fun onResume() {
        super.onResume()
        registerToozer()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
    }

    private fun registerToozer() {
        toozifier.addListener(sensorData.listener)
        toozifier.addListener(buttonEventListener)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private fun deregisterToozer() {
        toozifier.deregister()
        toozifier.removeListener(sensorData.listener)
        toozifier.removeListener(buttonEventListener)
        toozifier.deregisterFromSensorData(Sensor.acceleration)
    }

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")

            sensorData.registerForSensorData()
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ScrollByHeadMotionAdapter()
        setupRecyclerView()

        // Get the view which is supposed to be shown on the glasses
        sensorData.inflateSensorView(requireContext())
    }

    private fun setupRecyclerView() {
        binding.recyclerViewScrollByHeadMotion.let {
            val layoutManager = LinearLayoutManager(requireContext())
            it.layoutManager = layoutManager
            it.adapter = adapter

            val dividerItemDecoration = DividerItemDecoration(
                it.context,
                layoutManager.orientation
            )
            it.addItemDecoration(dividerItemDecoration)
        }
    }
}
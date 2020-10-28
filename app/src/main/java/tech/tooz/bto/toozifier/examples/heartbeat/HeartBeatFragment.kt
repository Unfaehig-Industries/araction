package tech.tooz.bto.toozifier.examples.heartbeat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.fragment_heartbeat.*
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.Resource
import tech.tooz.bto.toozifier.examples.ViewState
import tech.tooz.bto.toozifier.examples.databinding.FragmentHeartbeatBinding
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener


class HeartBeatFragment : BaseToozifierFragment(), ButtonEventListener, MessageClient.OnMessageReceivedListener {

    // These are views that are displayed in the glasses
    private var focusViewHeartBeat: ConstraintLayout? = null
    private var promptViewHeartBeat: AppCompatTextView? = null

    // The binding contains the views that are part of this fragment
    private var binding: FragmentHeartbeatBinding? = null

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
            if (isAdded) setLayout(Resource(state = ViewState.SUCCESS))
        }

        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("$TOOZ_EVENT onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
            if (isAdded) setLayout(Resource(state = ViewState.ERROR, errorMessage = getString(R.string.error)))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeartbeatBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout(Resource(state = ViewState.LOADING))
        inflateFocusView()
        inflatePromptView()
        setInitialHeartBeatText()
        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        registerToozer()
        Wearable.getMessageClient(requireContext()).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
        Wearable.getMessageClient(requireContext()).removeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.refresh) {
            findNavController().navigate(R.id.fragment_heartbeat_to_self)
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // The message we receive from the watch, if the event path equals /tooz/heartbeat, it is the current heart beat
    override fun onMessageReceived(event: MessageEvent) {
        Timber.d("$WATCH_EVENT onMessageReceived $event")
        if (isAdded) {
            if (promptViewHeartBeat != null && focusViewHeartBeat != null) {
                if (event.path == "/tooz/heartbeat") {
                    val message = String(event.data)
                    getString(R.string.bpm, message).apply {
                        val color = ContextCompat.getColor(requireContext(), getHeartColor(parseMessageToInt(message)))
                        updateFragmentUi(color, this)
                        updateToozUi(color, this)
                    }
                }
            }
        }
    }

    override fun onButtonEvent(button: Button) {
        Timber.d("$TOOZ_EVENT onButtonEvent $button")
    }

    @ColorRes
    private fun getHeartColor(bpm: Int): Int {
        return when (bpm) {
            in Int.MIN_VALUE..99 -> R.color.heart_green
            in 100..140 -> R.color.heart_yellow
            in 141..Int.MAX_VALUE -> R.color.heart_red
            else -> 0
        }
    }

    private fun parseMessageToInt(message: String): Int {
        return try {
            message.toInt()
        } catch (e: Exception) {
            Timber.d("Error parsing message to int: $e")
            0
        }
    }

    private fun updateFragmentUi(color: Int, text: String) {
        text_view_bpm.text = text
        image_view_bpm.setColorFilter(color)
    }

    private fun updateToozUi(color: Int, text: String) {
        promptViewHeartBeat!!.text = text
        focusViewHeartBeat!!.findViewById<AppCompatTextView>(R.id.text_view_bpm).text = text
        focusViewHeartBeat!!.findViewById<AppCompatImageView>(R.id.image_view_bpm).setColorFilter(color)
        // The promptView is shown when this application is shown as a card, for example when the user switches through the existing apps on the glasses
        // The focusView is shown when this application is in fullscreen mode on the glasses
        // Both views can be the same. In this example the focusView has a heart with a color, indication the frequency while the promptView only has text
        toozifier.updateCard(promptViewHeartBeat!!, focusViewHeartBeat!!, Constants.FRAME_TIME_TO_LIVE_FOREVER)
    }

    private fun setLayout(resource: Resource) {
        when (resource.state) {
            ViewState.SUCCESS -> {
                binding?.layoutError?.visibility = View.GONE
                binding?.layoutSuccess?.visibility = View.VISIBLE
                showProgress(false)
            }
            ViewState.ERROR -> {
                binding?.layoutError?.visibility = View.VISIBLE
                binding?.layoutSuccess?.visibility = View.GONE
                binding?.textViewError?.text = resource.errorMessage
                showProgress(false)
            }
            ViewState.LOADING -> {
                binding?.layoutError?.visibility = View.GONE
                binding?.layoutSuccess?.visibility = View.GONE
                showProgress(true)
            }
        }
    }

    private fun registerToozer() {
        toozifier.addListener(this)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )
    }

    private fun deregisterToozer() {
        toozifier.removeListener(this)
        toozifier.deregister()
    }

    private fun setClickListener() {
        button_glass_connection_error?.setOnClickListener {
            deregisterToozer()
            registerToozer()
        }
    }

    private fun setInitialHeartBeatText() {
        getString(R.string.bpm, "0").apply {
            binding?.textViewBpm?.text = this
        }
    }

    @SuppressLint("InflateParams")
    private fun inflateFocusView() {
        promptViewHeartBeat = (LayoutInflater.from(requireContext())
            .inflate(R.layout.card_heartbeat_prompt, null) as LinearLayout).findViewById(R.id.text_view_bpm)
    }

    @SuppressLint("InflateParams")
    private fun inflatePromptView() {
        focusViewHeartBeat = (LayoutInflater.from(requireContext())
            .inflate(R.layout.card_heartbeat_focus, null) as ConstraintLayout).findViewById(R.id.root_view_prompt)
    }

}
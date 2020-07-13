package tech.tooz.bto.toozifier.examples.heartbeat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.findNavController
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.fragment_heartbeat.*
import tech.tooz.bto.toozifier.examples.BaseFragment
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


class HeartBeatFragment : BaseFragment(), RegistrationListener, ButtonEventListener, MessageClient.OnMessageReceivedListener {

    private var heartBeatCard: AppCompatTextView? = null
    private var binding: FragmentHeartbeatBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeartbeatBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout(Resource(state = ViewState.LOADING))
        heartBeatCard = (LayoutInflater.from(requireContext())
            .inflate(R.layout.card_heartbeat, null) as LinearLayout).findViewById(R.id.text_view_bpm)
        registerToozer()
        Wearable.getMessageClient(requireContext()).addListener(this)
        setInitialHeartBeatText()
        setClickListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_heartbeat, menu);
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
        Wearable.getMessageClient(requireContext()).removeListener(this)
        binding = null
    }

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

    // The message we receive from the watch, if the event path equals /tooz/heartbeat, it is the current heart beat
    override fun onMessageReceived(event: MessageEvent) {
        Timber.d("$WATCH_EVENT onMessageReceived $event")
        if (isAdded) {
            heartBeatCard?.let { card ->
                if (event.path == "/tooz/heartbeat") {
                    val message = String(event.data)
                    getString(R.string.bpm, message).apply {
                        text_view_bpm.text = this
                        card.text = this
                        toozifier.updateCard(card, card, Constants.FRAME_TIME_TO_LIVE_FOREVER)
                    }
                }
            }
        }
    }

    override fun onButtonEvent(button: Button) {
        Timber.d("$TOOZ_EVENT onButtonEvent $button")
    }

    private fun setLayout(resource: Resource) {
        when (resource.state) {
            ViewState.SUCCESS -> {
                binding?.layoutError?.visibility = View.GONE
                binding?.textViewBpm?.visibility = View.VISIBLE
                showProgress(false)
            }
            ViewState.ERROR -> {
                binding?.layoutError?.visibility = View.VISIBLE
                binding?.textViewBpm?.visibility = View.GONE
                binding?.textViewError?.text = resource.errorMessage
                showProgress(false)
            }
            ViewState.LOADING -> {
                binding?.layoutError?.visibility = View.GONE
                binding?.textViewBpm?.visibility = View.GONE
                showProgress(true)
            }
        }
    }

    private fun registerToozer() {
        toozifier.addListener(this)
        toozifier.register(
            requireContext(),
            "Heartbeat-Example",
            this
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

}


// TODO test if a refresh button might be needed in case of state failure
//        setHasOptionsMenu(true)
package tech.tooz.bto.toozifier.examples.heartbeat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.fragment_heartbeat.*
import tech.tooz.bto.toozifier.examples.BaseFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.ViewState
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener

class HeartBeatFragment : BaseFragment(), RegistrationListener, ButtonEventListener, MessageClient.OnMessageReceivedListener {

    private var heartBeatCard: AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_heartbeat, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        heartBeatCard = (LayoutInflater.from(requireContext())
            .inflate(R.layout.card_heartbeat, null) as LinearLayout).findViewById(R.id.text_view_bpm)
        registerToozer()
        button_retry?.setOnClickListener {
            deregisterToozer()
            registerToozer()
        }
        Wearable.getMessageClient(requireContext()).addListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Wearable.getMessageClient(requireContext()).removeListener(this)
    }

    override fun onRegisterSuccess() {
        Timber.d("$TOOZ_EVENT onRegisterSuccess")
        setLayout(ViewState.SUCCESS)
    }

    override fun onDeregisterFailure(errorCause: ErrorCause) {
        Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
    }

    override fun onDeregisterSuccess() {
        Timber.d("$TOOZ_EVENT onDeregisterSuccess")
    }

    override fun onRegisterFailure(errorCause: ErrorCause) {
        Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
        setLayout(ViewState.ERROR)
    }

    override fun onMessageReceived(event: MessageEvent) {
        Timber.d("onMessageReceived $event")
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

    override fun onButtonEvent(button: Button) {
        Timber.d("$TOOZ_EVENT onButtonEvent $button")
    }

    private fun setLayout(viewState: ViewState) {
        when (viewState) {
            ViewState.SUCCESS -> {
                layout_error?.visibility = View.GONE
                text_view_bpm.visibility = View.VISIBLE
            }
            ViewState.ERROR -> {
                layout_error?.visibility = View.VISIBLE
                text_view_bpm.visibility = View.GONE
            }
        }
    }

    private fun registerToozer() {
        toozifier.addListener(this)
        toozifier.register(
            requireContext(),
            "Heartbeat oh my, what to put here?",
            this
        )
    }

    private fun deregisterToozer() {
        toozifier.removeListener(this)
        toozifier.deregister()
    }

}
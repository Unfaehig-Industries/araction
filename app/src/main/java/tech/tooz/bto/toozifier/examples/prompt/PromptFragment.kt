package tech.tooz.bto.toozifier.examples.prompt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_prompt.*
import tech.tooz.bto.toozifier.examples.BaseFragment
import tech.tooz.bto.toozifier.examples.R
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.button.Button
import tooz.bto.toozifier.button.ButtonEventListener
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import java.util.*

class PromptFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prompt, container, false)
    }

    /**
     * This is where we register with tooz OS, add the button event listener, and prepare our hidden
     * prompt view.
     */
    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        promptView = layoutInflater.inflate(R.layout.layout_prompt, null)

        toozifier.addListener(buttonEventListener)
        toozifier.register(
            requireContext(),
            getString(R.string.app_name),
            registrationListener
        )

        button_change_color.setOnClickListener {
            changeFrameAndTextColor()
        }

        button_send_frame.setOnClickListener {
            toozifier.updateCard(
                promptView,
                view_frame,
                Constants.FRAME_TIME_TO_LIVE_FOREVER
            )
        }
    }

    private val random = Random()

    private lateinit var promptView: View

    /**
     * This is the listener object that receives registration events from Toozifier.
     *
     * The main callback for this example is `onRegistrationSuccessful()`.
     */
    private val registrationListener = object : RegistrationListener {

        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.e("Deregister failure: ${errorCause.description}")
        }

        override fun onDeregisterSuccess() {
            Timber.i("Deregister success")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.e("Register failure: ${errorCause.description}")
        }

        override fun onRegisterSuccess() {
            Timber.i("Register success")
            button_send_frame.isEnabled = true
        }
    }

    /**
     * This is the button event listener that enables the app to react to button clicks
     * on the glasses. In the current user interaction concept, we only send a single-click
     * of the back button (code: *B_1S*) to the active app.
     *
     * Here we use it to reset the color of the text and background.
     */
    private val buttonEventListener = object : ButtonEventListener {

        override fun onButtonEvent(button: Button) {
            Timber.d("Button event: $button")
            changeFrameAndTextColor()
            // TODO make open source
            toozifier.updateCard(
                promptView,
                view_frame,
                Constants.FRAME_TIME_TO_LIVE_FOREVER
            )
        }
    }

    /**
     * Here, we deregister from tooz OS.
     */
    override fun onDestroy() {
        toozifier.deregister()
        super.onDestroy()
    }

    private fun changeFrameAndTextColor() {
        val backgroundColor = getRandomColor()
        val textColor = getRandomColor()

        view_frame.setBackgroundColor(backgroundColor)
        view_frame.setTextColor(textColor)
    }

    private fun getRandomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}
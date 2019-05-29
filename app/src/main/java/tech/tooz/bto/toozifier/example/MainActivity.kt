package tech.tooz.bto.toozifier.example

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val toozifier: Toozifier = ToozifierFactory.getInstance()

    private val random = Random()

    private lateinit var promptView: View

    private val registrationListener = object : RegistrationListener {

        override fun onDeregistrationFailed(eventCause: EventCause) {
            Timber.e("Deregistration failed: ${eventCause.description}")
        }

        override fun onDeregistrationSuccessful() {
            Timber.i("Deregistration successful")
        }

        override fun onRegistrationFailed(eventCause: EventCause) {
            Timber.e("Registration failed: ${eventCause.description}")
        }

        override fun onRegistrationSuccessful() {
            Timber.i("Registration successful")
            button_send_frame.isEnabled = true
        }
    }

    private val buttonEventListener = object : ButtonEventListener {

        override fun onButtonEvent(button: Constants.Button) {
            Timber.d("Button event: $button")
            when (button) {
                Constants.Button.B_1S -> {
                    view_frame.setBackgroundColor(Color.BLUE)
                    view_frame.setTextColor(Color.YELLOW)
                }
                else -> {
                    // Do nothing
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        promptView = layoutInflater.inflate(R.layout.layout_prompt, null)

        toozifier.addListener(buttonEventListener)
        toozifier.register(this, getString(R.string.app_name), registrationListener)

        button_change_color.setOnClickListener {
            changeFrameAndTextColor()
        }

        button_send_frame.setOnClickListener {
            toozifier.updateCard(promptView, view_frame, Constants.FRAME_TIME_TO_LIVE_FOREVER)
        }
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

    override fun onDestroy() {
        toozifier.deregister()
        super.onDestroy()
    }
}

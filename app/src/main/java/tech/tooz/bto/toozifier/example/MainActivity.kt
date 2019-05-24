package tech.tooz.bto.toozifier.example

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.prompt.*
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.EventCause
import tooz.bto.toozifier.RegistrationListener
import tooz.bto.toozifier.ToozifierFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private val toozifier = ToozifierFactory.getInstance()

    private val random = Random()

    private lateinit var frameViewInflater: LayoutInflater
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_send_frame.isEnabled = false

        frameViewInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        promptView = frameViewInflater.inflate(R.layout.prompt, null)

        toozifier.register(this, getString(R.string.app_name), registrationListener)

        button_change_color.setOnClickListener {
            val backgroundColor = getRandomColor()
            val textColor = getRandomColor()

            view_frame.setBackgroundColor(backgroundColor)
            view_frame.setTextColor(textColor)
        }

        button_send_frame.setOnClickListener {
            toozifier.updateCard(promptView, view_frame, Constants.FRAME_TIME_TO_LIVE_FOREVER)
        }
    }

    private fun getRandomColor(): Int {
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}

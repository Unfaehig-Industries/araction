package tech.tooz.bto.toozifier.example

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import tooz.bto.toozifier.EventCause
import tooz.bto.toozifier.RegistrationListener
import tooz.bto.toozifier.ToozifierFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private val toozifier = ToozifierFactory.getInstance()

    private val random = Random()

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

        toozifier.register(this, getString(R.string.app_name), registrationListener)

        button_change_color.setOnClickListener {
            val randomColor =
                Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            view_frame.setBackgroundColor(randomColor)
        }

        button_send_frame.setOnClickListener {
            toozifier.sendFrame(view_frame)
        }
    }
}

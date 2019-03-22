package tech.tooz.bto.toozifier.example

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tooz.bto.toozifier.ToozifierFactory
import tooz.bto.toozifier.impl.SimpleGlassesListener
import java.util.*

class MainActivity : AppCompatActivity() {

    private val toozifier = ToozifierFactory.getInstance()

    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toozifier.register(this, getString(R.string.app_name), SimpleGlassesListener())

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

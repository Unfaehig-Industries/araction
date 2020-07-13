package tech.tooz.bto.toozifier.watch.beat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.TextView
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.ExecutionException

class MainActivity : WearableActivity(), SensorEventListener {

    companion object {
        private const val REQUEST_CODE_BODY_SENSORS = 1
    }

    lateinit var textView: TextView
    private var sensorManager: SensorManager? = null
    private var heartBeatSensor: Sensor? = null
    private var currentHeartBeat: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        // Enables Always-on
        setAmbientEnabled()
    }

    override fun onResume() {
        super.onResume()
        checkPermissionThenDo(Manifest.permission.BODY_SENSORS, REQUEST_CODE_BODY_SENSORS) {
            registerSensors()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        textView.text = getString(R.string.bpm, currentHeartBeat)
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        textView.paint.isAntiAlias = false
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        textView.paint.isAntiAlias = true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.let { values ->
            if (values.isNotEmpty()) {
                values[0].toInt().let { firstValue ->
                    currentHeartBeat = firstValue
                    textView.text = getString(R.string.bpm, currentHeartBeat)
                    sendMessageToHandheld(currentHeartBeat.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_BODY_SENSORS -> registerSensors()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindViews() {
        textView = findViewById(R.id.text)
    }

    private fun sendMessageToHandheld(message: String) {
        MessageThread("/tooz/heartbeat", message).start()
    }

    private fun registerSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartBeatSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        sensorManager!!.registerListener(this, heartBeatSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    internal inner class MessageThread
        (var path: String, var message: String) : Thread() {
        override fun run() {
            // Retrieve the connected devices
            val nodeListTask = Wearable.getNodeClient(applicationContext).connectedNodes
            try {
                // Block on a task and get the result synchronously
                val nodes = Tasks.await<List<Node>>(nodeListTask)
                for (node in nodes) {
                    // Send the message
                    val sendMessageTask = Wearable.getMessageClient(this@MainActivity)
                        .sendMessage(node.id, path, message.toByteArray())
                    try {
                        Tasks.await(sendMessageTask)
                    } catch (exception: ExecutionException) {
                        Log.d("Error!", exception.toString())
                    } catch (exception: InterruptedException) {
                        Log.d("Error!", exception.toString())
                    }
                }
            } catch (exception: ExecutionException) {
                Log.d("Error!", exception.toString())
            } catch (exception: InterruptedException) {
                Log.d("Error!", exception.toString())
            }

        }
    }
}

fun WearableActivity.checkPermissionThenDo(
    permission: String,
    request: Int,
    function: () -> Unit
) {
    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        // TODO refactor like this
        // https://adambennett.dev/2020/03/introducing-the-activity-result-apis/
        requestPermissions(arrayOf(permission), request)
    } else {
        function()
    }
}
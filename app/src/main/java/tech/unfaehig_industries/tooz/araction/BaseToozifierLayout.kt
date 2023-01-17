package tech.unfaehig_industries.tooz.araction

import android.content.Context
import android.view.View
import kotlinx.coroutines.*
import timber.log.Timber
import tooz.bto.toozifier.Toozifier

abstract class BaseToozifierLayout (toozifier: Toozifier){
    val delay: Long = 150

    var layoutView: View? = null
    private var isPaused: Boolean = false

    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch {
        while (isActive) {
            if (!isPaused) {

                layoutView?.run {
                    toozifier.sendFrame(this)
                }
                delay(delay)
            }
        }
    }

    fun pauseJob() {
        isPaused = true
    }

    fun resumeJob() {
        isPaused = false
    }

    fun cancelJob() {
        job.cancel("View destroyed")
    }

    abstract fun sendFrame()
    abstract fun sendFrame(reading: SafeSensorReading)
    abstract fun sendBlankFrame()

    abstract fun inflateView(context: Context)
}
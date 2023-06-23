package tech.unfaehig_industries.tooz.araction

import android.content.Context
import android.view.View
import kotlinx.coroutines.*
import tooz.bto.toozifier.Toozifier

abstract class BaseToozifierLayout (toozifier: Toozifier){
    private val delay: Long = 150L

    var layoutView: View? = null
    private var isPaused: Boolean = false

    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch {
        while (isActive) {
            if (!isPaused && toozifier.isRegistered) {

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

    abstract fun updateFrame()
    abstract fun updateFrame(reading: SafeSensorReading)
    abstract fun setBlankFrame()

    abstract fun inflateView(context: Context)
}
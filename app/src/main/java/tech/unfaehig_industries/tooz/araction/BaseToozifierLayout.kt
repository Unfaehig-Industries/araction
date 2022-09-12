package tech.unfaehig_industries.tooz.araction

import android.view.View
import kotlinx.coroutines.*
import timber.log.Timber
import tooz.bto.toozifier.Toozifier

open class BaseToozifierLayout (toozifier: Toozifier){

    var layoutView: View? = null
    private var isPaused: Boolean = false

    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch {
        while (isActive) {
            if (!isPaused) {
                val view = layoutView
                view?.run {
                    toozifier.sendFrame(this)
                }
                Timber.d("Frame: send")
                delay(33)
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
}
package tech.unfaehig_industries.tooz.araction.frame_rate

import android.content.Context
import android.view.LayoutInflater
import kotlinx.coroutines.*
import tech.unfaehig_industries.tooz.araction.databinding.FrameRateLayoutBinding
import timber.log.Timber
import tooz.bto.toozifier.Toozifier

class FrameRateLayout (toozifier: Toozifier) {

    // These are views that are displayed in the glasses
    private var frameRateView: FrameRateLayoutBinding? = null

    private var frameCount = 0
    private var readingCount = 0

    private var isPaused: Boolean = false
    var delay: Long = 250


    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch {
        while (isActive) {
            if (!isPaused) {
                frameCount += 1

                frameRateView?.run {
                    this.frameCounter.text = frameCount.toString()
                    Timber.d("Frame rate: frame ${this.frameCounter.text} send")
                    toozifier.sendFrame(this.root)
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

    fun setInterval() {
        frameRateView?.run {
            this.interval.text = delay.toString()
            Timber.d("Frame rate: interval ${this.interval.text}")
        }
    }

    fun updateFrame() {
        readingCount += 1
        frameRateView?.run {
            this.readingCounter.text = readingCount.toString()
            Timber.d("Frame rate: reading ${this.readingCounter.text}")
        }
    }

    fun inflateView(context: Context) {
        frameRateView = FrameRateLayoutBinding.inflate(LayoutInflater.from(context))
    }
}
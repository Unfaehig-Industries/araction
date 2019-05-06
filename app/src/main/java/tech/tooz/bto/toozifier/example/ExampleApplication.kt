package tech.tooz.bto.toozifier.example

import android.app.Application
import timber.log.Timber

@Suppress("unused")
class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
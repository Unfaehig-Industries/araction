package tech.unfaehig_industries.tooz.tooz_base_views

import android.app.Application
import timber.log.Timber
import tooz.bto.toozifier.ToozifierFactory

open class BaseApplication : Application() {

    companion object {
        private lateinit var instance: BaseApplication
        fun getBaseApplication(): BaseApplication = instance
    }

    val toozifier = ToozifierFactory.getInstance()

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
package tech.tooz.bto.toozifier.examples

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    companion object {
        const val TOOZ_EVENT = "Tooz event:"
    }

    protected val toozifier = ExampleApplication.getExampleApplication().toozifier

}
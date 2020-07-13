package tech.tooz.bto.toozifier.examples

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    protected val toozifier = ExampleApplication.getExampleApplication().toozifier

}
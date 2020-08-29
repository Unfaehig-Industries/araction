package tech.tooz.bto.toozifier.examples.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import kotlinx.android.synthetic.main.fragment_heartbeat.*
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentWebviewBinding
import timber.log.Timber
import tooz.bto.common.Constants
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener
import kotlin.concurrent.fixedRateTimer

class WebViewFragment : BaseToozifierFragment() {

    companion object {
        private const val WEB_VIEW_MIME_TYPE = "text/html; charset=utf-8"
        private const val UTF_8 = "UTF-8"
        private const val HTML = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Example</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <p>This is one amazing WebView.</p>\n" +
                "    </body>\n" +
                "</html>"
    }

    // These are views that are displayed in the glasses
    private var focusViewWebView: WebView? = null
    private var promptViewWebView: WebView? = null

    // The binding contains the views that are part of this fragment
    private var binding: FragmentWebviewBinding? = null

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            Timber.d("$TOOZ_EVENT onRegisterSuccess")
            setToozUi()
        }

        override fun onDeregisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onDeregisterFailure $errorCause")
        }

        override fun onDeregisterSuccess() {
            Timber.d("$TOOZ_EVENT onDeregisterSuccess")
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            Timber.d("$TOOZ_EVENT onRegisterFailure $errorCause")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateFocusView()
        inflatePromptView()
        focusViewWebView?.loadData(HTML, WEB_VIEW_MIME_TYPE, UTF_8)
        promptViewWebView?.loadData(HTML, WEB_VIEW_MIME_TYPE, UTF_8)
    }

    override fun onResume() {
        super.onResume()
        registerToozer()
    }

    override fun onPause() {
        super.onPause()
        deregisterToozer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // We update the webview every second. In this case it does not do much since the content of the webview is static.
    // If the contents were dynamic, the changes would be reflected in the glasses
    private fun setToozUi() {
        fixedRateTimer(period = 1000) {
            toozifier.updateCard(promptViewWebView!!, focusViewWebView!!, Constants.FRAME_TIME_TO_LIVE_FOREVER)
        }
    }

    private fun registerToozer() {
        toozifier.register(
            requireContext(),
            // Of course strings should usually be in the strings.xml file
            "WebView example",
            registrationListener
        )
    }

    private fun deregisterToozer() {
        toozifier.deregister()
    }


    @SuppressLint("InflateParams")
    private fun inflateFocusView() {
        promptViewWebView = (LayoutInflater.from(requireContext())
            .inflate(R.layout.card_webview, null).findViewById(R.id.web_view) as WebView)
    }

    @SuppressLint("InflateParams")
    private fun inflatePromptView() {
        focusViewWebView = (LayoutInflater.from(requireContext())
            .inflate(R.layout.card_webview, null).findViewById(R.id.web_view) as WebView)
    }

}
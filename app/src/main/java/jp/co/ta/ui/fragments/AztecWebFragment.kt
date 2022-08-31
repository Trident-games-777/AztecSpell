package jp.co.ta.ui.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import jp.co.ta.R
import jp.co.ta.loaders.FileLoader

class AztecWebFragment : Fragment(R.layout.fragment_aztec_web) {
    private lateinit var aztecWeb: WebView
    private lateinit var chooserCallback: ValueCallback<Array<Uri?>>
    private val args: AztecWebFragmentArgs by navArgs()

    val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        chooserCallback.onReceiveValue(it.toTypedArray())
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.statusBarColor = requireContext().getColor(R.color.black)
        aztecWeb = view.findViewById(R.id.aztec_webView)

        aztecWeb.loadUrl(args.link)

        with(aztecWeb.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = false
            userAgentString = System.getProperty(STRING_AGENT)
        }

        with(CookieManager.getInstance()) {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(aztecWeb, true)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (aztecWeb.canGoBack()) {
                        aztecWeb.goBack()
                    }
                }
            })

        aztecWeb.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.e("TAG", error.description.toString())
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                CookieManager.getInstance().flush()
                if (url == args.domain) {
                    findNavController().navigate(
                        AztecWebFragmentDirections.actionAztecWebFragmentToAztecGreetingFragment()
                    )
                } else {
                    FileLoader.uploadToFile(url, requireContext(), args.domain)
                }
            }
        }

        aztecWeb.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri?>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                chooserCallback = filePathCallback
                getContent.launch(IMAGE_MIME_TYPE)
                return true
            }

            @SuppressLint("SetJavaScriptEnabled")
            override fun onCreateWindow(
                view: WebView?, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
        private const val STRING_AGENT = "http.agent"
    }
}
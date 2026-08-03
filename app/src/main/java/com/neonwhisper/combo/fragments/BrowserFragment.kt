package com.neonwhisper.combo.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.neonwhisper.combo.R

class BrowserFragment : Fragment() {
    private lateinit var webView: WebView
    private lateinit var urlInput: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        urlInput = view.findViewById(R.id.urlInput)

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        view.findViewById<ImageButton>(R.id.btnRefresh).setOnClickListener { webView.reload() }
        view.findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            loadUrl("https://github.com/Yshi777-lang")
        }

        urlInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                var url = urlInput.text.toString()
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://$url"
                }
                loadUrl(url)
                true
            } else false
        }

        loadUrl("https://github.com/Yshi777-lang")
    }

    private fun loadUrl(url: String) {
        webView.loadUrl(url)
        urlInput.setText(url)
    }
}

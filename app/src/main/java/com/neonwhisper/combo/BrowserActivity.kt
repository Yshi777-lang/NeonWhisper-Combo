package com.neonwhisper.combo

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class BrowserActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var urlInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        webView = findViewById(R.id.webView)
        urlInput = findViewById(R.id.urlInput)

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        findViewById<ImageButton>(R.id.refreshButton).setOnClickListener { webView.reload() }
        findViewById<ImageButton>(R.id.homeButton).setOnClickListener {
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

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}

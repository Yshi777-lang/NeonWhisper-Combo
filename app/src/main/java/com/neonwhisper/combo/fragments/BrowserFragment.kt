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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.neonwhisper.combo.R

class BrowserFragment : Fragment() {
    private lateinit var webView: WebView
    private lateinit var urlInput: EditText
    private lateinit var spinnerSearch: Spinner
    private lateinit var btnMode: Button
    private var isSearchMode = true

    private val searchEngines = mapOf(
        "Google" to "https://www.google.com/search?q=",
        "DuckDuckGo" to "https://duckduckgo.com/?q=",
        "Bing" to "https://www.bing.com/search?q=",
        "Yandex" to "https://yandex.com/search/?text=",
        "GitHub" to "https://github.com/search?q="
    )

    private val myPages = arrayOf(
        "https://github.com/Yshi777-lang",
        "https://github.com/Yshi777-lang/NeonWhisper-Combo",
        "https://google.com"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        webView = view.findViewById(R.id.webView)
        urlInput = view.findViewById(R.id.urlInput)
        spinnerSearch = view.findViewById(R.id.spinnerSearch)
        btnMode = view.findViewById(R.id.btnMode)

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, searchEngines.keys.toTypedArray())
        spinnerSearch.adapter = adapter

        btnMode.setOnClickListener {
            isSearchMode = !isSearchMode
            updateMode()
        }

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        view.findViewById<ImageButton>(R.id.btnRefresh).setOnClickListener { webView.reload() }
        view.findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            if (isSearchMode) loadUrl("https://github.com/Yshi777-lang")
            else loadUrl(myPages[0])
        }

        urlInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (isSearchMode) performSearch()
                else loadUrl(urlInput.text.toString())
                true
            } else false
        }

        updateMode()
        loadUrl("https://github.com/Yshi777-lang")
    }

    private fun updateMode() {
        btnMode.text = if (isSearchMode) "Mode: Search" else "Mode: My Pages"
        spinnerSearch.visibility = if (isSearchMode) View.VISIBLE else View.GONE
    }

    private fun performSearch() {
        val query = urlInput.text.toString()
        val selectedEngine = spinnerSearch.selectedItem.toString()
        val baseUrl = searchEngines[selectedEngine] ?: "https://www.google.com/search?q="
        loadUrl("$baseUrl$query")
    }

    private fun loadUrl(url: String) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            webView.loadUrl("https://$url")
        } else {
            webView.loadUrl(url)
        }
        urlInput.setText(url)
    }
}

package com.neonwhisper.combo.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val url by viewModel.url.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val purplePrimary = Color(0xFF9C27B0)
    val purpleBackground = Color(0xFF12121C)

    Column(modifier = Modifier.fillMaxSize().background(purpleBackground)) {
        // Адресная строка
        OutlinedTextField(
            value = url,
            onValueChange = { viewModel.setUrl(it) },
            label = { Text("URL", color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = purplePrimary,
                unfocusedBorderColor = Color.Gray,
                textColor = Color.White
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.loadUrl(url) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = purplePrimary
                    )
                }
            }
        )

        // WebView
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = false
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        settings.setGeolocationEnabled(false)
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        
                        // Фон WebView
                        setBackgroundColor(Color(0xFF12121C).value.toInt())
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                viewModel.setLoading(true)
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                viewModel.setLoading(false)
                            }
                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                viewModel.setLoading(false)
                            }
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                val requestUrl = request?.url.toString()
                                val adDomains = listOf("doubleclick.net", "adservice.google.com", "googlesyndication.com")
                                if (adDomains.any { requestUrl.contains(it) }) {
                                    return WebResourceResponse("text/plain", "UTF-8", null)
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        
                        // Загружаем стартовую страницу
                        loadUrl(url)
                    }
                },
                update = { webView ->
                    val currentUrl = webView.url
                    if (currentUrl != url && viewModel.shouldLoad()) {
                        webView.loadUrl(url)
                    }
                }
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = purplePrimary
                )
            }
        }
    }
}

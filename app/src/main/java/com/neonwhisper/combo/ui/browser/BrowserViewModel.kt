package com.neonwhisper.combo.ui.browser

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BrowserViewModel : ViewModel() {
    private val _url = MutableStateFlow("https://duckduckgo.com")
    val url: StateFlow<String> = _url.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var shouldLoadFlag = false

    fun setUrl(newUrl: String) {
        _url.value = if (newUrl.startsWith("http")) newUrl else "https://$newUrl"
    }
    
    fun loadUrl(url: String) {
        shouldLoadFlag = true
        _isLoading.value = true
        _url.value = url
    }
    
    fun shouldLoad(): Boolean {
        val result = shouldLoadFlag
        shouldLoadFlag = false
        return result
    }
    
    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
}

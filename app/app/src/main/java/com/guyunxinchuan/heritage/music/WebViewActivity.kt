package com.guyunxinchuan.heritage.music

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    private val webViewBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        onBackPressedDispatcher.addCallback(this, webViewBackCallback)

        setupViews()
    }

    private fun setupViews() {
        val title = intent.getStringExtra("title") ?: "网页"
        val url = intent.getStringExtra("url") ?: "https://example.com"

        supportActionBar?.title = title

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupWebView(url)
    }

    private fun setupWebView(url: String) {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.loadingProgressBar.visibility = android.view.View.GONE
            }
        }

        binding.webView.loadUrl(url)
    }
}
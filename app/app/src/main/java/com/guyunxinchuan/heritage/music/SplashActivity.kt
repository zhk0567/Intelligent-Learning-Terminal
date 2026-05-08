package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        startSplashAnimation()

        // 留给视觉过渡的最小时长，相比原来 2000ms 缩短，避免无谓等待
        handler.postDelayed({ navigateToMainActivity() }, SPLASH_HOLD_MS)

        binding.root.setOnClickListener { navigateToMainActivity() }

        // 启动页禁止返回；用 OnBackPressedDispatcher 取代已废弃的 onBackPressed 重写
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = Unit
        })
    }

    private fun startSplashAnimation() {
        binding.logoImageView.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(700)
            .withEndAction {
                binding.logoImageView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(380)
                    .start()
            }
            .start()

        binding.appNameTextView.alpha = 0f
        binding.appNameTextView.animate()
            .alpha(1f)
            .setDuration(900)
            .start()

        binding.taglineTextView.alpha = 0f
        handler.postDelayed({
            binding.taglineTextView.animate()
                .alpha(1f)
                .setDuration(600)
                .start()
        }, 350)
    }

    @Suppress("DEPRECATION")
    private fun navigateToMainActivity() {
        if (navigated) return
        navigated = true
        handler.removeCallbacksAndMessages(null)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_HOLD_MS = 1200L
    }
}

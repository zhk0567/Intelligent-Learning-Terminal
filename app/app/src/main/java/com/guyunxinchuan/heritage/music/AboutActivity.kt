package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
    }

    private fun setupViews() {
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.checkUpdateItem.setOnClickListener {
            checkForUpdates()
        }

        binding.userAgreementItem.setOnClickListener {
            showUserAgreement()
        }

        binding.privacyPolicyItem.setOnClickListener {
            showPrivacyPolicy()
        }

        binding.contactUsItem.setOnClickListener {
            contactUs()
        }

        binding.shareAppItem.setOnClickListener {
            shareApp()
        }

        displayVersionInfo()
    }

    private fun displayVersionInfo() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            @Suppress("DEPRECATION")
            val versionCode =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    packageInfo.versionCode.toLong()
                }
            binding.versionTextView.text = "\u7248\u672c v$versionName ($versionCode)"
            binding.buildDateTextView.text = "\u6784\u5efa\u65e5\u671f\uff1a2024-01-01"
        } catch (e: Exception) {
            binding.versionTextView.text = "\u7248\u672c v1.0.0"
        }
    }

    private fun checkForUpdates() {
        binding.checkUpdateProgressBar.visibility = android.view.View.VISIBLE

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.checkUpdateProgressBar.visibility = android.view.View.GONE
            UiFeedback.toast(this, "\u5f53\u524d\u5df2\u662f\u6700\u65b0\u7248\u672c")
        }, 1500)
    }

    private fun showUserAgreement() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("title", "\u7528\u6237\u534f\u8bae")
        intent.putExtra("url", "https://example.com/user-agreement")
        startActivity(intent)
    }

    private fun showPrivacyPolicy() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("title", "\u9690\u79c1\u653f\u7b56")
        intent.putExtra("url", "https://example.com/privacy-policy")
        startActivity(intent)
    }

    private fun contactUs() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:support@example.com")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "\u53e4\u97f5\u85aa\u4f20\u5e94\u7528\u53cd\u9988")

        try {
            startActivity(Intent.createChooser(emailIntent, "\u9009\u62e9\u90ae\u4ef6\u5e94\u7528"))
        } catch (e: Exception) {
            UiFeedback.toast(this, "\u672a\u627e\u5230\u90ae\u4ef6\u5e94\u7528")
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "\u53e4\u97f5\u85aa\u4f20")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "\u63a8\u8350\u4f7f\u7528\u53e4\u97f5\u85aa\u4f20\u5e94\u7528\uff0c\u4f53\u9a8c\u4f20\u7edf\u6587\u5316\u4e0e\u73b0\u4ee3\u79d1\u6280\u7684\u5b8c\u7f8e\u7ed3\u5408\uff01\u4e0b\u8f7d\u94fe\u63a5\uff1ahttps://example.com/download"
        )

        try {
            startActivity(Intent.createChooser(shareIntent, "\u5206\u4eab\u5e94\u7528"))
        } catch (e: Exception) {
            UiFeedback.toast(this, "\u5206\u4eab\u5931\u8d25")
        }
    }
}

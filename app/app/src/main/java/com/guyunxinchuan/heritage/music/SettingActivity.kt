package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.guyunxinchuan.heritage.music.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
    }

    override fun onResume() {
        super.onResume()
        refreshThemeSummary()
    }

    private fun setupViews() {
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.userAvatarImageView.setOnClickListener {
            showChangeAvatarDialog()
        }

        binding.editProfileItem.setOnClickListener {
            navigateToEditProfile()
        }

        binding.changePasswordItem.setOnClickListener {
            navigateToChangePassword()
        }

        binding.clearCacheItem.setOnClickListener {
            showClearCacheDialog()
        }

        binding.themeAppearanceItem.setOnClickListener {
            showThemePickerDialog()
        }

        binding.aboutUsItem.setOnClickListener {
            navigateToAbout()
        }

        binding.privacyPolicyItem.setOnClickListener {
            showPrivacyPolicy()
        }

        binding.logoutItem.setOnClickListener {
            showLogoutDialog()
        }

        displayUserInfo()
        refreshThemeSummary()
    }

    private fun refreshThemeSummary() {
        binding.themeAppearanceCurrent.text = themeModeLabel(ThemePrefs.getNightMode(this))
    }

    private fun themeModeLabel(mode: Int): String = when (mode) {
        AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.setting_theme_option_light)
        AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.setting_theme_option_dark)
        else -> getString(R.string.setting_theme_option_dark)
    }

    private fun showThemePickerDialog() {
        val modes = intArrayOf(
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES,
        )
        val labels = arrayOf(
            getString(R.string.setting_theme_option_light),
            getString(R.string.setting_theme_option_dark),
        )
        val current = ThemePrefs.getNightMode(this)
        val checked = modes.indexOf(current).let { if (it < 0) 1 else it }

        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MyApplication_MaterialAlertDialog)
            .setTitle(R.string.setting_theme_dialog_title)
            .setSingleChoiceItems(labels, checked) { dialog, which ->
                val mode = modes[which]
                if (mode != ThemePrefs.getNightMode(this)) {
                    ThemePrefs.setNightMode(this, mode)
                    UiFeedback.toast(
                        this,
                        getString(R.string.setting_theme_changed_toast, labels[which]),
                    )
                    dialog.dismiss()
                    // 仅重建当前页以应用昼/夜资源，保留返回栈、不回到首页
                    window.decorView.post {
                        if (!isFinishing && !isDestroyed) recreate()
                    }
                } else {
                    dialog.dismiss()
                }
            }
            .setNegativeButton(R.string.fav_dialog_cancel, null)
            .show()
    }

    private fun displayUserInfo() {
        binding.userNameTextView.text = "用户123456"
        binding.userPhoneTextView.text = "138****8888"
    }

    private fun showChangeAvatarDialog() {
        val options = arrayOf("拍照", "从相册选择", "使用默认头像")
        AlertDialog.Builder(this)
            .setTitle("修改头像")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> chooseFromGallery()
                    2 -> useDefaultAvatar()
                }
            }
            .show()
    }

    private fun takePhoto() {
        UiFeedback.toast(this, "拍照功能开发中")
    }

    private fun chooseFromGallery() {
        UiFeedback.toast(this, "相册选择功能开发中")
    }

    private fun useDefaultAvatar() {
        binding.userAvatarImageView.setImageResource(android.R.drawable.ic_menu_my_calendar)
        UiFeedback.toast(this, "已使用默认头像")
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToChangePassword() {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle("清除缓存")
            .setMessage("确定要清除所有缓存数据吗？")
            .setPositiveButton("确定") { _, _ ->
                clearCache()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun clearCache() {
        binding.clearCacheProgressBar.visibility = android.view.View.VISIBLE

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.clearCacheProgressBar.visibility = android.view.View.GONE
            UiFeedback.toast(this, "缓存清除成功")
        }, 1500)
    }

    private fun navigateToAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun showPrivacyPolicy() {
        AlertDialog.Builder(this)
            .setTitle("隐私政策")
            .setMessage(
                "我们非常重视您的隐私保护。本应用收集的信息仅用于提供更好的服务体验，包括：\n\n" +
                    "1. 用户基本信息（用户名、头像等）\n" +
                    "2. 使用数据（浏览记录、收藏内容等）\n" +
                    "3. 设备信息（用于优化应用性能）\n\n" +
                    "我们承诺不会将您的个人信息泄露给第三方。\n\n" +
                    "如需了解更多详情，请联系客服。"
            )
            .setPositiveButton("我知道了", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("退出登录")
            .setMessage("确定要退出当前账号吗？")
            .setPositiveButton("确定") { _, _ ->
                performLogout()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun performLogout() {
        binding.logoutProgressBar.visibility = android.view.View.VISIBLE

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.logoutProgressBar.visibility = android.view.View.GONE

            UiFeedback.toast(this, "已退出登录")

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }, 1000)
    }
}

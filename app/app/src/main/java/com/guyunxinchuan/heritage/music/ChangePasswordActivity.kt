package com.guyunxinchuan.heritage.music

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
    }

    private fun setupViews() {
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.confirmButton.setOnClickListener {
            val oldPassword = binding.oldPasswordEditText.text.toString().trim()
            val newPassword = binding.newPasswordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (validatePasswords(oldPassword, newPassword, confirmPassword)) {
                changePassword(oldPassword, newPassword)
            }
        }
    }

    private fun validatePasswords(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        if (TextUtils.isEmpty(oldPassword)) {
            binding.oldPasswordEditText.error = "请输入旧密码"
            return false
        }

        if (TextUtils.isEmpty(newPassword)) {
            binding.newPasswordEditText.error = "请输入新密码"
            return false
        }

        if (newPassword.length < 6) {
            binding.newPasswordEditText.error = "新密码至少6位"
            return false
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordEditText.error = "请确认新密码"
            return false
        }

        if (newPassword != confirmPassword) {
            binding.confirmPasswordEditText.error = "两次输入的新密码不一致"
            return false
        }

        if (oldPassword == newPassword) {
            binding.newPasswordEditText.error = "新密码不能与旧密码相同"
            return false
        }

        return true
    }

    private fun changePassword(oldPassword: String, newPassword: String) {
        binding.changePasswordProgressBar.visibility = android.view.View.VISIBLE
        binding.confirmButton.isEnabled = false

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.changePasswordProgressBar.visibility = android.view.View.GONE
            binding.confirmButton.isEnabled = true

            UiFeedback.toast(this, "密码修改成功")
            finish()
        }, 2000)
    }
}

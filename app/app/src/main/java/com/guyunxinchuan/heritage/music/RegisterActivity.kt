package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var countDownTimer: CountDownTimer? = null
    private var isCountingDown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
    }

    private fun setupViews() {
        supportActionBar?.hide()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.getVerificationCodeButton.setOnClickListener {
            val phone = binding.phoneEditText.text.toString().trim()
            if (validatePhoneForVerification(phone)) {
                getVerificationCode(phone)
            }
        }

        binding.registerButton.setOnClickListener {
            val phone = binding.phoneEditText.text.toString().trim()
            val code = binding.verificationCodeEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (validateRegistration(phone, code, password, confirmPassword)) {
                performRegistration(phone, code, password)
            }
        }

        binding.loginTextView.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validatePhoneForVerification(phone: String): Boolean {
        if (TextUtils.isEmpty(phone)) {
            binding.phoneEditText.error = "请输入手机号"
            return false
        }

        if (!isValidPhoneNumber(phone)) {
            binding.phoneEditText.error = "手机号格式不正确"
            return false
        }

        return true
    }

    private fun validateRegistration(
        phone: String,
        code: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (TextUtils.isEmpty(phone)) {
            binding.phoneEditText.error = "请输入手机号"
            return false
        }

        if (!isValidPhoneNumber(phone)) {
            binding.phoneEditText.error = "手机号格式不正确"
            return false
        }

        if (TextUtils.isEmpty(code)) {
            binding.verificationCodeEditText.error = "请输入验证码"
            return false
        }

        if (code.length != 6) {
            binding.verificationCodeEditText.error = "验证码为6位数字"
            return false
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.error = "请输入密码"
            return false
        }

        if (password.length < 6) {
            binding.passwordEditText.error = "密码至少6位"
            return false
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordEditText.error = "请确认密码"
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "两次输入的密码不一致"
            return false
        }

        return true
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^1[3-9]\\d{9}$"))
    }

    private fun getVerificationCode(phone: String) {
        if (isCountingDown) {
            return
        }

        binding.getVerificationCodeButton.isEnabled = false
        isCountingDown = true

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.getVerificationCodeButton.text = "${seconds}s后重试"
            }

            override fun onFinish() {
                binding.getVerificationCodeButton.isEnabled = true
                binding.getVerificationCodeButton.text = "获取验证码"
                isCountingDown = false
            }
        }.start()

        UiFeedback.toast(this, "验证码已发送到 $phone")
    }

    private fun performRegistration(phone: String, code: String, password: String) {
        binding.registerProgressBar.visibility = android.view.View.VISIBLE
        binding.registerButton.isEnabled = false

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.registerProgressBar.visibility = android.view.View.GONE
            binding.registerButton.isEnabled = true

            UiFeedback.toast(this, "注册成功")

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("phone", phone)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}

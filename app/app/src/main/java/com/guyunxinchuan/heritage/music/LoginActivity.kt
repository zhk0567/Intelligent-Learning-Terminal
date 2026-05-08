package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.guyunxinchuan.heritage.music.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private var isPhoneLoginMode = true // 默认为手机号登录模式
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        setupLoginModeSwitch()
        setupDoubleBackToExit()
    }

    private fun setupDoubleBackToExit() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    return
                }
                doubleBackToExitPressedOnce = true
                UiFeedback.toast(this@LoginActivity, "再按一次退出应用")
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        })
    }
    
    private fun setupViews() {
        // 开始探索按钮点击事件
        binding.exploreButton.setOnClickListener {
            showLoginOptions()
        }
        
        // 设置用户协议文本
        setupAgreementText()
        
        // 登录按钮点击事件
        binding.loginButton.setOnClickListener {
            if (isPhoneLoginMode) {
                val phone = binding.phoneEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                
                if (validatePhoneLogin(phone, password)) {
                    performPhoneLogin(phone, password)
                }
            } else {
                val username = binding.usernameEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                
                if (validatePasswordLogin(username, password)) {
                    performPasswordLogin(username, password)
                }
            }
        }
        
        // 游客模式按钮点击事件
        binding.guestModeButton.setOnClickListener {
            enterGuestMode()
        }
        
        // 注册按钮点击事件
        binding.registerTextView.setOnClickListener {
            navigateToRegister()
        }
        
        // 忘记密码点击事件
        binding.forgotPasswordTextView.setOnClickListener {
            showForgotPasswordDialog()
        }
    }
    
    private fun setupAgreementText() {
        val fullText = "登录即表示同意《用户协议》与《隐私政策》"
        val spannableString = SpannableString(fullText)
        
        // 找到《用户协议》的位置
        val userAgreementStart = fullText.indexOf("《用户协议》")
        val userAgreementEnd = userAgreementStart + "《用户协议》".length
        
        // 找到《隐私政策》的位置
        val privacyPolicyStart = fullText.indexOf("《隐私政策》")
        val privacyPolicyEnd = privacyPolicyStart + "《隐私政策》".length
        
        // 设置《用户协议》为可点击、淡金色、带下划线
        if (userAgreementStart != -1) {
            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showUserAgreement()
                    }
                    
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = ContextCompat.getColor(this@LoginActivity, R.color.gold_accent)
                        ds.isUnderlineText = true
                    }
                },
                userAgreementStart,
                userAgreementEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // 设置《隐私政策》为可点击、淡金色、带下划线
        if (privacyPolicyStart != -1) {
            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        showPrivacyPolicy()
                    }
                    
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = ContextCompat.getColor(this@LoginActivity, R.color.gold_accent)
                        ds.isUnderlineText = true
                    }
                },
                privacyPolicyStart,
                privacyPolicyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // 应用SpannableString到TextView
        binding.agreementTextView.text = spannableString
        binding.agreementTextView.movementMethod = LinkMovementMethod.getInstance()
        // 禁用长按高亮效果
        binding.agreementTextView.highlightColor = android.graphics.Color.TRANSPARENT
    }
    
    private fun showUserAgreement() {
        UiFeedback.toast(this, "跳转到用户协议页面")
        // TODO: 实现跳转到用户协议页面
        // val intent = Intent(this, UserAgreementActivity::class.java)
        // startActivity(intent)
    }
    
    private fun showPrivacyPolicy() {
        UiFeedback.toast(this, "跳转到隐私政策页面")
        // TODO: 实现跳转到隐私政策页面
        // val intent = Intent(this, PrivacyPolicyActivity::class.java)
        // startActivity(intent)
    }
    
    private fun showLoginOptions() {
        // 显示登录选项弹窗
        val dialog = LoginOptionsDialog()
        dialog.setOnLoginOptionSelectedListener(object : LoginOptionsDialog.OnLoginOptionSelectedListener {
            override fun onPhoneLoginSelected() {
                // 手机号登录 - 跳转到手机号登录页面
                val intent = Intent(this@LoginActivity, PhoneLoginActivity::class.java)
                startActivity(intent)
            }

            override fun onPasswordLoginSelected() {
                // 账号密码登录 - 显示登录卡片并切换到密码模式
                binding.exploreButton.visibility = View.GONE
                binding.loginCard.visibility = View.VISIBLE
                binding.loginCard.alpha = 0f
                binding.loginCard.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
                switchToPasswordLoginMode()
            }

            override fun onGuestModeSelected() {
                // 游客模式 - 直接进入主界面并显示提示
                enterGuestModeWithHint()
            }
        })
        dialog.show(supportFragmentManager, "LoginOptionsDialog")
    }
    
    private fun setupLoginModeSwitch() {
        // 手机号登录标签点击事件
        binding.phoneLoginTab.setOnClickListener {
            switchToPhoneLoginMode()
        }
        
        // 密码登录标签点击事件
        binding.passwordLoginTab.setOnClickListener {
            switchToPasswordLoginMode()
        }
    }
    
    private fun switchToPhoneLoginMode() {
        isPhoneLoginMode = true
        
        // 更新UI状态
        binding.phoneLoginTab.apply {
            setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
            setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.tab_selected_bg)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        binding.passwordLoginTab.apply {
            setTextColor(android.graphics.Color.parseColor("#A0A0B0"))
            setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.tab_normal_bg)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        
        // 显示/隐藏相应的输入框
        binding.phoneInputLayout.visibility = View.VISIBLE
        binding.usernameInputLayout.visibility = View.GONE
        
        // 清空输入框内容
        binding.phoneEditText.text?.clear()
        binding.usernameEditText.text?.clear()
        binding.passwordEditText.text?.clear()
    }
    
    private fun switchToPasswordLoginMode() {
        isPhoneLoginMode = false
        
        // 更新UI状态
        binding.passwordLoginTab.apply {
            setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
            setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.tab_selected_bg)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        
        binding.phoneLoginTab.apply {
            setTextColor(android.graphics.Color.parseColor("#A0A0B0"))
            setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.tab_normal_bg)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.NORMAL)
        }
        
        // 显示/隐藏相应的输入框
        binding.phoneInputLayout.visibility = View.GONE
        binding.usernameInputLayout.visibility = View.VISIBLE
        
        // 清空输入框内容
        binding.phoneEditText.text?.clear()
        binding.usernameEditText.text?.clear()
        binding.passwordEditText.text?.clear()
    }
    
    private fun validatePhoneLogin(phone: String, password: String): Boolean {
        if (TextUtils.isEmpty(phone)) {
            binding.phoneEditText.error = "请输入手机号"
            return false
        }
        
        if (!isValidPhoneNumber(phone)) {
            binding.phoneEditText.error = "手机号格式不正确"
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
        
        return true
    }
    
    private fun validatePasswordLogin(username: String, password: String): Boolean {
        if (TextUtils.isEmpty(username)) {
            binding.usernameEditText.error = "请输入用户名"
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
        
        return true
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^1[3-9]\\d{9}$"))
    }
    
    private fun performPhoneLogin(phone: String, password: String) {
        // 模拟登录过程
        binding.loginProgressBar.visibility = android.view.View.VISIBLE
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.loginProgressBar.visibility = android.view.View.GONE
            
            // 这里应该是实际的登录逻辑
            UiFeedback.toast(this, "手机号登录成功")
            AppSession.markLoggedInUser(this)
            navigateToMainActivity()
        }, 1500)
    }
    
    private fun performPasswordLogin(username: String, password: String) {
        // 模拟登录过程
        binding.loginProgressBar.visibility = android.view.View.VISIBLE
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            binding.loginProgressBar.visibility = android.view.View.GONE
            
            // 这里应该是实际的登录逻辑
            UiFeedback.toast(this, "密码登录成功")
            AppSession.markLoggedInUser(this)
            navigateToMainActivity()
        }, 1500)
    }
    
    private fun enterGuestMode() {
        AppSession.setGuest(this, true)
        UiFeedback.toast(this, "进入游客模式")
        navigateToMainActivity()
    }
    
    private fun enterGuestModeWithHint() {
        AppSession.setGuest(this, true)
        // 跳转到主界面
        val intent = Intent(this, MusicLibActivity::class.java)
        intent.putExtra("show_guest_hint", true)
        startActivity(intent)
        finish()
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MusicLibActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    
    private fun showForgotPasswordDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("忘记密码")
            .setMessage("请联系客服或通过注册手机号找回密码")
            .setPositiveButton("确定", null)
            .show()
    }
    
    companion object {
        private var doubleBackToExitPressedOnce = false
    }
}

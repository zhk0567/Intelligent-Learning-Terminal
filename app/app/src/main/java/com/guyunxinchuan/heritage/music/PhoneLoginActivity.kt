package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.guyunxinchuan.heritage.music.databinding.ActivityPhoneLoginBinding

/**
 * 手机号登录页面
 * 功能：手机号 + 验证码登录，包含获取验证码倒计时、输入校验等功能
 */
class PhoneLoginActivity : AppCompatActivity() {

    // ViewBinding实例，用于访问布局中的视图
    private lateinit var binding: ActivityPhoneLoginBinding
    
    // 倒计时器实例，用于验证码60秒倒计时
    private var countDownTimer: CountDownTimer? = null
    
    // 标记是否正在倒计时中
    private var isCountingDown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // 初始化ViewBinding并设置布局
        binding = ActivityPhoneLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化视图和事件监听
        setupViews()
        
        // 设置输入框内容变化监听，用于实时更新登录按钮状态
        setupInputValidation()
    }

    private fun setupViews() {
        // 获取验证码按钮点击事件
        binding.getCodeButton.setOnClickListener {
            if (!isCountingDown) {
                val phone = binding.phoneEditText.text.toString().trim()
                if (validatePhone(phone)) {
                    startCountdown()
                    UiFeedback.toast(this, "验证码已发送")
                }
            }
        }

        // 登录按钮点击事件
        binding.loginButton.setOnClickListener {
            val phone = binding.phoneEditText.text.toString().trim()
            val code = binding.codeEditText.text.toString().trim()

            if (validatePhone(phone) && validateCode(code)) {
                performLogin()
            }
        }
        
        // 设置输入框焦点监听
        setupInputFocusListeners()

        // 底部辅助文字点击事件
        setupBottomHelpText()
    }
    
    private fun setupInputFocusListeners() {
        // 手机号输入框焦点变化
        binding.phoneEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.phoneInputContainer.setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.input_field_bg_focused)
            } else {
                binding.phoneInputContainer.setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.input_field_bg)
            }
        }
        
        // 验证码输入框焦点变化
        binding.codeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.codeInputContainer.setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.input_field_bg_focused)
            } else {
                binding.codeInputContainer.setBackgroundResource(com.guyunxinchuan.heritage.music.R.drawable.input_field_bg)
            }
        }
    }

    /**
     * 设置输入框内容变化监听
     * 功能：实时检测手机号和验证码输入，动态更新登录按钮的可用性和透明度
     */
    private fun setupInputValidation() {
        // 监听手机号输入框内容变化
        binding.phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // 每次输入后更新登录按钮状态
                updateLoginButtonState()
            }
        })

        // 监听验证码输入框内容变化
        binding.codeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // 每次输入后更新登录按钮状态
                updateLoginButtonState()
            }
        })
    }

    /**
     * 更新登录按钮状态
     * 规则：手机号11位且验证码6位时，按钮可用且不透明；否则禁用且半透明
     */
    private fun updateLoginButtonState() {
        // 获取当前输入内容
        val phone = binding.phoneEditText.text.toString().trim()
        val code = binding.codeEditText.text.toString().trim()

        // 校验输入是否完整：手机号11位 + 验证码6位
        val isValid = phone.length == 11 && code.length == 6

        // 设置按钮可用性和透明度
        binding.loginButton.isEnabled = isValid
        binding.loginButton.alpha = if (isValid) 1.0f else 0.5f
    }

    /**
     * 校验手机号格式
     * @param phone 待校验的手机号字符串
     * @return true-格式正确，false-格式错误并显示Toast提示
     */
    private fun validatePhone(phone: String): Boolean {
        // 检查是否为空
        if (phone.isEmpty()) {
            UiFeedback.toast(this, "请输入手机号")
            return false
        }

        // 正则表达式校验：以1开头，第二位3-9，共11位数字
        if (!phone.matches(Regex("^1[3-9]\\d{9}$"))) {
            UiFeedback.toast(this, "手机号格式不正确")
            return false
        }

        return true
    }

    /**
     * 校验验证码格式
     * @param code 待校验的验证码字符串
     * @return true-格式正确，false-格式错误并显示Toast提示
     */
    private fun validateCode(code: String): Boolean {
        // 检查是否为空
        if (code.isEmpty()) {
            UiFeedback.toast(this, "请输入验证码")
            return false
        }

        // 检查长度是否为6位
        if (code.length != 6) {
            UiFeedback.toast(this, "验证码应为6位")
            return false
        }

        return true
    }

    /**
     * 启动60秒倒计时
     * 功能：禁用获取验证码按钮，显示倒计时文本，每秒更新剩余时间
     */
    private fun startCountdown() {
        // 标记正在倒计时中
        isCountingDown = true
        // 禁用按钮防止重复点击
        binding.getCodeButton.isEnabled = false

        // 创建60秒倒计时器，每1秒触发一次onTick
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // 计算剩余秒数
                val seconds = millisUntilFinished / 1000
                
                // 更新按钮文字和颜色（变灰）
                binding.getCodeButton.text = "${seconds}s后重新获取"
                binding.getCodeButton.setTextColor(Color.parseColor("#707080"))
                
                // 在验证码输入框右侧显示倒计时文本
                binding.countdownText.visibility = View.VISIBLE
                binding.countdownText.text = "${seconds}s"
            }

            override fun onFinish() {
                // 倒计时结束，恢复初始状态
                isCountingDown = false
                binding.getCodeButton.text = "获取验证码"
                binding.getCodeButton.setTextColor(Color.parseColor("#3A86FF"))
                binding.getCodeButton.isEnabled = true
                
                // 隐藏倒计时文本
                binding.countdownText.visibility = View.GONE
                binding.countdownText.text = ""
            }
        }.start()
    }

    /**
     * 执行登录操作
     * 功能：显示登录成功提示，跳转到主界面（MusicLibActivity）
     */
    private fun performLogin() {
        // 显示登录成功Toast提示
        UiFeedback.toast(this, "登录成功")
        
        // 创建跳转到主界面的Intent
        val intent = Intent(this, MusicLibActivity::class.java)
        startActivity(intent)
        
        // 关闭当前登录页面
        finish()
    }

    /**
     * 设置底部辅助文字（忘记密码、其他登录方式）
     * 功能：使用SpannableString实现部分文本可点击，点击后执行相应操作
     */
    private fun setupBottomHelpText() {
        val fullText = "忘记密码？ | 其他登录方式"
        val spannableString = android.text.SpannableString(fullText)
    
        // 找到“忘记密码？”的起始和结束位置
        val forgotPasswordStart = fullText.indexOf("忘记密码？")
        val forgotPasswordEnd = forgotPasswordStart + "忘记密码？".length
    
        // 找到“其他登录方式”的起始和结束位置
        val otherLoginStart = fullText.indexOf("其他登录方式")
        val otherLoginEnd = otherLoginStart + "其他登录方式".length
    
        // 设置“忘记密码？”为可点击区域
        if (forgotPasswordStart != -1) {
            spannableString.setSpan(
                object : android.text.style.ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 点击“忘记密码？”时显示找回密码对话框
                        showForgotPasswordDialog()
                    }
    
                    override fun updateDrawState(ds: android.text.TextPaint) {
                        super.updateDrawState(ds)
                        // 设置淡金色，无下划线
                        ds.color = Color.parseColor("#D4AF37")
                        ds.isUnderlineText = false
                    }
                },
                forgotPasswordStart,
                forgotPasswordEnd,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    
        // 设置“其他登录方式”为可点击区域
        if (otherLoginStart != -1) {
            spannableString.setSpan(
                object : android.text.style.ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 点击"其他登录方式"时返回上一页
                        onBackPressedDispatcher.onBackPressed()
                    }
    
                    override fun updateDrawState(ds: android.text.TextPaint) {
                        super.updateDrawState(ds)
                        // 设置淡金色，无下划线
                        ds.color = Color.parseColor("#D4AF37")
                        ds.isUnderlineText = false
                    }
                },
                otherLoginStart,
                otherLoginEnd,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // 设置"|"分隔符颜色
        val separatorIndex = fullText.indexOf("|")
        if (separatorIndex != -1) {
            spannableString.setSpan(
                android.text.style.ForegroundColorSpan(Color.parseColor("#A0A0B0")),
                separatorIndex,
                separatorIndex + 1,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.bottomHelpText.text = spannableString
        binding.bottomHelpText.movementMethod = android.text.method.LinkMovementMethod.getInstance()
        binding.bottomHelpText.highlightColor = Color.TRANSPARENT
    }

    /**
     * 显示找回密码对话框
     * 功能：提示用户通过手机号验证码重置密码
     */
    private fun showForgotPasswordDialog() {
        // 创建AlertDialog并显示找回密码提示信息
        android.app.AlertDialog.Builder(this)
            .setTitle("忘记密码")
            .setMessage("请通过手机号验证码重置密码")
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // 清理倒计时器，防止内存泄漏
        countDownTimer?.cancel()
        countDownTimer = null
    }
}

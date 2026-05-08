package com.guyunxinchuan.heritage.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        loadUserInfo()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnClickListener {
            saveUserInfo()
        }
    }

    private fun loadUserInfo() {
        binding.etNickname.setText("用户123456")
        binding.etPhone.setText("138****8888")
        binding.etEmail.setText("user@example.com")
        binding.etBio.setText("热爱传统文化的音乐爱好者")
    }

    private fun saveUserInfo() {
        val nickname = binding.etNickname.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()

        if (nickname.isEmpty()) {
            binding.etNickname.error = "请输入昵称"
            return
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "请输入手机号"
            return
        }

        UiFeedback.toast(this, "保存成功")
        finish()
    }
}

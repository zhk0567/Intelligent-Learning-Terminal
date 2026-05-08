package com.guyunxinchuan.heritage.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.guyunxinchuan.heritage.music.databinding.DialogLoginOptionsBinding

/**
 * 登录选项底部弹窗
 * 功能：提供三种登录方式选择 - 手机号登录、账号密码登录、游客模式
 */
class LoginOptionsDialog : BottomSheetDialogFragment() {

    // ViewBinding实例，用于访问弹窗布局中的视图
    private var _binding: DialogLoginOptionsBinding? = null
    private val binding get() = _binding!!

    /**
     * 登录选项选择监听器接口
     * 定义三种登录方式的选择回调方法
     */
    interface OnLoginOptionSelectedListener {
        fun onPhoneLoginSelected()      // 手机号登录被选中
        fun onPasswordLoginSelected()   // 账号密码登录被选中
        fun onGuestModeSelected()       // 游客模式被选中
    }

    // 监听器实例，用于回调登录选项选择事件
    private var listener: OnLoginOptionSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 初始化ViewBinding并返回根视图
        _binding = DialogLoginOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置点击事件监听
        setupClickListeners()
    }

    /**
     * 设置三个登录选项的点击事件监听
     * 每个选项点击后调用对应的回调方法并关闭弹窗
     */
    private fun setupClickListeners() {
        // 选项1：手机号登录（推荐）
        binding.optionPhone.setOnClickListener {
            listener?.onPhoneLoginSelected()
            dismiss()  // 关闭弹窗
        }

        // 选项2：账号密码登录
        binding.optionPassword.setOnClickListener {
            listener?.onPasswordLoginSelected()
            dismiss()  // 关闭弹窗
        }

        // 选项3：游客模式
        binding.optionGuest.setOnClickListener {
            listener?.onGuestModeSelected()
            dismiss()  // 关闭弹窗
        }
    }

    /**
     * 设置登录选项选择监听器
     * @param listener 监听器实例，处理三种登录方式的选择事件
     */
    fun setOnLoginOptionSelectedListener(listener: OnLoginOptionSelectedListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 清理binding引用，防止内存泄漏
        _binding = null
    }
}

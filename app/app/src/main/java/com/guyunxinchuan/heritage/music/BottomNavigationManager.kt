package com.guyunxinchuan.heritage.music

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class BottomNavigationManager(
    private val context: Context,
    private val currentActivity: String
) {
    private val activeColor: Int
        get() = ContextCompat.getColor(context, R.color.neon_teal)

    private val inactiveColor: Int
        get() = ContextCompat.getColor(context, R.color.nav_inactive)

    private var navMusicIcon: ImageView? = null
    private var navMusicText: TextView? = null
    private var navMusicIndicator: View? = null

    private var navStoryIcon: ImageView? = null
    private var navStoryText: TextView? = null
    private var navStoryIndicator: View? = null

    private var navCreateIcon: ImageView? = null
    private var navCreateText: TextView? = null
    private var navCreateIndicator: View? = null

    private var navShopIcon: ImageView? = null
    private var navShopText: TextView? = null
    private var navShopIndicator: View? = null

    private var navProfileIcon: ImageView? = null
    private var navProfileText: TextView? = null
    private var navProfileIndicator: View? = null

    fun setupNavigation(view: View) {
        navMusicIcon      = view.findViewById(R.id.nav_music_icon)
        navMusicText      = view.findViewById(R.id.nav_music_text)
        navMusicIndicator = view.findViewById(R.id.nav_music_indicator)

        navStoryIcon      = view.findViewById(R.id.nav_story_icon)
        navStoryText      = view.findViewById(R.id.nav_story_text)
        navStoryIndicator = view.findViewById(R.id.nav_story_indicator)

        navCreateIcon      = view.findViewById(R.id.nav_create_icon)
        navCreateText      = view.findViewById(R.id.nav_create_text)
        navCreateIndicator = view.findViewById(R.id.nav_create_indicator)

        navShopIcon      = view.findViewById(R.id.nav_shop_icon)
        navShopText      = view.findViewById(R.id.nav_shop_text)
        navShopIndicator = view.findViewById(R.id.nav_shop_indicator)

        navProfileIcon      = view.findViewById(R.id.nav_profile_icon)
        navProfileText      = view.findViewById(R.id.nav_profile_text)
        navProfileIndicator = view.findViewById(R.id.nav_profile_indicator)

        updateNavigationState()
        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<View>(R.id.nav_music).setOnClickListener {
            if (currentActivity != "MusicLibActivity") {
                animateClick(it)
                switchToPeerTab(MusicLibActivity::class.java)
            }
        }
        view.findViewById<View>(R.id.nav_story).setOnClickListener {
            if (currentActivity != "StoryActivity") {
                animateClick(it)
                switchToPeerTab(StoryActivity::class.java)
            }
        }
        view.findViewById<View>(R.id.nav_create).setOnClickListener {
            if (currentActivity != "CreateActivity") {
                animateClick(it)
                switchToPeerTab(CreateActivity::class.java)
            }
        }
        view.findViewById<View>(R.id.nav_shop).setOnClickListener {
            if (currentActivity != "ShopActivity") {
                animateClick(it)
                switchToPeerTab(ShopActivity::class.java)
            }
        }
        view.findViewById<View>(R.id.nav_profile).setOnClickListener {
            if (currentActivity != "ProfileActivity") {
                animateClick(it)
                switchToPeerTab(ProfileActivity::class.java)
            }
        }
    }

    /**
     * 五个主导航之间为平级切换：结束当前 Tab 页并启动目标 Tab，使用淡入淡出替代默认「推进」动画。
     */
    private fun switchToPeerTab(target: Class<out Activity>) {
        val act = context as? Activity ?: return
        act.startActivity(Intent(context, target))
        @Suppress("DEPRECATION")
        when {
            // 进出商城 Tab：无过渡（与商城子页面主题一致）
            target == ShopActivity::class.java || act.javaClass == ShopActivity::class.java ->
                act.overridePendingTransition(0, 0)
            else -> applyPeerTabTransition(act)
        }
        act.finish()
    }

    @Suppress("DEPRECATION")
    private fun applyPeerTabTransition(act: Activity) {
        act.overridePendingTransition(R.anim.nav_peer_fade_in, R.anim.nav_peer_fade_out)
    }

    private fun updateNavigationState() {
        resetItem(navMusicIcon,   navMusicText,   navMusicIndicator)
        resetItem(navStoryIcon,   navStoryText,   navStoryIndicator)
        resetItem(navCreateIcon,  navCreateText,  navCreateIndicator)
        resetItem(navShopIcon,    navShopText,    navShopIndicator)
        resetItem(navProfileIcon, navProfileText, navProfileIndicator)

        when (currentActivity) {
            "MusicLibActivity" -> activateItem(navMusicIcon,   navMusicText,   navMusicIndicator)
            "StoryActivity"    -> activateItem(navStoryIcon,   navStoryText,   navStoryIndicator)
            "CreateActivity"   -> activateItem(navCreateIcon,  navCreateText,  navCreateIndicator)
            "ShopActivity"     -> activateItem(navShopIcon,    navShopText,    navShopIndicator)
            "ProfileActivity"  -> activateItem(navProfileIcon, navProfileText, navProfileIndicator)
        }
    }

    private fun activateItem(icon: ImageView?, label: TextView?, indicator: View?) {
        icon?.imageTintList = ColorStateList.valueOf(activeColor)
        label?.setTextColor(activeColor)

        indicator?.setBackgroundResource(R.drawable.nav_indicator)

        icon?.let {
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(it, "scaleX", 0.85f, 1.12f, 1.0f),
                    ObjectAnimator.ofFloat(it, "scaleY", 0.85f, 1.12f, 1.0f)
                )
                duration = 300
                interpolator = OvershootInterpolator(2.0f)
                start()
            }
        }

        indicator?.let {
            it.visibility = View.VISIBLE
            it.scaleX = 0f
            ObjectAnimator.ofFloat(it, "scaleX", 0f, 1f).apply {
                duration = 220
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }

    private fun resetItem(icon: ImageView?, label: TextView?, indicator: View?) {
        icon?.imageTintList = ColorStateList.valueOf(inactiveColor)
        label?.setTextColor(inactiveColor)
        indicator?.setBackgroundResource(R.drawable.nav_indicator)
        // GONE 不占位，避免底栏图标下方出现一条空白带（INVISIBLE 仍占位）
        indicator?.visibility = View.GONE
    }

    private fun animateClick(view: View) {
        val down = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.93f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.93f)
            )
            duration = 80
        }
        val up = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1.0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.0f)
            )
            duration = 130
            interpolator = OvershootInterpolator(2.5f)
        }
        down.start()
        down.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                up.start()
            }
        })
    }
}

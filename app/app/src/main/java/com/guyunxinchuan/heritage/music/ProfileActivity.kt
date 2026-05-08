package com.guyunxinchuan.heritage.music

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        MallWindowInsets.applyToActivity(
            this,
            findViewById(R.id.drawer_layout),
            findViewById(R.id.profile_main_host),
            findViewById(R.id.bottom_navigation),
            findViewById(R.id.profile_scroll),
            12f,
        )

        drawerLayout = findViewById(R.id.drawer_layout)

        findViewById<LinearLayout>(R.id.learn1).setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.learn2).setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.learn4).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.learn5).setOnClickListener {
            val intent = Intent(this, MyWorksActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.learn6).setOnClickListener {
            val intent = Intent(this, MyLearningActivity::class.java)
            startActivity(intent)
        }
        findViewById<LinearLayout>(R.id.learn3).setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        setupDrawerActions()

        val bottomNav = findViewById<LinearLayout>(R.id.bottom_navigation)
        BottomNavigationManager(this, "ProfileActivity").setupNavigation(bottomNav)
    }

    override fun onResume() {
        super.onResume()
        applyProfileHeader()
    }

    private fun applyProfileHeader() {
        val guest = AppSession.isGuest(this)
        findViewById<TextView>(R.id.profile_username).text =
            if (guest) "游客" else "非遗文化爱好者"
        findViewById<TextView>(R.id.profile_bio).text =
            if (guest) "登录解锁更多功能" else "已关注4 | 粉丝12"
        findViewById<View>(R.id.profile_vip_badge).visibility =
            if (guest) View.GONE else View.VISIBLE
    }

    private fun setupDrawerActions() {
        val drawer = findViewById<LinearLayout>(R.id.nav_drawer)

        drawer.findViewById<LinearLayout>(R.id.drawer_edit_profile).setOnClickListener {
            drawerLayout.closeDrawers()
            UiFeedback.toast(this, "修改个人信息")
        }
        drawer.findViewById<LinearLayout>(R.id.drawer_switch_account).setOnClickListener {
            drawerLayout.closeDrawers()
            UiFeedback.toast(this, "切换账号")
        }
        drawer.findViewById<LinearLayout>(R.id.drawer_settings).setOnClickListener {
            drawerLayout.closeDrawers()
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        drawer.findViewById<LinearLayout>(R.id.drawer_logout).setOnClickListener {
            drawerLayout.closeDrawers()
            UiFeedback.toast(this, "已退出登录")
        }
    }
}

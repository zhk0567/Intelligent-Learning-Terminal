package com.intangibleheritage.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.intangibleheritage.music.core.ui.theme.HeritageMusicTheme

/** 应用入口；Compose 导航与底栏策略见 [HeritageApp]、[HeritageNavigation]。 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeritageMusicTheme {
                HeritageApp()
            }
        }
    }
}

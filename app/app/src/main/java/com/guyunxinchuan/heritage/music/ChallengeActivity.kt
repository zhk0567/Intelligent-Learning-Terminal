package com.guyunxinchuan.heritage.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityChallengeBinding

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        loadChallenges()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.joinChallengeBtn.setOnClickListener {
            UiFeedback.toast(this, "打开投稿页面")
        }
    }

    private fun loadChallenges() {
        binding.challengeTitle.text = "非遗音乐改编大赛"
        binding.challengeDescription.text =
            "将传统非遗音乐与现代元素融合，创作出独特的音乐作品。展示你的创意和才华。"
        binding.participantsCount.text = "256"
        binding.daysLeft.text = "15"
    }
}

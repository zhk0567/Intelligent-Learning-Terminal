package com.guyunxinchuan.heritage.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guyunxinchuan.heritage.music.databinding.ActivityCreatorProfileBinding

class CreatorProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatorProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MallWindowInsets.applyRootOnly(this, binding.root, 0f)

        setupViews()
        loadCreatorInfo()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.followButton.setOnClickListener {
            toggleFollow()
        }
    }

    private fun loadCreatorInfo() {
        binding.creatorName.text = "音乐探索者"
        binding.creatorBio.text = "热爱传统文化的音乐创作者，专注于非遗音乐与现代音乐的融合。"
        binding.worksCount.text = "12"
        binding.followersCount.text = "1.2k"
        binding.followingCount.text = "89"
    }

    private fun toggleFollow() {
        if (binding.followButton.text == "关注") {
            binding.followButton.text = "已关注"
            binding.followButton.setBackgroundResource(R.drawable.tag_bg_normal)
        } else {
            binding.followButton.text = "关注"
            binding.followButton.setBackgroundResource(R.drawable.tag_bg_selected)
        }
    }
}

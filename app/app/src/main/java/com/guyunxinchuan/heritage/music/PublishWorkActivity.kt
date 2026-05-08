package com.guyunxinchuan.heritage.music

import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.guyunxinchuan.heritage.music.databinding.ActivityPublishWorkBinding

class PublishWorkActivity : BaseActivity() {

    private lateinit var binding: ActivityPublishWorkBinding
    private val selectedTags = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublishWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.publishButton.setOnClickListenerThrottled {
            publishWork()
        }

        binding.uploadAudioBtn.setOnClickListenerThrottled {
            UiFeedback.toast(this, "演示环境：未接入真实文件选择，填写信息后可直接发布")
        }

        setupTagSelection()
    }

    private fun setupTagSelection() {
        val pairs = listOf(
            binding.tagTraditional to "传统",
            binding.tagElectronic to "电子",
            binding.tagFusion to "融合",
            binding.tagInstrumental to "器乐",
            binding.tagVocal to "声乐",
            binding.tagRemix to "改编"
        )
        pairs.forEach { (view, label) ->
            applyTagUnselected(view)
            view.setOnClickListener {
                if (selectedTags.contains(label)) {
                    selectedTags.remove(label)
                    applyTagUnselected(view)
                } else {
                    selectedTags.add(label)
                    applyTagSelected(view)
                }
            }
        }
    }

    private fun applyTagSelected(tag: TextView) {
        tag.setBackgroundResource(R.drawable.create_tag_selected)
        tag.setTextColor(ContextCompat.getColor(this, R.color.text_on_light_primary))
    }

    private fun applyTagUnselected(tag: TextView) {
        tag.setBackgroundResource(R.drawable.create_tag_normal)
        tag.setTextColor(ContextCompat.getColor(this, R.color.shop_text_muted))
    }

    private fun publishWork() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "请输入作品标题"
            binding.etTitle.requestFocus()
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "请输入作品介绍"
            binding.etDescription.requestFocus()
            return
        }

        UserPublishedContent.publishWork(title, description, selectedTags.toSet())

        UiFeedback.toast(this, "发布成功，已在创作社区与个人作品展示")
        setResult(RESULT_OK)
        finish()
    }
}

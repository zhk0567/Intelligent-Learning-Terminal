package com.intangibleheritage.music.core.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AudioTrack(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val coverImageRes: Int,
    /** 流媒体示例 URL（需 INTERNET）；可改为 file/raw */
    val streamUrl: String
)

package com.intangibleheritage.music.core.data.repository

import com.intangibleheritage.music.core.data.model.HeritageArchiveAsset

interface ArchiveRepository {
    fun allAssets(): List<HeritageArchiveAsset>
    fun assetById(id: String): HeritageArchiveAsset?
}

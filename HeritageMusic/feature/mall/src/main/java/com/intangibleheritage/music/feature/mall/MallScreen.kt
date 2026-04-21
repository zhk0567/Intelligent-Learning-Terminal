package com.intangibleheritage.music.feature.mall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.MallSection
import com.intangibleheritage.music.core.data.model.Product
import com.intangibleheritage.music.core.resources.R

@Composable
fun MallScreen(
    onProductClick: (String) -> Unit,
    onOpenSection: (MallSection) -> Unit
) {
    val products = remember { AppRepositories.mall.products() }
    val sections = remember {
        listOf(
            MallSectionMeta(
                titleRes = R.string.mall_section_story_title,
                descRes = R.string.mall_section_story_desc,
                section = MallSection.Story,
                highlightProductId = "dunhuang_magnet"
            ),
            MallSectionMeta(
                titleRes = R.string.mall_section_cultural_title,
                descRes = R.string.mall_section_cultural_desc,
                section = MallSection.Cultural,
                highlightProductId = "silk_scarf"
            ),
            MallSectionMeta(
                titleRes = R.string.mall_section_cross_title,
                descRes = R.string.mall_section_cross_desc,
                section = MallSection.Cross,
                highlightProductId = "bronze_bells"
            ),
            MallSectionMeta(
                titleRes = R.string.mall_section_instrument_title,
                descRes = R.string.mall_section_instrument_desc,
                section = MallSection.Instrument,
                highlightProductId = "pipa_bookmark"
            )
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            Text(
                text = stringResource(R.string.mall_section_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        item {
            Text(
                text = stringResource(R.string.mall_section_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(sections, key = { it.section.name }) { section ->
            MallSectionCard(
                section = section,
                previewProducts = remember(section.section) {
                    AppRepositories.mall.productsInSection(section.section).take(3)
                },
                onOpenSection = { onOpenSection(section.section) },
                onHighlightProduct = { onProductClick(section.highlightProductId) },
                onPreviewProduct = onProductClick
            )
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.mall_section_products),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(products.chunked(2), key = { row -> row.joinToString("-") { it.id } }) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MallProductCard(
                    product = row[0],
                    onClick = { onProductClick(row[0].id) },
                    modifier = Modifier.weight(1f)
                )
                if (row.size > 1) {
                    MallProductCard(
                        product = row[1],
                        onClick = { onProductClick(row[1].id) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class MallSectionMeta(
    val titleRes: Int,
    val descRes: Int,
    val section: MallSection,
    val highlightProductId: String
)

@Composable
private fun MallSectionCard(
    section: MallSectionMeta,
    previewProducts: List<Product>,
    onOpenSection: () -> Unit,
    onHighlightProduct: () -> Unit,
    onPreviewProduct: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(section.titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(section.descRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                previewProducts.forEach { product ->
                    MallProductCard(
                        product = product,
                        onClick = { onPreviewProduct(product.id) },
                        modifier = Modifier.width(148.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpenSection) {
                    Text(stringResource(R.string.mall_section_enter_zone))
                }
                OutlinedButton(onClick = onHighlightProduct) {
                    Text(stringResource(R.string.mall_section_open_goods))
                }
            }
        }
    }
}

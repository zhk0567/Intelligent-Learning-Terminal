package com.intangibleheritage.music.feature.mall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.model.MallSection
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.theme.ScreenLayout

private enum class MallSortMode {
    Default,
    PriceAsc,
    PriceDesc,
    RatingDesc
}

@Composable
fun MallSectionListScreen(
    section: MallSection,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit
) {
    val context = LocalContext.current
    val products = remember(section) { AppRepositories.mall.productsInSection(section) }
    var query by remember { mutableStateOf("") }
    var sortMode by remember { mutableStateOf(MallSortMode.Default) }

    val titleRes = when (section) {
        MallSection.Story -> R.string.mall_section_story_title
        MallSection.Cultural -> R.string.mall_section_cultural_title
        MallSection.Cross -> R.string.mall_section_cross_title
        MallSection.Instrument -> R.string.mall_section_instrument_title
    }

    val filtered = remember(products, query, sortMode) {
        val q = query.trim()
        var list = products.filter { p ->
            q.isEmpty() || context.getString(p.titleRes).contains(q, ignoreCase = true)
        }
        list = when (sortMode) {
            MallSortMode.Default -> list.sortedBy { context.getString(it.titleRes) }
            MallSortMode.PriceAsc -> list.sortedBy { it.priceYuan }
            MallSortMode.PriceDesc -> list.sortedByDescending { it.priceYuan }
            MallSortMode.RatingDesc -> list.sortedByDescending { it.rating }
        }
        list
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeritageSecondaryTopBar(
                title = stringResource(titleRes),
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = ScreenLayout.HorizontalPadding),
            contentPadding = PaddingValues(bottom = ScreenLayout.BottomSpacing),
            verticalArrangement = Arrangement.spacedBy(ScreenLayout.GroupSpacing)
        ) {
            item {
                Text(
                    text = stringResource(R.string.mall_section_list_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ScreenLayout.TopSpacing)
                )
                Spacer(modifier = Modifier.height(8.dp))
                MallSectionFilterBar(
                    query = query,
                    onQueryChange = { query = it },
                    sortMode = sortMode,
                    onSortChange = { sortMode = it },
                    onClear = {
                        query = ""
                        sortMode = MallSortMode.Default
                    }
                )
            }
            items(filtered, key = { it.id }) { product ->
                MallProductCard(
                    product = product,
                    onClick = { onOpenProduct(product.id) }
                )
            }
        }
    }
}

@Composable
private fun MallSectionFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    sortMode: MallSortMode,
    onSortChange: (MallSortMode) -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.mall_filter_panel_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.mall_section_search_hint)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.section_actions),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onClear) {
                    Text(stringResource(R.string.mall_filter_clear), color = MaterialTheme.colorScheme.primary)
                }
            }
            val scroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MallSortChip(
                    label = stringResource(R.string.mall_sort_default),
                    selected = sortMode == MallSortMode.Default,
                    onClick = { onSortChange(MallSortMode.Default) }
                )
                MallSortChip(
                    label = stringResource(R.string.mall_sort_price_asc),
                    selected = sortMode == MallSortMode.PriceAsc,
                    onClick = { onSortChange(MallSortMode.PriceAsc) }
                )
                MallSortChip(
                    label = stringResource(R.string.mall_sort_price_desc),
                    selected = sortMode == MallSortMode.PriceDesc,
                    onClick = { onSortChange(MallSortMode.PriceDesc) }
                )
                MallSortChip(
                    label = stringResource(R.string.mall_sort_rating),
                    selected = sortMode == MallSortMode.RatingDesc,
                    onClick = { onSortChange(MallSortMode.RatingDesc) }
                )
            }
        }
    }
}

@Composable
private fun MallSortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

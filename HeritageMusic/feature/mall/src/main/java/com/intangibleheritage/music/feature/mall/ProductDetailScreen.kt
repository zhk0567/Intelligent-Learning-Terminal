package com.intangibleheritage.music.feature.mall

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.intangibleheritage.music.core.data.AppRepositories
import com.intangibleheritage.music.core.data.RuntimePerformanceConfig
import com.intangibleheritage.music.core.data.model.Product
import com.intangibleheritage.music.core.resources.R
import com.intangibleheritage.music.core.ui.navigation.HeritageSecondaryTopBar
import com.intangibleheritage.music.core.ui.navigation.InvalidDeepLinkScreen
import com.intangibleheritage.music.core.ui.theme.BorderTeal
import com.intangibleheritage.music.core.ui.theme.PrimaryTeal
import com.intangibleheritage.music.core.ui.theme.SurfaceCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SwatchGold = Color(0xFFFFD54F)
private val SwatchRed = Color(0xFFC62828)
private val SwatchGreen = Color(0xFF1B5E20)
private val SwatchBlue = Color(0xFF0D47A1)

private sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Content(val product: Product) : ProductDetailUiState
    data object Empty : ProductDetailUiState
    data object Error : ProductDetailUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMsg = stringResource(R.string.mall_demo_snackbar)
    var detailState by remember(productId) { mutableStateOf<ProductDetailUiState>(ProductDetailUiState.Loading) }
    var selectedSwatch by remember { mutableIntStateOf(0) }
    val swatches = listOf(SwatchGold, SwatchRed, SwatchGreen, SwatchBlue)
    val retryLoad: () -> Unit = remember(productId, scope) {
        {
            scope.launch {
                detailState = ProductDetailUiState.Loading
                if (RuntimePerformanceConfig.enableFakeDelay) {
                    delay(240)
                }
                detailState = runCatching {
                    if (productId == "simulate_error") {
                        error("simulate detail error")
                    }
                    AppRepositories.mall.productById(productId)
                }.fold(
                    onSuccess = { product ->
                        if (product == null) ProductDetailUiState.Empty else ProductDetailUiState.Content(product)
                    },
                    onFailure = { ProductDetailUiState.Error }
                )
            }
            Unit
        }
    }
    LaunchedEffect(productId) {
        retryLoad()
        AppRepositories.profile.addHistory(productId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            HeritageSecondaryTopBar(
                title = when (val state = detailState) {
                    is ProductDetailUiState.Content -> stringResource(state.product.titleRes)
                    else -> stringResource(R.string.nav_mall)
                },
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (detailState !is ProductDetailUiState.Content) return@Scaffold
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch { snackbarHostState.showSnackbar(demoMsg) }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.mall_add_cart))
                }
                Button(
                    onClick = {
                        scope.launch { snackbarHostState.showSnackbar(demoMsg) }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.mall_buy_now))
                }
            }
        }
    ) { padding ->
        when (val state = detailState) {
            ProductDetailUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.mall_detail_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            ProductDetailUiState.Empty -> {
                InvalidDeepLinkScreen(
                    title = stringResource(R.string.nav_invalid_title),
                    message = stringResource(R.string.mall_detail_empty),
                    onBack = onBack,
                    modifier = Modifier.padding(padding)
                )
            }
            ProductDetailUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.mall_detail_error),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = retryLoad) {
                        Text(stringResource(R.string.mall_detail_retry))
                    }
                }
            }
            is ProductDetailUiState.Content -> {
                ProductDetailContent(
                    product = state.product,
                    selectedSwatch = selectedSwatch,
                    swatches = swatches,
                    onSelectSwatch = { selectedSwatch = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    selectedSwatch: Int,
    swatches: List<Color>,
    onSelectSwatch: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        AsyncImage(
            model = product.imageRes,
            contentDescription = stringResource(product.titleRes),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${product.priceYuan}${stringResource(R.string.currency_yuan)}",
            style = MaterialTheme.typography.headlineMedium,
            color = BorderTeal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = PrimaryTeal,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "${product.rating} (${stringResource(R.string.reviews_count, product.reviewCount)})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.section_description),
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryTeal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(product.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.section_color),
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryTeal
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            swatches.forEachIndexed { index, swatchColor ->
                val selected = selectedSwatch == index
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(swatchColor)
                        .border(
                            width = if (selected) 3.dp else 1.dp,
                            color = if (selected) PrimaryTeal else Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { onSelectSwatch(index) }
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        SpecSection(
            titleRes = R.string.section_product_specs,
            body = stringResource(specResFor(product))
        )
        Spacer(modifier = Modifier.height(16.dp))
        SpecSection(
            titleRes = R.string.section_delivery,
            body = stringResource(R.string.product_delivery_info)
        )
        Spacer(modifier = Modifier.height(16.dp))
        SpecSection(
            titleRes = R.string.section_after_sales,
            body = stringResource(R.string.product_after_sales_info)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.section_reviews),
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryTeal
        )
        Spacer(modifier = Modifier.height(10.dp))
        ReviewCard(
            author = stringResource(R.string.product_review_1),
            content = stringResource(R.string.product_review_1_text),
            stars = 5
        )
        Spacer(modifier = Modifier.height(10.dp))
        ReviewCard(
            author = stringResource(R.string.product_review_2),
            content = stringResource(R.string.product_review_2_text),
            stars = 4
        )
        Spacer(modifier = Modifier.height(10.dp))
        ReviewCard(
            author = stringResource(R.string.product_review_3),
            content = stringResource(R.string.product_review_3_text),
            stars = 4
        )
    }
}

@Composable
private fun SpecSection(
    titleRes: Int,
    body: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryTeal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun specResFor(product: Product): Int = when (product.id) {
    "dunhuang_magnet" -> R.string.product_spec_dunhuang
    "bronze_bells" -> R.string.product_spec_bronze
    "silk_scarf" -> R.string.product_spec_silk
    "pipa_bookmark" -> R.string.product_spec_pipa
    else -> R.string.product_spec_dunhuang
}

@Composable
private fun ReviewCard(author: String, content: String, stars: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderTeal.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.size(8.dp))
                repeat(5) { i ->
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (i < stars) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

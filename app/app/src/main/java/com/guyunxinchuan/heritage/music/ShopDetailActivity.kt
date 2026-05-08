package com.guyunxinchuan.heritage.music

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import java.text.NumberFormat
import java.util.Locale

class ShopDetailActivity : AppCompatActivity() {

    private var selectedSpec = "spec_standard"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_shop_detail)

        MallWindowInsets.applyToActivity(
            this,
            findViewById(R.id.shopDetailRoot),
            findViewById(R.id.shopDetailTopInsetHost),
            findViewById(R.id.shopDetailBottomBar),
            findViewById(R.id.detailScroll),
            88f,
        )

        val name = intent.getStringExtra("product_name") ?: "商品名称"
        val price = intent.getStringExtra("product_price") ?: "¥0"
        val rating = intent.getStringExtra("product_rating") ?: "4.9"
        val coverId = intent.getIntExtra("product_image", 0)
        val heroExtra = intent.getIntExtra("product_hero", 0)
        val imageResId = when {
            heroExtra != 0 -> heroExtra
            coverId != 0 -> coverId
            else -> R.drawable.banner2_img
        }
        val description = intent.getStringExtra("product_desc")
            ?: "创意礼物博物馆文创非遗伴手礼，采用天然材料精雕细刻，融合传统非遗元素，每一件都是独一无二的艺术品。"
        val sales = intent.getIntExtra("product_sales", -1)

        findViewById<TextView>(R.id.detail_name).text = name
        findViewById<TextView>(R.id.detail_price).text = price
        findViewById<TextView>(R.id.detail_rating).text = formatRatingForPill(rating)
        findViewById<ShapeableImageView>(R.id.detail_image).loadCover(imageResId, CoverPreset.Hero)
        findViewById<TextView>(R.id.detail_description).text = description

        findViewById<TextView>(R.id.detail_sales).text = formatSalesForPill(sales)

        findViewById<TextView>(R.id.detail_original_price).visibility = View.GONE

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener { finish() }

        val desc = findViewById<TextView>(R.id.detail_description)
        val readMore = findViewById<TextView>(R.id.detail_read_more)
        desc.maxLines = 4
        readMore.setOnClickListener {
            if (desc.maxLines == Int.MAX_VALUE) {
                desc.maxLines = 4
                readMore.text = "展开全文"
            } else {
                desc.maxLines = Int.MAX_VALUE
                readMore.text = "收起"
            }
        }

        setupSpecSelection()

        findViewById<LinearLayout>(R.id.btn_add_cart).setOnClickListener {
            ShopCartStore.addItems(this, 1)
            UiFeedback.toast(this, "已加入购物车")
        }
        findViewById<TextView>(R.id.btn_buy_now).setOnClickListener {
            UiFeedback.toast(this, "正在跳转结算…")
        }
    }

    private fun formatRatingForPill(rating: String): String {
        val rt = rating.trim()
        return when {
            rt.endsWith("分") -> rt
            else -> "$rt 分"
        }
    }

    private fun formatSalesForPill(sales: Int): String {
        if (sales < 0) return "—"
        val nf = NumberFormat.getIntegerInstance(Locale.CHINA)
        return "${nf.format(sales.toLong())} 件"
    }

    private fun setupSpecSelection() {
        val specStandard = findViewById<TextView>(R.id.spec_standard)
        val specDeluxe = findViewById<TextView>(R.id.spec_deluxe)
        val neon = ContextCompat.getColor(this, R.color.neon_teal)

        fun select(selected: TextView, other: TextView) {
            selected.setBackgroundResource(R.drawable.shop_detail_spec_selected)
            selected.setTextColor(0xFF000000.toInt())
            selected.textSize = 14f
            other.setBackgroundResource(R.drawable.shop_detail_spec_normal)
            other.setTextColor(neon)
            other.textSize = 14f
        }

        select(specStandard, specDeluxe)
        specStandard.setOnClickListener {
            selectedSpec = "spec_standard"
            select(specStandard, specDeluxe)
        }
        specDeluxe.setOnClickListener {
            selectedSpec = "spec_deluxe"
            select(specDeluxe, specStandard)
        }
    }
}

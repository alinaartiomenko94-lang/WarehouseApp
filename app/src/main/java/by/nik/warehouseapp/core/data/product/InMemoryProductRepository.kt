package by.nik.warehouseapp.core.data.product

import by.nik.warehouseapp.R
import by.nik.warehouseapp.features.returns.model.ProductItem

object InMemoryProductRepository {

    private val items = listOf(
        ProductItem(
            id = 1,
            nn = "100001",
            name = "Машинка пластиковая",
            article = "ART-001",
            barcode = "4601234567890",
            imageRes = R.drawable.ic_toy_placeholder
        ),
        ProductItem(
            id = 2,
            nn = "100002",
            name = "Кукла 30 см",
            article = "ART-002",
            barcode = "4609876543210",
            imageRes = R.drawable.ic_toy_placeholder
        )
    )

    fun findByAnyCode(query: String): List<ProductItem> {
        val q = query.trim()
        return items.filter {
            it.barcode == q ||
                    it.article.equals(q, true) ||
                    it.nn == q ||
                    it.name.contains(q, true)
        }
    }
}

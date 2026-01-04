package by.nik.warehouseapp.core.data.product

import by.nik.warehouseapp.R
import by.nik.warehouseapp.features.returns.model.ProductItem

object InMemoryProductRepository {

    // TODO: потом заменим на запрос к 1С
    private val items = listOf(
        ProductItem(
            id = 1,
            name = "Конструктор Робот",
            article = "RB-100",
            barcode = "4601234567890",
            nn = "100001",
            imageRes = R.drawable.ic_toy_placeholder
        ),
        ProductItem(
            id = 2,
            name = "Конструктор Робот (вариант)",
            article = "RB-101",
            barcode = "4601234567890", // тот же штрихкод → будет 2 товара
            nn = "100002",
            imageRes = R.drawable.ic_toy_placeholder
        ),
        ProductItem(
            id = 3,
            name = "Мягкий мишка",
            article = "TB-777",
            barcode = "4810000000123",
            nn = "200010",
            imageRes = R.drawable.ic_toy_placeholder
        )
    )

    fun findByAnyCode(query: String): List<ProductItem> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()

        return items.filter { p ->
            p.barcode.equals(q, true) ||
                    p.article.equals(q, true) ||
                    p.nn.equals(q, true)
        }
    }
}
package by.nik.warehouseapp.features.products.data

import by.nik.warehouseapp.features.products.data.dao.ProductDao
import by.nik.warehouseapp.features.products.data.entity.ProductEntity

class ProductRepository(
    private val dao: ProductDao
) {

    fun findByBarcode(barcode: String): ProductEntity? {
        return dao.findByBarcode(barcode)
    }

    fun search(query: String): List<ProductEntity> {
        return dao.search(query)
    }

    fun getById(id: Long): ProductEntity? {
        return dao.getById(id)
    }

    fun isEmpty(): Boolean {
        return dao.count() == 0
    }

    /**
     * Временная инициализация тестового справочника.
     * Позже будет заменено загрузкой из 1С / синхронизацией.
     */
    fun ensureSeeded() {
        if (!isEmpty()) return
        seedTestData()
    }

    fun seedTestData() {
        dao.insertAll(
            listOf(
                ProductEntity(
                    nomenclatureCode = "000000001",
                    name = "Машинка пластиковая",
                    article = "ART-001",
                    barcode = "4601234567890",
                    imageUri = null
                ),
                ProductEntity(
                    nomenclatureCode = "000000002",
                    name = "Кукла 30 см",
                    article = "ART-002",
                    barcode = "4609876543210",
                    imageUri = null
                ),
                ProductEntity(
                    nomenclatureCode = "000000003",
                    name = "Конструктор (без штрихкода)",
                    article = "ART-003",
                    barcode = null,
                    imageUri = null
                )
            )
        )
    }
}

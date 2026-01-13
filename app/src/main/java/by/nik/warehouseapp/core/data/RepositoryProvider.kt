package by.nik.warehouseapp.core.data

import android.content.Context
import by.nik.warehouseapp.core.data.room.DbProvider
import by.nik.warehouseapp.features.products.data.ProductRepository
import by.nik.warehouseapp.features.returns.data.InMemoryReturnRepository
import by.nik.warehouseapp.features.returns.data.ReturnRepository
import by.nik.warehouseapp.features.returns.data.RoomReturnRepository

object RepositoryProvider {
    lateinit var returnRepository: ReturnRepository
        private set

    lateinit var productRepository: ProductRepository
        private set

    fun init(context: Context) {
        val db = DbProvider.get(context)
        returnRepository = RoomReturnRepository(db.returnDao())
        productRepository = ProductRepository(db.productDao())
    }
}
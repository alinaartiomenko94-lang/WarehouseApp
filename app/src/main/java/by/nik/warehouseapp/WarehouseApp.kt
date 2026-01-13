package by.nik.warehouseapp

import android.app.Application
import by.nik.warehouseapp.core.data.RepositoryProvider

class WarehouseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        RepositoryProvider.init(this)

        // Временный засев справочника товаров
        RepositoryProvider.productRepository.ensureSeeded()
    }
}

package by.nik.warehouseapp.core.data

import by.nik.warehouseapp.features.returns.data.InMemoryReturnRepository
import by.nik.warehouseapp.features.returns.data.ReturnRepository

object RepositoryProvider {
    val returnRepository: ReturnRepository by lazy { InMemoryReturnRepository() }
}
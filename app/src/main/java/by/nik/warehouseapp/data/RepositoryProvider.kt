package by.nik.warehouseapp.data

object RepositoryProvider {
    val returnRepository: ReturnRepository by lazy { InMemoryReturnRepository() }
}
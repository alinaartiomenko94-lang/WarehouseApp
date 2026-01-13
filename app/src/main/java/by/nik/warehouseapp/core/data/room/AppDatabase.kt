package by.nik.warehouseapp.core.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import by.nik.warehouseapp.core.data.room.dao.ReturnDao
import by.nik.warehouseapp.core.data.room.entity.ReturnDocumentEntity
import by.nik.warehouseapp.core.data.room.entity.ReturnProductEntity
import by.nik.warehouseapp.features.products.data.dao.ProductDao
import by.nik.warehouseapp.features.products.data.entity.ProductEntity

@Database(
    entities = [
        ReturnDocumentEntity::class,
        ReturnProductEntity::class,
        ProductEntity::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun returnDao(): ReturnDao

    abstract fun productDao() : ProductDao
}
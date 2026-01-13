package by.nik.warehouseapp.features.products.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nomenclatureCode: String, // Код номенклатуры (1С)
    val name: String,
    val article: String,
    val barcode: String?,         // может отсутствовать
    val imageUri: String?         // локальный Uri или путь (пока null)
)


package by.nik.warehouseapp.features.returns.model

data class ProductItem(
    val id: Long,
    val name: String,
    val article: String,
    val barcode: String,
    val nn: String,          // н-н
    val imageRes: Int        // картинка из drawable
)
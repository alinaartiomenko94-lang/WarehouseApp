package by.nik.warehouseapp.features.returns.model

data class ReturnProduct(
    val code: String,
    var quantity: Int,
    var defect: Int
)
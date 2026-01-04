package by.nik.warehouseapp.core.data.legacy

class ReturnItem(
    val productCode: String,
    val productName: String,
    val quantity: Int,
    val defectQuantity: Int,
    val defectReason: String?
)
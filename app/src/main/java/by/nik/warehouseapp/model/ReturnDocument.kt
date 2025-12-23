package by.nik.warehouseapp.model
data class ReturnDocument(
    val id: Long,
    val invoice: String,
    val date: String,
    val contractor: String,
    val status: ReturnStatus,
    val products: MutableList<ReturnProduct>
)

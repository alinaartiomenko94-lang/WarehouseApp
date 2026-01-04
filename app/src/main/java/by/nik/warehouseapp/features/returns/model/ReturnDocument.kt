package by.nik.warehouseapp.features.returns.model

data class ReturnDocument(
    val id: Long,
    val invoice: String,
    val ttnDate: String,
    val contractor: String,
    var status: ReturnStatus = ReturnStatus.CREATED,
    var acceptanceDate: String? = null,
    val products: MutableList<ReturnProduct> = mutableListOf()
)
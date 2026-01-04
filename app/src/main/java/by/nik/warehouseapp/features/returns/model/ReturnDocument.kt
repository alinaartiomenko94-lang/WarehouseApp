package by.nik.warehouseapp.features.returns.model

import by.nik.warehouseapp.features.returns.model.ReturnProduct
import by.nik.warehouseapp.features.returns.model.ReturnStatus

data class ReturnDocument(
    val id: Long,
    val invoice: String,
    val date: String,
    val contractor: String,
    val status: ReturnStatus,
    val products: MutableList<ReturnProduct>
)
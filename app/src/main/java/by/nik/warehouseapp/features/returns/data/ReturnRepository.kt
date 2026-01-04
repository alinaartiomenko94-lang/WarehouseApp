package by.nik.warehouseapp.features.returns.data

import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.model.ReturnProduct

interface ReturnRepository {

    fun getAll(): List<ReturnDocument>
    fun getById(id: Long): ReturnDocument?

    fun create(doc: ReturnDocument)

    fun addProduct(returnId: Long, product: ReturnProduct)
    fun updateProduct(returnId: Long, position: Int, product: ReturnProduct)
    fun deleteProduct(returnId: Long, position: Int)

    fun confirmReturn(id: Long)

}
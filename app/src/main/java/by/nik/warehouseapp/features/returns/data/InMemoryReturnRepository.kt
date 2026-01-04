package by.nik.warehouseapp.features.returns.data

import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.model.ReturnProduct

class InMemoryReturnRepository : ReturnRepository {

    private val returns = mutableListOf<ReturnDocument>()

    override fun getAll(): List<ReturnDocument> = returns

    override fun getById(id: Long): ReturnDocument? =
        returns.firstOrNull {it.id == id}

    override fun create(doc: ReturnDocument) {
        returns.add(0, doc)
    }

    override fun addProduct(returnId: Long, product: ReturnProduct) {
        val doc = getById(returnId) ?: return
        doc.products.add(product)
    }

    override fun updateProduct(returnId: Long, position: Int, product: ReturnProduct) {
        val doc = getById(returnId) ?: return
        if (position < 0 || position >= doc.products.size) return
        doc.products[position] = product
    }

    override fun deleteProduct(returnId: Long, position: Int) {
        val doc = getById(returnId) ?: return
        if (position < 0 || position >= doc.products.size) return
        doc.products.removeAt(position)
    }

}
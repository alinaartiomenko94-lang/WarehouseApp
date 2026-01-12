package by.nik.warehouseapp.features.returns.data

import by.nik.warehouseapp.core.data.room.dao.ReturnDao
import by.nik.warehouseapp.core.data.room.entity.ReturnDocumentEntity
import by.nik.warehouseapp.core.data.room.entity.ReturnProductEntity
import by.nik.warehouseapp.core.utils.DateUtils
import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.model.ReturnProduct
import by.nik.warehouseapp.features.returns.model.ReturnStatus

class RoomReturnRepository(
    private val dao: ReturnDao
) : ReturnRepository {

    override fun getAll(): MutableList<ReturnDocument> {
        val docs = dao.getAllDocs()
        return docs.map { e ->
            val products = dao.getProducts(e.id).map { p ->
                ReturnProduct(code = p.code, quantity = p.quantity, defect = p.defect)
            }.toMutableList()

            ReturnDocument(
                id = e.id,
                invoice = e.invoice,
                ttnDate = e.ttnDate,
                contractor = e.contractor,
                status = ReturnStatus.valueOf(e.status),
                acceptanceDate = e.acceptanceDate,
                products = products
            )
        }.toMutableList()
    }

    override fun getById(id: Long): ReturnDocument? {
        val e = dao.getDocById(id) ?: return null
        val products = dao.getProducts(e.id).map { p->
            ReturnProduct(code = p.code, quantity =  p.quantity, defect = p.defect)
        }.toMutableList()

        return ReturnDocument(
            id = e.id,
            invoice = e.invoice,
            ttnDate = e.ttnDate,
            contractor = e.contractor,
            status = ReturnStatus.valueOf(e.status),
            acceptanceDate = e.acceptanceDate,
            products = products
        )
    }

    override fun create(doc: ReturnDocument) {
        dao.upsertDoc(
            ReturnDocumentEntity(
                id = doc.id,
                invoice = doc.invoice,
                ttnDate = doc.ttnDate,
                contractor = doc.contractor,
                status = doc.status.name,
                acceptanceDate = doc.acceptanceDate
            )
        )
        // товары обычно пустые при создании
    }

    override fun addProduct(returnId: Long, product: ReturnProduct) {
        dao.insertProduct(
            ReturnProductEntity(
                returnId = returnId,
                code = product.code,
                quantity = product.quantity,
                defect = product.defect
            )
        )
    }

    override fun updateProduct(returnId: Long, position: Int, product: ReturnProduct) {
        val existing = dao.getProductByOffset(returnId, position) ?: return
        dao.updateProduct(
            existing.copy(
                code = product.code,
                quantity = product.quantity,
                defect = product.defect
            )
        )
    }

    override fun deleteProduct(returnId: Long, position: Int) {
        val existing = dao.getProductByOffset(returnId, position) ?: return
        dao.deleteProduct(existing)
    }

    override fun confirmReturn(id: Long) {
        val doc = dao.getDocById(id) ?: return
        val products = dao.getProducts(id)
        if (products.isEmpty()) return

        val updated = doc.copy(
            status = ReturnStatus.ACCEPTED.name,
            acceptanceDate = doc.acceptanceDate ?: DateUtils.today_ddMMyyyy()
        )
        dao.updateDoc(updated)
    }
}
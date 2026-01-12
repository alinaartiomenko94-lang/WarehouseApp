package by.nik.warehouseapp.core.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import by.nik.warehouseapp.core.data.room.entity.ReturnDocumentEntity
import by.nik.warehouseapp.core.data.room.entity.ReturnProductEntity
import by.nik.warehouseapp.features.returns.model.ReturnProduct

@Dao
interface ReturnDao
{

    //--- documents ---
    @Query("SELECT * FROM return_documents ORDER BY id DESC")
    fun getAllDocs(): MutableList<ReturnDocumentEntity>

    @Query("SELECT * FROM return_documents WHERE id = :id LIMIT 1")
    fun getDocById(id: Long): ReturnDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertDoc(doc: ReturnDocumentEntity)

    @Update
    fun updateDoc(doc: ReturnDocumentEntity)

    @Query("DELETE FROM return_documents WHERE id = :id")
    fun deleteDoc(id: Long)

    //--- products ---
    @Query("SELECT * FROM return_products WHERE returnId = :returnId ORDER BY rowId ASC")
    fun getProducts(returnId: Long): MutableList<ReturnProductEntity>

    @Insert
    fun insertProduct(product: ReturnProductEntity)

    @Update
    fun updateProduct(product: ReturnProductEntity)

    @Delete
    fun deleteProduct(product: ReturnProductEntity)

    @Query("DELETE FROM return_products WHERE returnId = :returnId")
    fun deleteAllProducts(returnId: Long)

    @Query("SELECT * FROM return_products WHERE returnId = :returnId ORDER BY rowId ASC LIMIT 1 OFFSET :offset")
    fun getProductByOffset(returnId: Long, offset: Int): ReturnProductEntity?

}
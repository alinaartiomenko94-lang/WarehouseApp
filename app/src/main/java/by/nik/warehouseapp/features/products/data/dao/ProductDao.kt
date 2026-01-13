package by.nik.warehouseapp.features.products.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.nik.warehouseapp.features.products.data.entity.ProductEntity

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    fun findByBarcode(barcode: String): ProductEntity?

    @Query("""
        SELECT * FROM products 
        WHERE article LIKE '%' || :query || '%'
           OR name LIKE '%' || :query || '%'
        ORDER BY name
    """)
    fun search(query: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getById(id: Long): ProductEntity?

}
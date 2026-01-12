package by.nik.warehouseapp.core.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "return_products",
    foreignKeys = [
        ForeignKey(
            entity = ReturnDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["returnId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("returnId")]
)
data class ReturnProductEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val returnId: Long,
    val code: String,
    val quantity: Int,
    val defect: Int
)

package by.nik.warehouseapp.core.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "return_documents")
data class ReturnDocumentEntity(
    @PrimaryKey val id: Long,
    val invoice: String,
    val ttnDate: String,
    val contractor: String,
    val status: String,
    val acceptanceDate: String?
)

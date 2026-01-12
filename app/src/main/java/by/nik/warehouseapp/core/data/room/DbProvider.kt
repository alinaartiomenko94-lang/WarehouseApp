package by.nik.warehouseapp.core.data.room

import android.content.Context
import androidx.room.Room

object DbProvider {
    @Volatile private var db: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "warehouse.db"
            )

            // ✅ чтобы НЕ переписывать всё на корутины прямо сейчас:
                .allowMainThreadQueries()
                .build()
                .also { db = it }
        }
    }
}
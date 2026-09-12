package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.ProductListing

@Database(entities = [ProductListing::class], version = 1, exportSchema = false)
abstract class ShreniDatabase : RoomDatabase() {
    abstract fun productListingDao(): ProductListingDao

    companion object {
        @Volatile
        private var INSTANCE: ShreniDatabase? = null

        fun getDatabase(context: Context): ShreniDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShreniDatabase::class.java,
                    "shrenisetu_artisan.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

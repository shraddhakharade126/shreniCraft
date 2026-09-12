package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.ProductScanRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductScanDao {
    @Query("SELECT * FROM product_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ProductScanRecord>>

    @Query("SELECT * FROM product_scans WHERE id = :id")
    suspend fun getScanById(id: String): ProductScanRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ProductScanRecord)

    @Update
    suspend fun updateScan(scan: ProductScanRecord)

    @Query("DELETE FROM product_scans WHERE id = :id")
    suspend fun deleteScan(id: String)

    @Query("SELECT COUNT(*) FROM product_scans")
    fun getScansCount(): Flow<Int>
}

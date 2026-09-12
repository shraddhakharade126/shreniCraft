package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.ProductListing
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductListingDao {
    @Query("SELECT * FROM product_listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<ProductListing>>

    @Query("SELECT * FROM product_listings WHERE id = :id")
    suspend fun getListingById(id: String): ProductListing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ProductListing)

    @Query("DELETE FROM product_listings WHERE id = :id")
    suspend fun deleteListing(id: String)

    @Query("SELECT COUNT(*) FROM product_listings")
    fun getListingsCount(): Flow<Int>
}

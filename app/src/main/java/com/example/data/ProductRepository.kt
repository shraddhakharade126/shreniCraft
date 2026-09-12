package com.example.data

import com.example.model.ProductListing
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductListingDao) {
    val allListings: Flow<List<ProductListing>> = dao.getAllListings()
    val listingsCount: Flow<Int> = dao.getListingsCount()

    suspend fun saveListing(listing: ProductListing) {
        dao.insertListing(listing)
    }

    suspend fun getListingById(id: String): ProductListing? {
        return dao.getListingById(id)
    }

    suspend fun deleteListing(id: String) {
        dao.deleteListing(id)
    }
}

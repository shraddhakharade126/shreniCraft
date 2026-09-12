package com.example.data

import com.example.model.ProductListing
import com.example.model.ProductScanRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ProductRepository(
    private val dao: ProductListingDao,
    private val scanDao: ProductScanDao? = null
) {
    val allListings: Flow<List<ProductListing>> = dao.getAllListings()
    val listingsCount: Flow<Int> = dao.getListingsCount()
    val allScans: Flow<List<ProductScanRecord>> = scanDao?.getAllScans() ?: flowOf(emptyList())
    val scansCount: Flow<Int> = scanDao?.getScansCount() ?: flowOf(0)

    suspend fun saveListing(listing: ProductListing) {
        dao.insertListing(listing)
    }

    suspend fun getListingById(id: String): ProductListing? {
        return dao.getListingById(id)
    }

    suspend fun deleteListing(id: String) {
        dao.deleteListing(id)
    }

    suspend fun saveScan(scan: ProductScanRecord) {
        scanDao?.insertScan(scan)
    }

    suspend fun getScanById(id: String): ProductScanRecord? {
        return scanDao?.getScanById(id)
    }

    suspend fun deleteScan(id: String) {
        scanDao?.deleteScan(id)
    }
}


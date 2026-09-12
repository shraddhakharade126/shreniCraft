package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "product_listings")
data class ProductListing(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val productName: String,
    val category: String,
    val craftType: String,
    val material: String,
    val colors: String,
    val description: String,
    val price: Double,
    val suggestedPriceMin: Double,
    val suggestedPriceMax: Double,
    val imageUri: String,
    val originalImageUri: String,
    val artisanName: String = "Savitri Devi",
    val region: String = "Jaipur, Rajasthan",
    val tags: String,
    val backgroundRemoved: Boolean = true,
    val enhanced: Boolean = true,
    val integrityProtected: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

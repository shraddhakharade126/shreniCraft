package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room entity representing a saved previous scan in Shreni Scan.
 * Preserves the historical authenticity verification record, the original
 * untouched capture, and the final processed product image.
 */
@Entity(tableName = "product_scans")
data class ProductScanRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val originalImageUri: String,
    val processedImageUri: String?,
    val productTitle: String = "Handcrafted Artisan Item",
    val craftType: String = "Traditional Craft",
    val category: String = "Handicrafts",
    val material: String = "Natural Materials",
    val description: String = "",
    val tags: String = "handmade, artisan",
    val suggestedPriceMin: Double = 500.0,
    val suggestedPriceMax: Double = 800.0,
    val finalPrice: Double = 650.0,
    val confidenceScore: Float = 0.98f,
    val integrityPassed: Boolean = true,
    val integrityVerdict: String = "Authentic handmade craft details verified. Foreground pixels preserved.",
    val backgroundRemoved: Boolean = true,
    val enhanced: Boolean = true,
    val publishedListingId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

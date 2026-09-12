package com.example.model

data class DemoCraft(
    val id: String,
    val title: String,
    val category: String,
    val craftType: String,
    val material: String,
    val colors: List<String>,
    val description: String,
    val tags: List<String>,
    val minPrice: Double,
    val maxPrice: Double,
    val defaultPrice: Double,
    val badgeLabel: String,
    val primaryColorHex: Long
) {
    companion object {
        val SAMPLES = listOf(
            DemoCraft(
                id = "craft_terracotta_pot",
                title = "Handmade Terracotta Pot with Painted Motifs",
                category = "Pottery",
                craftType = "Terracotta Craft",
                material = "Natural Clay & Mineral Pigments",
                colors = listOf("Terracotta Brown", "Ivory White", "Ochre Red"),
                description = "Authentic hand-molded earthen terracotta pot crafted on traditional potter's wheel, adorned with cultural floral motifs and heat-resistant finish.",
                tags = listOf("terracotta", "pottery", "handmade", "Indian craft", "home decor"),
                minPrice = 450.0,
                maxPrice = 900.0,
                defaultPrice = 650.0,
                badgeLabel = "GI Tagged Craft",
                primaryColorHex = 0xFFC85A32
            ),
            DemoCraft(
                id = "craft_brass_diya",
                title = "Handcrafted Antique Brass Peacock Diya",
                category = "Metal Craft",
                craftType = "Dhokra Brass Casting",
                material = "Pure Bell Metal & Brass Alloy",
                colors = listOf("Antique Golden", "Brass Bronze"),
                description = "Intricately cast brass oil lamp sculpted in majestic peacock form using ancestral lost-wax casting technique by tribal master artisans.",
                tags = listOf("brass", "dhokra", "diya", "handcrafted", "temple art", "puja decor"),
                minPrice = 750.0,
                maxPrice = 1500.0,
                defaultPrice = 1100.0,
                badgeLabel = "Tribal Craft",
                primaryColorHex = 0xFFC59B27
            ),
            DemoCraft(
                id = "craft_madhubani_art",
                title = "Traditional Madhubani Peacock Wall Painting",
                category = "Painting",
                craftType = "Mithila / Madhubani Art",
                material = "Handmade Paper & Organic Plant Dyes",
                colors = listOf("Indigo Blue", "Turmeric Yellow", "Crimson Red"),
                description = "Hand-painted folk artwork portraying the tree of life and sacred peacocks created with bamboo nibs and natural twig brushes on treated paper.",
                tags = listOf("madhubani", "mithila", "folk painting", "natural dyes", "wall art"),
                minPrice = 1200.0,
                maxPrice = 2800.0,
                defaultPrice = 1850.0,
                badgeLabel = "Master Artisan",
                primaryColorHex = 0xFF1976D2
            ),
            DemoCraft(
                id = "craft_kutch_bag",
                title = "Embroidered Gujarati Kutch Mirrorwork Clutch",
                category = "Textile",
                craftType = "Kutch Hand Embroidery",
                material = "Organic Cotton, Silk Thread & Real Glass Mirrors",
                colors = listOf("Vibrant Maroon", "Saffron Orange", "Teal Green"),
                description = "Hand-stitched bohemian envelope clutch embellished with traditional Rabari geometric mirrorwork and cotton tassel charms.",
                tags = listOf("kutch embroidery", "mirrorwork", "handloom", "ethnic clutch", "gujarat craft"),
                minPrice = 650.0,
                maxPrice = 1350.0,
                defaultPrice = 850.0,
                badgeLabel = "Heritage Textile",
                primaryColorHex = 0xFF880E4F
            )
        )
    }
}

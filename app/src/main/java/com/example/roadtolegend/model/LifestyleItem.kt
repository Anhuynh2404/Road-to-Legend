package com.example.roadtolegend.model

enum class LifestyleCategory(val titleVi: String) {
    TRAINING_FACILITY("Thiết bị & HLV cá nhân"),
    EQUIPMENT("Trang thiết bị thi đấu"),
    LIVING_COMFORT("Nơi ở & Tiện nghi"),
    LUXURY("Đẳng cấp & Xa xỉ")
}

data class LifestyleItem(
    val id: String,
    val nameVi: String,
    val category: LifestyleCategory,
    val price: Long,
    val weeklyMaintenance: Long,
    val descriptionVi: String,
    val trainingMultiplierBonus: Double = 0.0,
    val fatigueReductionBonus: Double = 0.0,
    val moraleBonus: Int = 0,
    val reputationBonus: Int = 0,
    var isPurchased: Boolean = false
)

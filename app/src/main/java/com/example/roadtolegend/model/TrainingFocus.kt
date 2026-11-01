package com.example.roadtolegend.model

import com.example.roadtolegend.core.config.TrainingConfig
import com.example.roadtolegend.engine.BalanceConfig

enum class TrainingFocus(
    val titleVi: String,
    val descriptionVi: String,
    val targetStatName: String,
    val baseCost: Long,
    val baseEnergyCost: Int,
    val baseFatigue: Double
) {
    PACE(
        titleVi = "Bứt tốc & Khởi động nhanh",
        descriptionVi = "Tập luyện sức rướn, bứt tốc đoạn ngắn và tăng tốc",
        targetStatName = "PAC",
        baseCost = 60,
        baseEnergyCost = 18,
        baseFatigue = 6.0
    ),
    SHOOTING(
        titleVi = "Dứt điểm & Sút phạt",
        descriptionVi = "Rèn luyện sút một chạm, cứa lòng và lực sút",
        targetStatName = "SHO",
        baseCost = 80,
        baseEnergyCost = 20,
        baseFatigue = 7.0
    ),
    PASSING(
        titleVi = "Chuyền ngắn & Phân phối bóng",
        descriptionVi = "Tầm nhìn chiến thuật, chuyền dài vượt tuyến và chọc khe",
        targetStatName = "PAS",
        baseCost = 70,
        baseEnergyCost = 16,
        baseFatigue = 5.5
    ),
    DRIBBLING(
        titleVi = "Kỹ thuật rê bóng & Giữ bóng",
        descriptionVi = "Kiểm soát bóng trong phạm vi hẹp, đảo chân qua người",
        targetStatName = "DRI",
        baseCost = 75,
        baseEnergyCost = 18,
        baseFatigue = 6.5
    ),
    STAMINA(
        titleVi = "Sức bền & Nền tảng thể lực",
        descriptionVi = "Chạy bền, nâng cao ngưỡng mệt mỏi và sức chịu đựng",
        targetStatName = "STA",
        baseCost = 50,
        baseEnergyCost = 24,
        baseFatigue = 9.0
    ),
    DEFENDING(
        titleVi = "Phòng ngự & Tắc bóng",
        descriptionVi = "Kèm người, cắt bóng, phán đoán đường chuyền đối phương",
        targetStatName = "DEF",
        baseCost = 60,
        baseEnergyCost = 19,
        baseFatigue = 7.0
    ),
    REST_AND_RECOVERY(
        titleVi = "Hồi phục & Liệu trình Massage",
        descriptionVi = "Massage vật lý trị liệu, giảm mệt mỏi và phòng tránh chấn thương",
        targetStatName = "REST",
        baseCost = 35,
        baseEnergyCost = -35,
        baseFatigue = -25.0
    );

    fun getCost(intensity: TrainingIntensity): Long {
        return (baseCost * intensity.costMultiplier).toLong()
    }

    fun getEnergyCost(intensity: TrainingIntensity): Int {
        return if (this == REST_AND_RECOVERY) {
            (baseEnergyCost * intensity.energyCostMultiplier).toInt()
        } else {
            (baseEnergyCost * intensity.energyCostMultiplier).toInt()
        }
    }

    fun getEstimatedGainRange(
        intensity: TrainingIntensity,
        age: Int,
        currentStat: Double,
        potential: Double,
        facilityLevel: Int
    ): Pair<Double, Double> {
        if (this == REST_AND_RECOVERY) return 0.0 to 0.0

        val ageFactor = BalanceConfig.getAgeGrowthMultiplier(age)
        if (ageFactor <= 0) {
            val minDrop = ageFactor * 0.4
            val maxDrop = ageFactor * 0.1
            return minDrop to maxDrop
        }

        val potGap = BalanceConfig.getPotentialGapFactor(currentStat, potential)
        val facilityBonus = 1.0 + (facilityLevel * 0.05)
        val baseMin = 0.35 * intensity.growthMultiplier * ageFactor * potGap * facilityBonus
        val baseMax = 0.70 * intensity.growthMultiplier * ageFactor * potGap * facilityBonus

        return baseMin to baseMax
    }
}

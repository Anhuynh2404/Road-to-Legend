package com.example.roadtolegend.model

enum class TrainingIntensity(
    val titleVi: String,
    val costMultiplier: Double,
    val growthMultiplier: Double,
    val fatigueMultiplier: Double,
    val energyCostMultiplier: Double,
    val injuryRiskLabel: String,
    val injuryRiskPercent: Double
) {
    LIGHT(
        titleVi = "Nhẹ",
        costMultiplier = 0.5,
        growthMultiplier = 0.65,
        fatigueMultiplier = 0.5,
        energyCostMultiplier = 0.6,
        injuryRiskLabel = "Rất thấp",
        injuryRiskPercent = 0.5
    ),
    MEDIUM(
        titleVi = "Vừa",
        costMultiplier = 1.0,
        growthMultiplier = 1.0,
        fatigueMultiplier = 1.0,
        energyCostMultiplier = 1.0,
        injuryRiskLabel = "Trung bình",
        injuryRiskPercent = 2.0
    ),
    HEAVY(
        titleVi = "Nặng (Cường độ cao)",
        costMultiplier = 1.8,
        growthMultiplier = 1.45,
        fatigueMultiplier = 1.8,
        energyCostMultiplier = 1.5,
        injuryRiskLabel = "Cao",
        injuryRiskPercent = 6.0
    )
}

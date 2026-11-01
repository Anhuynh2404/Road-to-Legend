package com.example.roadtolegend.model

import java.util.UUID

data class CareerStats(
    var matchesPlayed: Int = 0,
    var starts: Int = 0,
    var subs: Int = 0,
    var goals: Int = 0,
    var assists: Int = 0,
    var tackles: Int = 0,
    var cleanSheets: Int = 0,
    var totalRatingPoints: Double = 0.0,
    var manOfTheMatchCount: Int = 0,
    var totwCount: Int = 0,
    var yellowCards: Int = 0,
    var redCards: Int = 0,
    var trophies: MutableList<String> = mutableListOf()
) {
    val averageRating: Double
        get() = if (matchesPlayed > 0) String.format("%.2f", totalRatingPoints / matchesPlayed).toDouble() else 0.0
}

data class Player(
    val id: String,
    var name: String,
    var age: Int = 17,
    var birthYear: Int = 2007,
    var nationality: String = "Việt Nam",
    var heightCm: Int = 178,
    var preferredFoot: Foot = Foot.RIGHT,
    var primaryPosition: Position = Position.ST,

    // 6 chỉ số cốt lõi (1 - 99)
    var pac: Double = 50.0,
    var sho: Double = 55.0,
    var pas: Double = 45.0,
    var dri: Double = 52.0,
    var sta: Double = 50.0,
    var def: Double = 35.0,

    // Tiềm năng ẩn (Ẩn số đối với người chơi - xem qua scoutReport)
    val hiddenPotential: Double = 84.0,

    // Trạng thái thể lực & tinh thần
    var energy: Int = 100,        // 0 - 100 (tuần này)
    var fatigue: Double = 0.0,    // 0 - 100 (tích lũy)
    var morale: Int = 85,         // 0 - 100
    var form: Int = 75,           // 0 - 100

    // Danh tiếng
    var domesticReputation: Int = 15,
    var globalReputation: Int = 2,

    // Quan hệ nội bộ
    var managerTrust: Int = 65,   // Quyết định suất đá chính
    var teamChemistry: Int = 50,  // Độ ăn ý với đồng đội

    // Tài chính cá nhân
    var balance: Long = 1_200,

    // Chấn thương
    var injuryWeeksRemaining: Int = 0,
    var injuryName: String? = null,

    // Thống kê sự nghiệp
    var careerStats: CareerStats = CareerStats(),

    // Lịch sử từng mùa giải & CLB (Section F)
    val seasonRecords: MutableList<SeasonRecord> = mutableListOf(),

    // Cột mốc sự nghiệp (Milestones)
    val milestones: MutableList<CareerMilestone> = mutableListOf(),

    var peakMarketValue: Long = 0L,
    var customAvatarPath: String? = null
) {
    val ovr: Int
        get() = primaryPosition.calculateOvr(pac, sho, pas, dri, sta, def)

    val isInjured: Boolean
        get() = injuryWeeksRemaining > 0

    // Báo cáo tuyển trạch (Scout Report - Football Manager style)
    val scoutReport: String
        get() = when {
            hiddenPotential >= 92 -> "Tố chất Siêu sao Đẳng cấp Thế giới (World-Class Phenom)"
            hiddenPotential >= 87 -> "Tiềm năng xuất ngoại và trở thành Trụ cột ĐTQG"
            hiddenPotential >= 82 -> "Tiềm năng ngôi sao hàng đầu giải quốc nội"
            hiddenPotential >= 76 -> "Cầu thủ thi đấu tròn vai ở các giải chuyên nghiệp"
            else -> "Tiềm năng hạn chế, cần nỗ lực phi thường để bứt phá"
        }

    val marketValue: Long
        get() {
            val baseOvrFactor = Math.pow((ovr / 50.0), 3.8) * 50_000
            val ageMultiplier = when {
                age <= 20 -> 1.8
                age <= 23 -> 1.4
                age <= 28 -> 1.0
                age <= 31 -> 0.7
                else -> 0.4
            }
            val repMultiplier = 1.0 + (globalReputation * 0.02) + (domesticReputation * 0.008)
            val calculated = (baseOvrFactor * ageMultiplier * repMultiplier).toLong().coerceAtLeast(10_000)
            if (calculated > peakMarketValue) peakMarketValue = calculated
            return calculated
        }

    fun addMilestoneIfNew(title: String, description: String, dateText: String, icon: String = "⭐") {
        if (milestones.none { it.titleVi == title }) {
            milestones.add(
                CareerMilestone(
                    id = UUID.randomUUID().toString(),
                    titleVi = title,
                    descriptionVi = description,
                    dateAchievedText = dateText,
                    iconEmoji = icon
                )
            )
        }
    }
}

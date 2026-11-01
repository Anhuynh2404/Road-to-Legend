package com.example.roadtolegend.model

/**
 * Mô hình hợp đồng chuyên nghiệp đầy đủ chi tiết theo tiêu chuẩn bóng đá quốc tế.
 */
data class Contract(
    val clubId: String,
    val clubName: String,
    var weeklyWage: Long,                      // Lương tuần (€ / USD)
    var signingBonus: Long = 0L,               // Tiền thưởng khi đặt bút ký hợp đồng (lót tay)
    var bonusPerAppearance: Long = 50L,        // Thưởng mỗi trận ra sân (đá chính hoặc từ ghế dự bị)
    var bonusPerGoal: Long = 100L,             // Thưởng mỗi bàn thắng ghi được
    var bonusPerAssist: Long = 50L,            // Thưởng mỗi pha kiến tạo thành bàn
    var bonusPerCleanSheet: Long = 80L,        // Thưởng giữ sạch lưới (với hậu vệ / thủ môn)
    var bonusTrophy: Long = 5_000L,            // Thưởng vô địch giải đấu
    var contractYearsRemaining: Int = 2,       // Số năm hợp đồng còn lại
    var totalWeeksRemaining: Int = 52,         // Tổng số tuần hợp đồng còn hiệu lực (26 tuần/mùa)
    var releaseClause: Long? = 500_000L,       // Điều khoản giải phóng hợp đồng (null = không có)
    var squadRole: SquadRole = SquadRole.PROSPECT, // Vai trò cam kết trong đội hình
    var annualWageIncreasePercent: Int = 0,    // Tự động tăng lương mỗi mùa (%)
    var wageIncreaseAfterMatches: Int = 0,     // Tự động tăng 15% lương sau số trận nhất định
    var clubSatisfaction: Int = 75,            // Mức độ hài lòng của CLB đối với cầu thủ (0 - 100)
    var isPreContract: Boolean = false,        // Đã ký thỏa thuận trước với CLB mới chưa
    var preContractClubId: String? = null,     // CLB mới đã ký trước (chuyển vào đầu mùa sau)
    var startSeason: Int = 1,                  // Mùa bắt đầu hợp đồng
    var endSeason: Int = 3                     // Mùa kết thúc hợp đồng
) {
    val isExpired: Boolean
        get() = totalWeeksRemaining <= 0

    val isExpiringSoon: Boolean
        get() = totalWeeksRemaining in 1..13 // Còn dưới 6 tháng (13 tuần) -> được phép ký pre-contract

    val isFreeAgent: Boolean
        get() = clubId.isEmpty() || clubId == "free_agent"
}

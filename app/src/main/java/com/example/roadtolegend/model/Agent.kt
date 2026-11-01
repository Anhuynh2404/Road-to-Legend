package com.example.roadtolegend.model

enum class AgentTier(
    val titleVi: String,
    val descriptionVi: String,
    val negotiationBonus: Int,         // Điểm cộng đàm phán ngầm (1 - 25)
    val networkTierMax: Int,           // Hạng giải đấu có thể tiếp cận (1 = V-League 1/Premier League)
    val commissionPercent: Int,        // Tỉ lệ hoa hồng trích từ lương và phí ký (%)
    val weeklyRetainer: Long,          // Chi phí duy trì mỗi tuần (€)
    val minOvrRequired: Int,           // Yêu cầu OVR tối thiểu của cầu thủ
    val minReputationRequired: Int     // Yêu cầu Danh tiếng tối thiểu
) {
    NONE(
        titleVi = "Tự đại diện (Gia đình)",
        descriptionVi = "Bạn tự mình đàm phán hợp đồng hoặc nhờ người thân hỗ trợ. Không tốn phí nhưng mạng lưới hạn chế.",
        negotiationBonus = 0,
        networkTierMax = 3,
        commissionPercent = 0,
        weeklyRetainer = 0L,
        minOvrRequired = 0,
        minReputationRequired = 0
    ),
    ROOKIE(
        titleVi = "Môi giới trẻ triển vọng",
        descriptionVi = "Người đại diện khởi nghiệp nhiệt huyết, bắt đầu xây dựng mối quan hệ với các CLB giải hạng dưới.",
        negotiationBonus = 6,
        networkTierMax = 2,
        commissionPercent = 5,
        weeklyRetainer = 30L,
        minOvrRequired = 52,
        minReputationRequired = 10
    ),
    EXPERIENCED(
        titleVi = "Đại diện chuyên nghiệp quốc nội",
        descriptionVi = "Có uy tín và mối liên hệ sâu rộng với ban lãnh đạo các CLB chuyên nghiệp tại giải quốc gia.",
        negotiationBonus = 12,
        networkTierMax = 1,
        commissionPercent = 8,
        weeklyRetainer = 100L,
        minOvrRequired = 60,
        minReputationRequired = 25
    ),
    ELITE(
        titleVi = "Tập đoàn quản lý thể thao hàng đầu",
        descriptionVi = "Sở hữu mạng lưới tuyển trạch viên toàn cầu, liên hệ trực tiếp với các CLB châu Âu và giải đấu lớn.",
        negotiationBonus = 18,
        networkTierMax = 1,
        commissionPercent = 10,
        weeklyRetainer = 250L,
        minOvrRequired = 68,
        minReputationRequired = 45
    ),
    SUPER_AGENT(
        titleVi = "Siêu cò bóng đá quốc tế",
        descriptionVi = "Một trong những người quyền lực nhất làng túc cầu. Đảm bảo mức đãi ngộ kỷ lục và các bản hợp đồng bom tấn.",
        negotiationBonus = 25,
        networkTierMax = 1,
        commissionPercent = 12,
        weeklyRetainer = 600L,
        minOvrRequired = 76,
        minReputationRequired = 65
    )
}

data class Agent(
    val id: String,
    val name: String,
    val tier: AgentTier,
    var totalOffersBrought: Int = 0,
    var extraWagesSecured: Long = 0L,
    var playerTrust: Int = 80 // Mức độ gắn bó giữa Agent và Cầu thủ (0 - 100)
)

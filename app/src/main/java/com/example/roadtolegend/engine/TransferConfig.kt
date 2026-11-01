package com.example.roadtolegend.engine

import com.example.roadtolegend.model.Agent
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.SquadRole

/**
 * Cấu hình tập trung toàn bộ công thức, tham số và quy tắc nghiệp vụ cho:
 * - Hợp đồng & Giá trị thị trường (Market Value)
 * - Đàm phán hợp đồng & Độ kiên nhẫn ngầm
 * - Người đại diện (Agent) & Hoa hồng
 * - Cửa sổ chuyển nhượng & Ngày chốt sổ (Deadline Day)
 * - Mượn cầu thủ (Loan)
 * - Ngân sách và luật công bằng tài chính
 */
object TransferConfig {

    // =========================================================================
    // 1. CỬA SỔ CHUYỂN NHƯỢNG & LỊCH TRÌNH MÙA GIẢI (26 tuần/mùa)
    // =========================================================================
    const val SUMMER_WINDOW_START_WEEK = 1
    const val SUMMER_WINDOW_END_WEEK = 4
    const val SUMMER_DEADLINE_DAY_WEEK = 4

    const val WINTER_WINDOW_START_WEEK = 12
    const val WINTER_WINDOW_END_WEEK = 15
    const val WINTER_DEADLINE_DAY_WEEK = 15

    const val LATE_SEASON_WINDOW_START_WEEK = 24
    const val LATE_SEASON_WINDOW_END_WEEK = 26

    fun isTransferWindowOpen(week: Int): Boolean {
        return (week in SUMMER_WINDOW_START_WEEK..SUMMER_WINDOW_END_WEEK) ||
                (week in WINTER_WINDOW_START_WEEK..WINTER_WINDOW_END_WEEK) ||
                (week in LATE_SEASON_WINDOW_START_WEEK..LATE_SEASON_WINDOW_END_WEEK)
    }

    fun isDeadlineDay(week: Int): Boolean {
        return week == SUMMER_DEADLINE_DAY_WEEK || week == WINTER_DEADLINE_DAY_WEEK
    }

    // Luật ký trước hợp đồng (Pre-contract): còn 13 tuần trở xuống (~6 tháng)
    const val PRE_CONTRACT_MAX_WEEKS_REMAINING = 13

    // Cờ bật/tắt quyền ký pre-contract theo giải đấu
    fun isPreContractAllowed(leagueId: String): Boolean {
        // Cho phép tại V-League 1, V-League 2, Championship, Premier League
        return true
    }

    // Thuế thu nhập cá nhân đơn giản (tùy chọn theo config)
    const val IS_TAX_ENABLED = true
    const val TAX_RATE_PERCENT = 10 // 10% thuế

    fun calculateNetIncome(gross: Long): Long {
        if (!IS_TAX_ENABLED) return gross
        val tax = (gross * (TAX_RATE_PERCENT / 100.0)).toLong()
        return gross - tax
    }

    // =========================================================================
    // 2. CÔNG THỨC TÍNH GIÁ TRỊ THỊ TRƯỜNG (MARKET VALUE)
    // =========================================================================
    fun calculateMarketValue(
        player: Player,
        contractWeeksRemaining: Int
    ): Long {
        // 1. Hệ số sức mạnh cốt lõi OVR theo hàm mũ chuẩn Transfermarkt
        val baseOvrFactor = Math.pow((player.ovr / 50.0), 3.85) * 45_000.0

        // 2. Hệ số tuổi tác (cầu thủ trẻ có thặng dư chuyển nhượng cao)
        val ageMultiplier = when {
            player.age <= 18 -> 1.95
            player.age <= 20 -> 1.70
            player.age <= 22 -> 1.45
            player.age <= 25 -> 1.20
            player.age <= 28 -> 1.00 // Đỉnh cao
            player.age <= 30 -> 0.75
            player.age <= 33 -> 0.45
            else -> 0.20
        }

        // 3. Hệ số tiềm năng phát triển (khoảng cách giữa OVR và Tiềm năng ẩn)
        val potentialDiff = (player.hiddenPotential - player.ovr).coerceAtLeast(0.0)
        val potentialFactor = 1.0 + (potentialDiff * 0.022)

        // 4. Hệ số phong độ thi đấu gần đây
        val formFactor = 1.0 + ((player.form - 70) * 0.005)

        // 5. Hệ số thời hạn hợp đồng (hợp đồng sắp hết làm giảm giá chuyển nhượng)
        val contractFactor = when {
            contractWeeksRemaining <= 0 -> 0.25 // Cầu thủ tự do
            contractWeeksRemaining <= 13 -> 0.60 // Còn < 6 tháng
            contractWeeksRemaining <= 26 -> 0.85 // Còn < 1 năm
            contractWeeksRemaining <= 52 -> 1.00 // Còn 1 - 2 năm
            else -> 1.25 // Hợp đồng dài hạn
        }

        // 6. Hệ số danh tiếng (Toàn cầu & Quốc nội)
        val repMultiplier = 1.0 + (player.globalReputation * 0.025) + (player.domesticReputation * 0.008)

        val finalValue = (baseOvrFactor * ageMultiplier * potentialFactor * formFactor * contractFactor * repMultiplier).toLong()
        val normalized = finalValue.coerceAtLeast(15_000L)

        if (normalized > player.peakMarketValue) {
            player.peakMarketValue = normalized
        }
        return normalized
    }

    // =========================================================================
    // 3. CÔNG THỨC TÍNH MỨC LƯƠNG GỢI Ý & NGÂN SÁCH CLB
    // =========================================================================
    fun calculateRecommendedWage(
        playerOvr: Int,
        playerReputation: Int,
        clubTier: Int,
        clubBudget: Long,
        clubReputation: Int = 50
    ): Long {
        val baseTierWage = when (clubTier) {
            1 -> 3_000L + (clubReputation * 30L)
            2 -> 800L + (clubReputation * 15L)
            else -> 400L
        }

        val ovrMultiplier = Math.pow((playerOvr / 55.0), 3.3)
        val repBonus = 1.0 + (playerReputation * 0.007)
        val rawWage = (baseTierWage * ovrMultiplier * repBonus).toLong()

        // Giới hạn trong khả năng chi trả của ngân sách CLB
        val maxAffordableWage = (clubBudget / 50).coerceAtLeast(300L)
        return rawWage.coerceIn(250L, maxAffordableWage)
    }

    // =========================================================================
    // 4. ĐỘ KIÊN NHẪN ĐÀM PHÁN (HIDDEN PATIENCE) & PHẢN HỒI BẰNG CHỮ
    // =========================================================================
    const val INITIAL_NEGOTIATION_PATIENCE = 100
    const val MIN_PATIENCE_THRESHOLD = 15

    fun calculatePatiencePenalty(
        demandedWage: Long,
        clubMaxWage: Long,
        demandedSigningBonus: Long,
        clubMaxBonus: Long,
        agentBonus: Int
    ): Int {
        val wageRatio = demandedWage.toDouble() / clubMaxWage.toDouble()
        val bonusRatio = demandedSigningBonus.toDouble() / clubMaxBonus.coerceAtLeast(1L).toDouble()

        var penalty = when {
            wageRatio > 1.50 -> 40
            wageRatio > 1.30 -> 26
            wageRatio > 1.15 -> 16
            wageRatio > 1.05 -> 8
            else -> 3
        }

        if (bonusRatio > 1.6) penalty += 15
        else if (bonusRatio > 1.2) penalty += 8

        // Kỹ năng của Agent giúp giảm mức mất kiên nhẫn
        penalty = (penalty - (agentBonus * 0.5).toInt()).coerceAtLeast(2)
        return penalty
    }

    fun getPatienceFeedbackText(patience: Int, isAgentInvolved: Boolean): String {
        return when {
            patience >= 80 -> "Ban lãnh đạo CLB rất hào hứng và mong muốn đạt được thỏa thuận sớm."
            patience >= 60 -> "CLB lắng nghe thiện chí của bạn, nhưng đề nghị hai bên cùng nhượng bộ một phần."
            patience >= 40 -> "Đại diện CLB bắt đầu cau mày. Yêu cầu của bạn đang tiến gần đến trần ngân sách."
            patience >= 20 -> if (isAgentInvolved) {
                "Người đại diện cảnh báo: CLB sắp mất kiên nhẫn và có thể lập tức rời bàn đàm phán!"
            } else {
                "Ban lãnh đạo tỏ thái độ cứng rắn. Đây có thể là vòng đàm phán cuối cùng của họ."
            }
            else -> "CLB đã chính thức rút lại đề nghị do đôi bên không tìm được tiếng nói chung."
        }
    }

    // =========================================================================
    // 5. DANH SÁCH NGƯỜI ĐẠI DIỆN CÓ SẴN (PRESET AGENTS)
    // =========================================================================
    fun getAvailableAgents(): List<Agent> = listOf(
        Agent(
            id = "agent_none",
            name = "Gia đình / Tự đại diện",
            tier = AgentTier.NONE
        ),
        Agent(
            id = "agent_rookie_1",
            name = "Trần Minh Quang (Môi giới trẻ)",
            tier = AgentTier.ROOKIE
        ),
        Agent(
            id = "agent_exp_1",
            name = "Vũ Đình Trọng (Văn phòng Luật TT)",
            tier = AgentTier.EXPERIENCED
        ),
        Agent(
            id = "agent_elite_1",
            name = "Học viện Quản lý Thể thao Châu Á - Thái Bình Dương",
            tier = AgentTier.ELITE
        ),
        Agent(
            id = "agent_super_1",
            name = "Marco Bellini (Siêu đại diện Quốc tế)",
            tier = AgentTier.SUPER_AGENT
        )
    )
}

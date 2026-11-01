package com.example.roadtolegend.engine

import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.FinanceTransaction
import com.example.roadtolegend.model.MarketValuePoint
import com.example.roadtolegend.model.TransactionType
import java.util.UUID

object FinanceEngine {

    /**
     * Xử lý quyết toán thu chi tài chính định kỳ vào cuối tuần:
     * - Trả lương tuần theo hợp đồng (hoặc tỉ lệ chia lương nếu đang đi mượn)
     * - Khấu trừ phí duy trì người đại diện (Agent Retainer)
     * - Cập nhật điểm giá trị thị trường vào biểu đồ lịch sử
     */
    fun processWeeklyFinance(state: CareerState) {
        val player = state.player
        val contract = state.currentContract
        val agent = state.agent
        val loan = state.activeLoan

        // 1. Nhận lương tuần (trừ khi là cầu thủ tự do)
        val weeklyWage = if (contract.isFreeAgent) {
            0L
        } else if (loan != null) {
            // Đang đi mượn: CLB mượn trả theo tỉ lệ thỏa thuận
            (contract.weeklyWage * (loan.wageSharePercent / 100.0)).toLong().coerceAtLeast(100L)
        } else {
            contract.weeklyWage
        }

        if (weeklyWage > 0L) {
            val netWage = TransferConfig.calculateNetIncome(weeklyWage)
            val taxPaid = weeklyWage - netWage
            player.balance += netWage

            state.financeTransactions.add(
                0,
                FinanceTransaction(
                    id = UUID.randomUUID().toString(),
                    week = state.currentWeek,
                    season = state.seasonNumber,
                    type = TransactionType.WAGE,
                    amount = netWage,
                    balanceAfter = player.balance,
                    descriptionVi = if (loan != null) {
                        "Lương tuần từ ${loan.loanClubShortName} (${loan.wageSharePercent}% hợp đồng mượn${if (taxPaid > 0) ", sau thuế 10%" else ""})"
                    } else {
                        "Lương tuần chính thức từ ${state.currentClub.shortName}${if (taxPaid > 0) " (sau thuế 10%)" else ""}"
                    }
                )
            )
        }

        // 2. Trừ phí duy trì Agent nếu có
        if (agent.tier != AgentTier.NONE && agent.tier.weeklyRetainer > 0) {
            val retainer = agent.tier.weeklyRetainer
            player.balance = (player.balance - retainer).coerceAtLeast(0L)

            state.financeTransactions.add(
                0,
                FinanceTransaction(
                    id = UUID.randomUUID().toString(),
                    week = state.currentWeek,
                    season = state.seasonNumber,
                    type = TransactionType.AGENT_RETAINER,
                    amount = retainer,
                    balanceAfter = player.balance,
                    descriptionVi = "Phí duy trì dịch vụ người đại diện (${agent.name})"
                )
            )
        }

        // 3. Giảm số tuần hợp đồng còn lại (mỗi tuần trôi qua giảm 1 tuần)
        if (contract.totalWeeksRemaining > 0) {
            contract.totalWeeksRemaining--
            contract.contractYearsRemaining = (contract.totalWeeksRemaining / 26) + 1

            // Khi hợp đồng vừa chính thức hết hạn
            if (contract.totalWeeksRemaining <= 0) {
                if (contract.isPreContract && contract.preContractClubId != null) {
                    // Đã ký pre-contract trước -> chuyển sang CLB mới
                    val targetClub = (DefaultDatabase.createVLeague1Clubs() +
                            DefaultDatabase.createVLeague2Clubs() +
                            DefaultDatabase.createChampionshipClubs() +
                            DefaultDatabase.createPremierLeagueClubs()).find { it.id == contract.preContractClubId }
                    if (targetClub != null) {
                        state.currentClub = targetClub
                        if (targetClub.leagueId != state.currentLeague.id) {
                            state.currentLeague = DefaultDatabase.initializeLeague(targetClub.leagueId)
                        }
                        contract.clubId = targetClub.id
                        contract.clubName = targetClub.name
                        contract.totalWeeksRemaining = contract.contractYearsRemaining * 26
                        contract.isPreContract = false
                        contract.preContractClubId = null

                        player.addMilestoneIfNew(
                            "Gia nhập ${targetClub.shortName} dạng tự do",
                            "Hợp đồng cũ đáo hạn, chuyển sang CLB mới theo thỏa thuận ký trước.",
                            "Tuần ${state.currentWeek}",
                            "✍️"
                        )
                    }
                } else {
                    // Không ký mới, không có pre-contract -> Cầu thủ tự do (Free Agent)
                    contract.clubId = "free_agent"
                    contract.clubName = "Cầu thủ tự do"
                    contract.weeklyWage = 0L
                    contract.totalWeeksRemaining = 0
                    contract.contractYearsRemaining = 0

                    player.morale = (player.morale - 8).coerceAtLeast(20)
                    player.addMilestoneIfNew(
                        "Cầu thủ tự do",
                        "Hợp đồng chuyên nghiệp đáo hạn. Bạn trở thành cầu thủ tự do trên thị trường.",
                        "Tuần ${state.currentWeek}",
                        "🆓"
                    )

                    state.newsFeed.add(
                        0,
                        com.example.roadtolegend.model.NewsItem(
                            id = UUID.randomUUID().toString(),
                            week = state.currentWeek,
                            headline = "HẾT HẠN HỢP ĐỒNG: ${player.name} chính thức trở thành cầu thủ tự do!",
                            body = "Cầu thủ ${player.age} tuổi hiện không thuộc biên chế CLB nào và đang chờ đợi các lời mời chiêu mộ.",
                            category = "CONTRACT",
                            sourceName = "Chuyển Nhượng 24/7",
                            reliabilityTier = 1
                        )
                    )
                }
            }
        }

        // Giảm thời gian mượn nếu đang đi mượn
        if (loan != null) {
            loan.weeksRemaining--
            if (loan.weeksRemaining <= 0) {
                // Hết hạn mượn, quay về CLB mẹ
                val parentClub = DefaultDatabase.createVLeague2Clubs().find { it.id == loan.parentClubId }
                    ?: DefaultDatabase.createVLeague1Clubs().find { it.id == loan.parentClubId }
                    ?: state.currentClub

                state.currentClub = parentClub
                state.activeLoan = null
                player.addMilestoneIfNew(
                    "Hoàn tất thời gian cho mượn",
                    "Trở về ${parentClub.name} sau thời gian thi đấu cho mượn tại ${loan.loanClubName}.",
                    "Tuần ${state.currentWeek}",
                    "🔙"
                )
            }
        }

        // 4. Cập nhật lịch sử Giá trị chuyển nhượng (Market Value Chart)
        val currentMarketVal = TransferConfig.calculateMarketValue(player, contract.totalWeeksRemaining)
        state.marketHistory.add(
            MarketValuePoint(
                season = state.seasonNumber,
                week = state.currentWeek,
                value = currentMarketVal
            )
        )
    }
}

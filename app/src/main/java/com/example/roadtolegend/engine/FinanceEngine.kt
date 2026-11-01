package com.example.roadtolegend.engine

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

        // 1. Nhận lương tuần
        val weeklyWage = if (loan != null) {
            // Đang đi mượn: CLB mượn trả theo tỉ lệ thỏa thuận
            (contract.weeklyWage * (loan.wageSharePercent / 100.0)).toLong().coerceAtLeast(100L)
        } else {
            contract.weeklyWage
        }

        player.balance += weeklyWage

        state.financeTransactions.add(
            0,
            FinanceTransaction(
                id = UUID.randomUUID().toString(),
                week = state.currentWeek,
                season = state.seasonNumber,
                type = TransactionType.WAGE,
                amount = weeklyWage,
                balanceAfter = player.balance,
                descriptionVi = if (loan != null) {
                    "Lương tuần từ ${loan.loanClubShortName} (${loan.wageSharePercent}% theo hợp đồng mượn)"
                } else {
                    "Lương tuần chính thức từ ${state.currentClub.shortName}"
                }
            )
        )

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
